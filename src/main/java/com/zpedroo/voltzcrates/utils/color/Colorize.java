package com.zpedroo.voltzcrates.utils.color;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Colorize {

    public static String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static List<String> getColored(List<String> list) {
        List<String> colored = new ArrayList<>();
        for (String str : list) {
            colored.add(getColored(str));
        }

        return colored;
    }
}