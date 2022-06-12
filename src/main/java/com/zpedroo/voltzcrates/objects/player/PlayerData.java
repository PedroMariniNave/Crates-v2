package com.zpedroo.voltzcrates.objects.player;

import com.zpedroo.voltzcrates.objects.crate.Crate;
import com.zpedroo.voltzcrates.utils.config.Messages;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Data
public class PlayerData {

    private final UUID uuid;
    private final Map<Crate, Integer> pendingKeys;
    private boolean update;

    public boolean hasPendingKeys() {
        return !pendingKeys.isEmpty();
    }

    public int getPendingKeysAmount(Crate crate) {
        return pendingKeys.getOrDefault(crate, 0);
    }

    public int getTotalPendingKeysAmount() {
        int ret = 0;
        for (int amount : pendingKeys.values()) {
            ret += amount;
        }

        return ret;
    }

    public void addPendingKey(Crate crate, int amount) {
        this.pendingKeys.put(crate, getPendingKeysAmount(crate) + amount);
        this.update = true;
    }

    public void collectAllKeys(Player player) {
        new HashSet<>(pendingKeys.keySet()).forEach(crate -> collectKeys(player, crate));
    }

    public void collectKeys(Player player, Crate crate) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Messages.NEED_SPACE_KEY);
            return;
        }

        int amount = pendingKeys.remove(crate);
        ItemStack item = crate.getKeyItem(amount);

        player.getInventory().addItem(item);
        setUpdate(true);
    }
}