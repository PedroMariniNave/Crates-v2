package com.zpedroo.voltzcrates.commands;

import com.zpedroo.voltzcrates.managers.CrateManager;
import com.zpedroo.voltzcrates.managers.DataManager;
import com.zpedroo.voltzcrates.objects.crate.Crate;
import com.zpedroo.voltzcrates.objects.crate.PlacedCrate;
import com.zpedroo.voltzcrates.objects.player.PlayerData;
import com.zpedroo.voltzcrates.utils.config.Messages;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class CratesCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;
        if (args.length > 0) {
            Block block = null;
            Location location = null;
            Crate crate = null;
            PlacedCrate placedCrate = null;
            int amount = 0;
            switch (args[0].toUpperCase()) {
                case "REMOVE":
                    if (player == null) break;

                    block = player.getTargetBlock((HashSet<Byte>) null, 5);
                    if (block.getType().equals(Material.AIR)) {
                        player.sendMessage(Messages.NULL_BLOCK);
                        return true;
                    }

                    location = block.getLocation();
                    placedCrate = DataManager.getInstance().getPlacedCrateByLocation(location);
                    if (placedCrate == null) {
                        player.sendMessage(Messages.NULL_CRATE);
                        return true;
                    }

                    CrateManager.getInstance().delete(placedCrate);
                    return true;
                case "SET":
                    if (player == null || args.length < 2) break;

                    block = player.getTargetBlock((HashSet<Byte>) null, 5);
                    if (block.getType().equals(Material.AIR)) {
                        player.sendMessage(Messages.NULL_BLOCK);
                        return true;
                    }

                    location = block.getLocation();
                    placedCrate = DataManager.getInstance().getPlacedCrateByLocation(location);
                    if (placedCrate != null) {
                        player.sendMessage(Messages.EXISTING_CRATE);
                        return true;
                    }

                    crate = DataManager.getInstance().getCrateByName(args[1]);
                    if (crate == null) {
                        player.sendMessage(Messages.INVALID_CRATE);
                        return true;
                    }

                    CrateManager.getInstance().create(location, crate);
                    return true;
                case "KEY":
                case "GIVE":
                    if (args.length < 4) break;

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(Messages.OFFLINE_PLAYER);
                        return true;
                    }

                    crate = DataManager.getInstance().getCrateByName(args[2]);
                    if (crate == null) {
                        sender.sendMessage(Messages.INVALID_CRATE);
                        return true;
                    }

                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (Exception ex) {
                        // ignore
                    }

                    if (amount <= 0) {
                        sender.sendMessage(Messages.INVALID_AMOUNT);
                        return true;
                    }

                    giveKeys(target, crate, amount, false);
                    return true;
                case "GIVEALL":
                    if (args.length < 3) break;

                    crate = DataManager.getInstance().getCrateByName(args[1]);
                    if (crate == null) {
                        sender.sendMessage(Messages.INVALID_CRATE);
                        return true;
                    }

                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (Exception ex) {
                        // ignore
                    }

                    if (amount <= 0) {
                        sender.sendMessage(Messages.INVALID_AMOUNT);
                        return true;
                    }

                    final int finalAmount = amount;
                    final Crate finalCrate = crate;
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                        giveKeys(onlinePlayer, finalCrate, finalAmount, true);
                    });
                    return true;
            }
        }

        for (String msg : Messages.COMMAND_USAGE) {
            sender.sendMessage(StringUtils.replaceEach(msg, new String[]{
                    "{cmd}"
            }, new String[]{
                    label
            }));
        }
        return false;
    }

    private void giveKeys(Player player, Crate crate, int amount, boolean keyAll) {
        PlayerData data = DataManager.getInstance().getPlayerData(player);
        data.addPendingKey(crate, amount);

        ItemStack item = crate.getKeyItem();
        String[] placeholders = new String[]{
                "{amount}",
                "{key}"
        };
        String[] replacers = new String[]{
                String.valueOf(amount),
                item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : crate.getName() : crate.getName()
        };

        if (keyAll) {
            player.sendMessage(StringUtils.replaceEach(Messages.GIVE_ALL, placeholders, replacers));
        }

        player.sendMessage(StringUtils.replaceEach(Messages.NEW_KEYS, placeholders, replacers));
    }
}