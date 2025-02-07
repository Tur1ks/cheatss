package net.minecraft.client.util;

import org.lwjgl.glfw.GLFWNativeWin32;
import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.win32.*;

public class WindowStyle {
    public interface DwmApi extends StdCallLibrary {
        DwmApi INSTANCE = Native.loadLibrary("dwmapi", DwmApi.class);
        int DwmSetWindowAttribute(HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);
    }

    private static final int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;
    private static final int DWMWA_SYSTEMBACKDROP_TYPE = 38;

    public static void setDarkMode(long windowHandle) {
        HWND hwndJna = getHwnd(windowHandle);
        setWindowAttribute(hwndJna, DWMWA_USE_IMMERSIVE_DARK_MODE, 1);
    }

    public static void setAcrylicEffect(long windowHandle) {
        HWND hwndJna = getHwnd(windowHandle);
        setWindowAttribute(hwndJna, DWMWA_SYSTEMBACKDROP_TYPE, 3);
    }


    public static void setTabbed(long windowHandle) {
        HWND hwndJna = getHwnd(windowHandle);
        setWindowAttribute(hwndJna, DWMWA_SYSTEMBACKDROP_TYPE, 4);
    }

    private static HWND getHwnd(long windowHandle) {
        long hwnd = GLFWNativeWin32.glfwGetWin32Window(windowHandle);
        return new HWND(new Pointer(hwnd));
    }

    private static void setWindowAttribute(HWND hwnd, int attribute, int value) {
        Memory memory = new Memory(4);
        memory.setInt(0, value);
        DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, attribute, memory, 4);
    }
}
