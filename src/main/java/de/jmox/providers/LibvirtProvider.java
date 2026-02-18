package de.jmox.providers;

import de.jmox.core.provider.HypervisorProvider;

import java.util.List;

public class LibvirtProvider implements HypervisorProvider {
    @Override
    public void startVM(String vmId) {

    }

    @Override
    public void stopVM(String vmId) {

    }

    @Override
    public List<String> listVMs() {
        return List.of();
    }

    @Override
    public boolean isVMRunning(String vmId) {
        return false;
    }
}
