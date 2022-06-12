package com.zpedroo.voltzcrates.tasks;

import com.zpedroo.voltzcrates.VoltzCrates;
import com.zpedroo.voltzcrates.objects.crate.PlacedCrate;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class ShowRewardsTask extends BukkitRunnable {

    private final PlacedCrate crate;
    private Item item;
    private Integer rewardId;

    private Long stopTask;

    public ShowRewardsTask(PlacedCrate crate) {
        this.crate = crate;
        this.rewardId = -1;
        this.stopTask = 0L;
        this.runTaskTimerAsynchronously(VoltzCrates.get(), 20L, 20L);
    }

    @Override
    public void run() {
        if (stopTask > System.currentTimeMillis()) return;
        if (++rewardId >= crate.getCrate().getRewards().size()) rewardId = 0;

        ItemStack display = crate.getCrate().getRewards().get(rewardId).getDisplay();

        if (item == null || item.isDead()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    item = crate.getLocation().getWorld().dropItem(crate.getLocation().clone().add(0.5D, 1D, 0.5D), display);
                    item.setVelocity(new Vector(0, 0.1, 0));
                    item.setPickupDelay(Integer.MAX_VALUE);
                    item.setMetadata("RewardItem", new FixedMetadataValue(VoltzCrates.get(), true));

                    updateItem(display);
                }
            }.runTaskLater(VoltzCrates.get(), 0L);
            return;
        }

        updateItem(display);
    }

    public void showItem(ItemStack item, long timeInSeconds) {
        this.updateItem(item);
        this.stopTask = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeInSeconds);
    }

    private void updateItem(ItemStack itemStack) {
        if (item == null) return;

        item.setItemStack(itemStack);
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            item.setCustomName(itemStack.getItemMeta().getDisplayName());
            item.setCustomNameVisible(true);
        }
    }

    public Item getItem() {
        return item;
    }
}