package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.slf4j.Logger;

public class ScreenManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Long2ObjectMap<Monitor> monitors = new Long2ObjectOpenHashMap<>();
    private final MonitorCreator monitorCreator;

    public ScreenManager(MonitorCreator pMonitorCreator) {
        this.monitorCreator = pMonitorCreator;
        GLFW.glfwSetMonitorCallback(this::onMonitorChange);
        PointerBuffer pointerbuffer = GLFW.glfwGetMonitors();
        if (pointerbuffer != null) {
            for (int i = 0; i < pointerbuffer.limit(); i++) {
                long j = pointerbuffer.get(i);
                this.monitors.put(j, pMonitorCreator.createMonitor(j));
            }
        }
    }

    private void onMonitorChange(long p_85274_, int p_85275_) {
        RenderSystem.assertOnRenderThread();
        if (p_85275_ == 262145) {
            this.monitors.put(p_85274_, this.monitorCreator.createMonitor(p_85274_));
            LOGGER.debug("Monitor {} connected. Current monitors: {}", p_85274_, this.monitors);
        } else if (p_85275_ == 262146) {
            this.monitors.remove(p_85274_);
            LOGGER.debug("Monitor {} disconnected. Current monitors: {}", p_85274_, this.monitors);
        }
    }

    @Nullable
    public Monitor getMonitor(long pMonitorID) {
        return this.monitors.get(pMonitorID);
    }

    @Nullable
    public Monitor findBestMonitor(Window pWindow) {
        long i = GLFW.glfwGetWindowMonitor(pWindow.getWindow());
        if (i != 0L) {
            return this.getMonitor(i);
        } else {
            int j = pWindow.getX();
            int k = j + pWindow.getScreenWidth();
            int l = pWindow.getY();
            int i1 = l + pWindow.getScreenHeight();
            int j1 = -1;
            Monitor monitor = null;
            long k1 = GLFW.glfwGetPrimaryMonitor();
            LOGGER.debug("Selecting monitor - primary: {}, current monitors: {}", k1, this.monitors);

            for (Monitor monitor1 : this.monitors.values()) {
                int l1 = monitor1.getX();
                int i2 = l1 + monitor1.getCurrentMode().getWidth();
                int j2 = monitor1.getY();
                int k2 = j2 + monitor1.getCurrentMode().getHeight();
                int l2 = clamp(j, l1, i2);
                int i3 = clamp(k, l1, i2);
                int j3 = clamp(l, j2, k2);
                int k3 = clamp(i1, j2, k2);
                int l3 = Math.max(0, i3 - l2);
                int i4 = Math.max(0, k3 - j3);
                int j4 = l3 * i4;
                if (j4 > j1) {
                    monitor = monitor1;
                    j1 = j4;
                } else if (j4 == j1 && k1 == monitor1.getMonitor()) {
                    LOGGER.debug("Primary monitor {} is preferred to monitor {}", monitor1, monitor);
                    monitor = monitor1;
                }
            }

            LOGGER.debug("Selected monitor: {}", monitor);
            return monitor;
        }
    }

    public static int clamp(int pValue, int pMin, int pMax) {
        if (pValue < pMin) {
            return pMin;
        } else {
            return pValue > pMax ? pMax : pValue;
        }
    }

    public void shutdown() {
        RenderSystem.assertOnRenderThread();
        GLFWMonitorCallback glfwmonitorcallback = GLFW.glfwSetMonitorCallback(null);
        if (glfwmonitorcallback != null) {
            glfwmonitorcallback.free();
        }
    }
}