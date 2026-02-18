package de.jmox.providers;

import de.jmox.core.provider.HypervisorProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VBoxProvider implements HypervisorProvider {
    @Override
    public void startVM(String vmId) {
        this.execute("startvm", vmId, "--type", "headless");
    }

    @Override
    public void stopVM(String vmId) {
        this.execute("controlvm", vmId, "savestate");
    }

    @Override
    public List<String> listVMs() {
        try {
            List<String> vms = new ArrayList<>();
            ProcessBuilder pb = new ProcessBuilder("VBoxManage", "list", "vms");
            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int firstQuote = line.indexOf('"');
                    int lastQuote = line.lastIndexOf('"');
                    if (firstQuote != -1 && lastQuote != -1 && firstQuote != lastQuote) {
                        vms.add(line.substring(firstQuote + 1, lastQuote));
                    }
                }
            }
            return vms;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isVMRunning(String vmId) {
        try {
            ProcessBuilder pb = new ProcessBuilder("VBoxManage", "showvminfo", vmId, "--machinereadable");
            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("VMState=")) {
                        return line.contains("running");
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void execute(String... args) {
        try {
            List<String> command = new ArrayList<>();
            command.add("VBoxManage");
            command.addAll(List.of(args));

            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("VBoxManage Error. Exit Code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
