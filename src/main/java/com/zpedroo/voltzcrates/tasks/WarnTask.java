package com.zpedroo.voltzcrates.tasks;

import com.zpedroo.voltzcrates.managers.DataManager;
import com.zpedroo.voltzcrates.objects.player.PlayerData;
import com.zpedroo.voltzcrates.utils.config.Messages;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static com.zpedroo.voltzcrates.utils.config.Settings.WARN_KEYS_TIME;

public class WarnTask extends BukkitRunnable {

    public WarnTask(Plugin plugin) {
        this.runTaskTimerAsynchronously(plugin, WARN_KEYS_TIME * 20L, WARN_KEYS_TIME * 20L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerData data = DataManager.getInstance().getPlayerData(player);
            if (data == null || !data.hasPendingKeys()) return;

            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 2f);

            for (String msg : Messages.PENDING_KEYS_WARN) {
                player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                        "{amount}"
                }, new String[]{
                        String.valueOf(data.getTotalPendingKeysAmount())
                }));
            }
        });
    }
}
