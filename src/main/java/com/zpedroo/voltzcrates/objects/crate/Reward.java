package com.zpedroo.voltzcrates.objects.crate;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class Reward {

    private final double chance;
    private final ItemStack display;
    private final ItemStack itemToGive;
    private final List<String> commands;

    public ItemStack getDisplay() {
        return display.clone();
    }

    public ItemStack getItemToGive() {
        if (itemToGive == null) return null; // fix null clone

        return itemToGive.clone();
    }
}