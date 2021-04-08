package com.wkclz.core.util;


import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.pojo.dto.JvmInfo;

import java.lang.management.*;
import java.util.List;

public class JvmUtil {

    public static void main(String[] args) {
        JvmInfo jvmStatus = getJvmStatus();
        System.out.println(jvmStatus);
    }

    public static JvmInfo getJvmStatus() {

        JvmInfo info = new JvmInfo();

        // MemoryMXBean 获取整个虚拟机内存使用情况
        MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage usage = memorymbean.getHeapMemoryUsage();
        info.setInit(usage.getInit());
        info.setMax(usage.getMax());
        info.setUsed(usage.getUsed());
        info.setCommitted(usage.getCommitted());
        info.setHeapMemoryUsage(memorymbean.getHeapMemoryUsage().toString());
        info.setNonHeapMemoryUsage(memorymbean.getNonHeapMemoryUsage().toString());

        // java option
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        info.setJavaOptions(inputArguments);

        // 通过java来获取相关系统状态
        info.setTotalMemory(Runtime.getRuntime().totalMemory());
        info.setFreeMemory(Runtime.getRuntime().freeMemory());
        info.setMaxMemory(Runtime.getRuntime().maxMemory());

        // OperatingSystemMXBean
        com.sun.management.OperatingSystemMXBean osm = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        info.setGetFreeSwapSpaceSize(osm.getFreeSwapSpaceSize());
        info.setFreePhysicalMemorySize(osm.getFreePhysicalMemorySize());
        info.setTotalPhysicalMemorySize(osm.getTotalPhysicalMemorySize());

        // 获取操作系统相关信息
        info.setArch(osm.getArch());
        info.setAvailableProcessors(osm.getAvailableProcessors());
        info.setCommittedVirtualMemorySize(osm.getCommittedVirtualMemorySize());
        info.setOsName(osm.getName());
        info.setProcessCpuTime(osm.getProcessCpuTime());
        info.setVersion(osm.getVersion());

        // ThreadMXBean 获取各个线程的各种状态，CPU 占用情况，以及整个系统中的线程状况
        ThreadMXBean tm = ManagementFactory.getThreadMXBean();
        info.setThreadCount(tm.getThreadCount());
        info.setPeakThreadCount(tm.getPeakThreadCount());
        info.setCurrentThreadCpuTime(tm.getCurrentThreadCpuTime());
        info.setDaemonThreadCount(tm.getDaemonThreadCount());
        info.setCurrentThreadUserTime(tm.getCurrentThreadUserTime());

        // CompilationMXBean 当前编译器情况
        CompilationMXBean gm = ManagementFactory.getCompilationMXBean();
        info.setName(gm.getName());
        info.setTotalCompilationTime(gm.getTotalCompilationTime());

        /*
        // MemoryPoolMXBean 获取多个内存池的使用情况
        List<MemoryPoolMXBean> mpmList = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean mpm : mpmList) {
            System.out.println("getUsage " + mpm.getUsage());
            System.out.println("getMemoryManagerNames " + mpm.getMemoryManagerNames().toString());
        }
        // MemoryPoolMXBean 获取GC的次数以及花费时间之类的信息=
        List<GarbageCollectorMXBean> gcmList = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcm : gcmList) {
            System.out.println("getName " + gcm.getName());
            System.out.println("getMemoryPoolNames " + gcm.getMemoryPoolNames());
        }
        */

        // RuntimeMXBean 获取运行时信息
        RuntimeMXBean rmb = ManagementFactory.getRuntimeMXBean();
        // info.setClassPath(rmb.getClassPath());
        // info.setLibraryPath(rmb.getLibraryPath());
        info.setVmVersion(rmb.getVmVersion());

        return info;

    }

}
