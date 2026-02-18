package de.jmox;

import de.jmox.api.VMController;
import de.jmox.core.database.DatabaseManager;
import de.jmox.core.provider.HypervisorManager;
import de.jmox.core.stats.StatsManager;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Map;

public class App {
    private DatabaseManager databaseManager;
    void main() {
        databaseManager = new DatabaseManager();
        databaseManager.init();

        HypervisorManager hvManager = new HypervisorManager();
        VMController vmController = new VMController(hvManager.getProvider());
        StatsManager statsManager = new StatsManager();

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.showJavalinBanner = false;
        }).start(8080);

        app.get("/api/vms", vmController::getAllVMs);
        app.post("/api/vms/{id}/start", vmController::startVM);
        app.post("/api/vms/{id}/stop", vmController::stopVM);
        app.get("/api/stats", ctx -> ctx.json(statsManager.getSystemMetrics()));

        app.post("/api/login", ctx -> {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            if (databaseManager.checkLogin(body.get("user"), body.get("password"))) {
                ctx.sessionAttribute("logged-in", "true");
                ctx.status(200);
            } else {
                ctx.status(401);
            }
        });

        app.post("/api/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.status(200).result("Successful logout");
        });

        app.before("/api/*", ctx -> {
            if (ctx.path().equals("/api/login")) return;
            if (ctx.sessionAttribute("logged-in") == null) {
                throw new UnauthorizedResponse();
            }
        });

        app.get("/api/users", ctx -> ctx.json(databaseManager.getAllUsernames()));

        app.delete("/api/users/{name}", ctx -> {
            databaseManager.deleteUser(ctx.pathParam("name"));
            ctx.status(200);
        });

        app.patch("/api/users/{name}", ctx -> {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            databaseManager.updateUserPassword(ctx.pathParam("name"), body.get("password"));
            ctx.status(200);
        });

        startCLI(app);
    }

    private void startCLI(Javalin app) {
        new Thread(() -> {
            try {
                Terminal terminal = TerminalBuilder.builder().system(true).build();
                LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

                System.out.println("=== J-Mox Admin Console started ===");
                System.out.println("Commands: adduser <name> <pass>, stop, help");

                while (true) {
                    String line = reader.readLine("j-mox> ");
                    if (line == null) continue;
                    line = line.trim();

                    if (line.equalsIgnoreCase("stop") || line.equalsIgnoreCase("exit")) {
                        System.out.println("Shutdown J-Mox...");
                        app.stop();
                        System.exit(0);
                    } else if (line.startsWith("adduser ")) {
                        handleAddUser(line);
                    } else if (line.equalsIgnoreCase("help")) {
                        System.out.println("available Commands: vms, adduser, stop, exit");
                    } else {
                        System.out.println("Unknown Command. use 'help' for help.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleAddUser(String line) {
        String[] parts = line.split(" ");
        if (parts.length == 3) {
            String user = parts[1];
            String pass = parts[2];
            databaseManager.createUser(user, pass);
            System.out.println("User '" + user + "' successful created with BCrypt.");
        } else {
            System.out.println("Syntax: adduser <username> <password>");
        }
    }
}
