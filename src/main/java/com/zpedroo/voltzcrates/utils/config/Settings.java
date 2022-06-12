package com.zpedroo.voltzcrates.utils.config;

import com.zpedroo.voltzcrates.utils.FileUtils;
import org.bukkit.ChatColor;

import java.util.List;

import static com.zpedroo.voltzcrates.utils.color.Colorize.getColored;

public class Settings {

    public static final String ADMIN_CMD = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.commands.admin.command");

    public static final List<String> ADMIN_CMD_ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.commands.admin.aliases");

    public static final String ADMIN_CMD_PERMISSION = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.commands.admin.permission");

    public static final String ADMIN_CMD_PERMISSION_MESSAGE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.commands.admin.permission-message"));

    public static final String COLLECT_CMD = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.commands.collect.command");

    public static final List<String> COLLECT_CMD_ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.commands.collect.aliases");

    public static final int WARN_KEYS_TIME = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.pending-keys-warn-every");
}