package de.jmox.core;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import java.util.Map;

public class StatsManager {
    private final SystemInfo si = new SystemInfo();
    private final CentralProcessor processor = si.getHardware().getProcessor();
    private final GlobalMemory memory = si.getHardware().getMemory();
    private long[] prevTicks = new long[CentralProcessor.TickType.values().length];

    public Map<String, Object> getSystemMetrics() {
        // CPU Last berechnen (Differenz zwischen zwei Zeitpunkten)
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = processor.getSystemCpuLoadTicks();

        long totalMem = memory.getTotal();
        long availableMem = memory.getAvailable();
        double memUsedPercent = ((double) (totalMem - availableMem) / totalMem) * 100;

        return Map.of(
                "cpu", String.format("%.1f", cpuLoad),
                "ram", String.format("%.1f", memUsedPercent),
                "uptime", si.getOperatingSystem().getSystemUptime()
        );
    }
}
