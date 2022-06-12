package com.zpedroo.voltzcrates.commands;

import com.zpedroo.voltzcrates.managers.DataManager;
import com.zpedroo.voltzcrates.objects.player.PlayerData;
import com.zpedroo.voltzcrates.utils.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CollectCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        collectPlayerKeys(player);
        return false;
    }

    private void collectPlayerKeys(Player player) {
        PlayerData data = DataManager.getInstance().getPlayerData(player);
        if (!data.hasPendingKeys()) {
            player.sendMessage(Messages.ZERO_KEYS);
            return;
        }

        data.collectAllKeys(player);
    }
}