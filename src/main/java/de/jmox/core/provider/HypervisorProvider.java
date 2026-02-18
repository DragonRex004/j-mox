package de.jmox.core;

import java.util.List;

public interface HypervisorProvider {
    void startVM(String vmId);
    void stopVM(String vmId);
    List<String> listVMs();
    boolean isVMRunning(String vmId);
}
