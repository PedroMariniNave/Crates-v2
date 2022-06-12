package com.zpedroo.voltzcrates.utils.config;

import com.zpedroo.voltzcrates.utils.FileUtils;

import java.util.List;

import static com.zpedroo.voltzcrates.utils.color.Colorize.getColored;

public class Messages {

    public static final List<String> COMMAND_USAGE = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.command-usage"));

    public static final List<String> PENDING_KEYS_WARN = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.pending-keys-warn"));

    public static final String NULL_BLOCK = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.null-block"));

    public static final String NULL_CRATE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.null-crate"));

    public static final String INVALID_CRATE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.invalid-crate"));

    public static final String INVALID_AMOUNT = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.invalid-amount"));

    public static final String EXISTING_CRATE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.existing-crate"));

    public static final String OFFLINE_PLAYER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.offline-player"));

    public static final String NEED_KEY = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.need-key"));

    public static final String NEED_SPACE_CRATE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.need-space-crate"));

    public static final String NEED_SPACE_KEY = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.need-space-key"));

    public static final String GIVE_ALL = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.give-all"));

    public static final String ZERO_KEYS = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.zero-keys"));

    public static final String NEW_KEYS = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.new-keys"));
}