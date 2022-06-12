package com.zpedroo.voltzcrates.objects.crate;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

@Data
public class Crate {

    private final File file;
    private final String name;
    private final ItemStack keyItem;
    private final String[] hologramLines;
    private final List<Reward> rewards;

    public ItemStack getKeyItem() {
        return getKeyItem(1);
    }

    public ItemStack getKeyItem(int amount) {
        NBTItem nbt = new NBTItem(keyItem.clone());
        nbt.setString("CrateKey", name);
        nbt.getItem().setAmount(amount);

        return nbt.getItem();
    }
}