package com.zpedroo.voltzcrates.listeners;

import com.zpedroo.voltzcrates.managers.CrateManager;
import com.zpedroo.voltzcrates.managers.DataManager;
import com.zpedroo.voltzcrates.objects.crate.PlacedCrate;
import com.zpedroo.voltzcrates.utils.config.Messages;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        DataManager.getInstance().savePlayerData(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        PlacedCrate crate = DataManager.getInstance().getPlacedCrateByLocation(block.getLocation());
        if (crate == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(Messages.NEED_KEY);
            return;
        }

        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("CrateKey") || !StringUtils.equals(nbt.getString("CrateKey"), crate.getCrate().getName())) {
            player.sendMessage(Messages.NEED_KEY);
            return;
        }

        int amountToOpen = player.isSneaking() ? item.getAmount() : 1;
        takeItems(player, item, amountToOpen);

        CrateManager.getInstance().open(player, crate, amountToOpen);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        PlacedCrate crate = DataManager.getInstance().getPlacedCrateByLocation(event.getBlock().getLocation());
        if (crate == null) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().equals(Material.AIR)) return;

        NBTItem nbt = new NBTItem(event.getItemInHand());
        if (!nbt.hasKey("CrateKey")) return;

        event.setCancelled(true);
    }

    private void takeItems(Player player, ItemStack toTake, Integer amount) {
        int remaining = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || !item.isSimilar(toTake)) continue;

            if (item.getAmount() > remaining) {
                item.setAmount(item.getAmount() - remaining);
                break;
            }

            remaining -= item.getAmount();
            player.getInventory().removeItem(item);
        }

        player.updateInventory();
    }
}