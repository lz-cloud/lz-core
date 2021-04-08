package com.wkclz.core.pojo.dto;

import java.util.List;

public class JvmInfo {

    private Long init;
    private Long used;
    private Long committed;
    private Long max;
    private String HeapMemoryUsage;
    private String nonHeapMemoryUsage;
    private List<String> javaOptions;

    // Java 虚拟机中的内存总量,以字节为单位
    private Long totalMemory;
    // Java 虚拟机中的空闲内存量
    private Long freeMemory;
    // Java 虚拟机中的最大内存量
    private Long maxMemory;

    private Long getFreeSwapSpaceSize;
    private Long freePhysicalMemorySize;
    private Long totalPhysicalMemorySize;

    private String arch;
    private Integer availableProcessors;
    private Long committedVirtualMemorySize;
    private String osName;
    private Long processCpuTime;
    private String version;

    private Integer threadCount;
    private int peakThreadCount;
    private Long currentThreadCpuTime;
    private Integer daemonThreadCount;
    private Long currentThreadUserTime;


    private String name;
    private Long totalCompilationTime;


    private String classPath;
    private String libraryPath;
    private String vmVersion;


    public Long getInit() {
        return init;
    }

    public void setInit(Long init) {
        this.init = init;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getCommitted() {
        return committed;
    }

    public void setCommitted(Long committed) {
        this.committed = committed;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public String getHeapMemoryUsage() {
        return HeapMemoryUsage;
    }

    public void setHeapMemoryUsage(String heapMemoryUsage) {
        HeapMemoryUsage = heapMemoryUsage;
    }

    public String getNonHeapMemoryUsage() {
        return nonHeapMemoryUsage;
    }

    public void setNonHeapMemoryUsage(String nonHeapMemoryUsage) {
        this.nonHeapMemoryUsage = nonHeapMemoryUsage;
    }

    public List<String> getJavaOptions() {
        return javaOptions;
    }

    public void setJavaOptions(List<String> javaOptions) {
        this.javaOptions = javaOptions;
    }

    public Long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(Long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public Long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(Long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public Long getGetFreeSwapSpaceSize() {
        return getFreeSwapSpaceSize;
    }

    public void setGetFreeSwapSpaceSize(Long getFreeSwapSpaceSize) {
        this.getFreeSwapSpaceSize = getFreeSwapSpaceSize;
    }

    public Long getFreePhysicalMemorySize() {
        return freePhysicalMemorySize;
    }

    public void setFreePhysicalMemorySize(Long freePhysicalMemorySize) {
        this.freePhysicalMemorySize = freePhysicalMemorySize;
    }

    public Long getTotalPhysicalMemorySize() {
        return totalPhysicalMemorySize;
    }

    public void setTotalPhysicalMemorySize(Long totalPhysicalMemorySize) {
        this.totalPhysicalMemorySize = totalPhysicalMemorySize;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public Integer getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(Integer availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public Long getCommittedVirtualMemorySize() {
        return committedVirtualMemorySize;
    }

    public void setCommittedVirtualMemorySize(Long committedVirtualMemorySize) {
        this.committedVirtualMemorySize = committedVirtualMemorySize;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public Long getProcessCpuTime() {
        return processCpuTime;
    }

    public void setProcessCpuTime(Long processCpuTime) {
        this.processCpuTime = processCpuTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }

    public Long getCurrentThreadCpuTime() {
        return currentThreadCpuTime;
    }

    public void setCurrentThreadCpuTime(Long currentThreadCpuTime) {
        this.currentThreadCpuTime = currentThreadCpuTime;
    }

    public Integer getDaemonThreadCount() {
        return daemonThreadCount;
    }

    public void setDaemonThreadCount(Integer daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }

    public Long getCurrentThreadUserTime() {
        return currentThreadUserTime;
    }

    public void setCurrentThreadUserTime(Long currentThreadUserTime) {
        this.currentThreadUserTime = currentThreadUserTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalCompilationTime() {
        return totalCompilationTime;
    }

    public void setTotalCompilationTime(Long totalCompilationTime) {
        this.totalCompilationTime = totalCompilationTime;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public void setLibraryPath(String libraryPath) {
        this.libraryPath = libraryPath;
    }

    public String getVmVersion() {
        return vmVersion;
    }

    public void setVmVersion(String vmVersion) {
        this.vmVersion = vmVersion;
    }

    @Override
    public String toString() {
        return "JvmInfo{" +
            "init=" + init +
            ", used=" + used +
            ", committed=" + committed +
            ", max=" + max +
            ", HeapMemoryUsage='" + HeapMemoryUsage + '\'' +
            ", nonHeapMemoryUsage='" + nonHeapMemoryUsage + '\'' +
            ", javaOptions=" + javaOptions +
            ", totalMemory=" + totalMemory +
            ", freeMemory=" + freeMemory +
            ", maxMemory=" + maxMemory +
            ", getFreeSwapSpaceSize=" + getFreeSwapSpaceSize +
            ", freePhysicalMemorySize=" + freePhysicalMemorySize +
            ", totalPhysicalMemorySize=" + totalPhysicalMemorySize +
            ", arch='" + arch + '\'' +
            ", availableProcessors=" + availableProcessors +
            ", committedVirtualMemorySize=" + committedVirtualMemorySize +
            ", osName='" + osName + '\'' +
            ", processCpuTime=" + processCpuTime +
            ", version='" + version + '\'' +
            ", threadCount=" + threadCount +
            ", peakThreadCount=" + peakThreadCount +
            ", currentThreadCpuTime=" + currentThreadCpuTime +
            ", daemonThreadCount=" + daemonThreadCount +
            ", currentThreadUserTime=" + currentThreadUserTime +
            ", name='" + name + '\'' +
            ", totalCompilationTime=" + totalCompilationTime +
            ", classPath='" + classPath + '\'' +
            ", libraryPath='" + libraryPath + '\'' +
            ", vmVersion='" + vmVersion + '\'' +
            '}';
    }
}
