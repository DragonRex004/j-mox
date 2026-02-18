package de.jmox.api;

import de.jmox.core.provider.HypervisorProvider;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class VMController {
    private final HypervisorProvider provider;

    public VMController(HypervisorProvider provider) {
        this.provider = provider;
    }

    public void startVM(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            provider.startVM(id);
            ctx.status(200).result("VM " + id + " started.");
        } catch (Exception e) {
            ctx.status(500).result("Error: " + e.getMessage());
        }
    }

    public void stopVM(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            provider.stopVM(id);
            ctx.status(200).result("VM " + id + " stopped.");
        } catch (Exception e) {
            ctx.status(500).result("Error: " + e.getMessage());
        }
    }

    public void getAllVMs(Context ctx) throws Exception {
        List<String> names = provider.listVMs();
        List<VMInfo> infos = new ArrayList<>();

        for (String name : names) {
            infos.add(new VMInfo(name, provider.isVMRunning(name)));
        }
        ctx.json(infos);
    }
}
