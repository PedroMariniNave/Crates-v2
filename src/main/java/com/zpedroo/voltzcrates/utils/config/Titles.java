package com.zpedroo.voltzcrates.utils.config;

import com.zpedroo.voltzcrates.utils.FileUtils;
import org.bukkit.ChatColor;

public class Titles {

    public static final String[] REWARD = new String[] {
            getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.reward.title")),
            getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.reward.subtitle"))
    };

    private static String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}