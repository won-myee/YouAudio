package com.youaudio;

public class HojaCheckOS {

    public static String osName = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return osName.contains("win");
    }

    public static boolean isMacOS() {
        return osName.contains("mac");
    }

    public static boolean isLinux() {
        return osName.contains("nix") || osName.contains("nux") || osName.contains("aix");
    }
}
