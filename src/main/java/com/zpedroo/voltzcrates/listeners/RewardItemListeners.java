package com.zpedroo.voltzcrates.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;

public class RewardItemListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDespawn(ItemDespawnEvent event) {
        if (!event.getEntity().hasMetadata("RewardItem")) return;

        event.setCancelled(true);
    }
}