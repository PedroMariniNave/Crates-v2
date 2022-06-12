package com.zpedroo.voltzcrates;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.zpedroo.voltzcrates.commands.CollectCmd;
import com.zpedroo.voltzcrates.commands.CratesCmd;
import com.zpedroo.voltzcrates.hooks.ProtocolLibHook;
import com.zpedroo.voltzcrates.listeners.PlayerGeneralListeners;
import com.zpedroo.voltzcrates.listeners.RewardItemListeners;
import com.zpedroo.voltzcrates.managers.CrateManager;
import com.zpedroo.voltzcrates.managers.DataManager;
import com.zpedroo.voltzcrates.tasks.WarnTask;
import com.zpedroo.voltzcrates.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

import static com.zpedroo.voltzcrates.utils.config.Settings.*;

public class VoltzCrates extends JavaPlugin {

    private static VoltzCrates instance;
    public static VoltzCrates get() { return instance; }

    public void onEnable() {
        instance = this;
        new FileUtils(this);
        new DataManager();
        new CrateManager();
        new WarnTask(this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new ProtocolLibHook(this, PacketType.Play.Client.LOOK));

        registerListeners();
        registerCommands();
    }

    public void onDisable() {
        DataManager.getInstance().saveAllPlayersData();
        DataManager.getInstance().getCache().getPlacedCrates().values().forEach(crate -> {
            crate.getShowRewardsTask().getItem().remove();
            crate.getShowRewardsTask().cancel();
            crate.getHologram().remove();
        });
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerGeneralListeners(), this);
        getServer().getPluginManager().registerEvents(new RewardItemListeners(), this);
    }

    private void registerCommands() {
        registerCommand(ADMIN_CMD, ADMIN_CMD_ALIASES, ADMIN_CMD_PERMISSION, ADMIN_CMD_PERMISSION_MESSAGE, new CratesCmd());
        registerCommand(COLLECT_CMD, COLLECT_CMD_ALIASES, null, null, new CollectCmd());
    }

    private void registerCommand(String command, List<String> aliases, String permission, String permissionMessage, CommandExecutor executor) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCmd = constructor.newInstance(command, this);
            pluginCmd.setAliases(aliases);
            pluginCmd.setExecutor(executor);
            if (permission != null) pluginCmd.setPermission(permission);
            if (permissionMessage != null) pluginCmd.setPermissionMessage(permissionMessage);

            Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            commandMap.register(getName().toLowerCase(), pluginCmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}