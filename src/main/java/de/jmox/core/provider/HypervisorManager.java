package de.jmox.core;

import de.jmox.providers.LibvirtProvider;
import de.jmox.providers.VBoxProvider;

public class HypervisorManager {
    private final HypervisorProvider provider;

    public HypervisorManager() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.out.println("[System] Windows detected. Use VirtualBox Provider.");
            this.provider = new VBoxProvider();
        } else {
            System.out.println("[System] Linux detected. Use Libvirt Provider.");
            this.provider = new LibvirtProvider();
        }
    }

    public HypervisorProvider getProvider() {
        return this.provider;
    }
}
