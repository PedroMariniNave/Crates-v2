package com.zpedroo.voltzcrates.managers;

import com.zpedroo.voltzcrates.VoltzCrates;
import com.zpedroo.voltzcrates.objects.crate.Crate;
import com.zpedroo.voltzcrates.objects.crate.PlacedCrate;
import com.zpedroo.voltzcrates.objects.crate.Reward;
import com.zpedroo.voltzcrates.utils.builder.ItemBuilder;
import com.zpedroo.voltzcrates.utils.config.Messages;
import com.zpedroo.voltzcrates.utils.config.Titles;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.particle.ParticleEffect;

import java.io.File;
import java.util.*;

public class CrateManager {

    private static CrateManager instance;
    public static CrateManager getInstance() { return instance; }

    private final Map<Block, BukkitTask> openChestTask = new HashMap<>(4);

    public CrateManager() {
        instance = this;
        this.loadCrates();
    }

    public void open(Player player, PlacedCrate placedCrate, int amount) {
        Reward lastReward = null;
        boolean cancelled = false;
        amountLoop: for (int i = 0; i < amount; ++i) {
            boolean openCrate = false;
            while (!openCrate) {
                for (Reward reward : placedCrate.getCrate().getRewards()) {
                    double randomNumber = new Random().nextDouble() * 100D;
                    if (randomNumber > reward.getChance()) continue;
                    if (InventoryManager.getFreeSpace(player, reward.getDisplay()) <= 0) {
                        player.sendMessage(Messages.NEED_SPACE_CRATE);
                        cancelled = true;
                        break amountLoop;
                    }

                    for (String cmd : reward.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(cmd, new String[]{
                                "{player}"
                        }, new String[]{
                                player.getName()
                        }));
                    }

                    if (reward.getItemToGive() != null) {
                        player.getInventory().addItem(reward.getItemToGive());
                    }

                    openCrate = true;
                    lastReward = reward;
                    break;
                }
            }
        }

        if (cancelled) {
            player.getInventory().addItem(placedCrate.getCrate().getKeyItem());
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
            return;
        }

        if (lastReward == null) return;

        showReward(lastReward, placedCrate);
        changeChestState(placedCrate.getLocation(), true);
        player.sendTitle(Titles.REWARD[0], StringUtils.replaceEach(Titles.REWARD[1], new String[]{
                "{item}"
        }, new String[]{
                lastReward.getDisplay().hasItemMeta() ? (lastReward.getDisplay().getItemMeta().hasDisplayName() ? lastReward.getDisplay().getItemMeta().getDisplayName() : lastReward.getDisplay().getType().toString()) : lastReward.getDisplay().getType().toString()
        }));

        ParticleEffect.FIREWORKS_SPARK.display(placedCrate.getLocation().clone().add(0.5D, 1.25D, 0.5D), 0.25f, 0.25f, 0.25f, 0.1f, 25, null);
    }

    public void create(Location location, Crate crate) {
        DataManager.getInstance().cache(new PlacedCrate(location, crate));

        File file = crate.getFile();
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        List<String> locations = fileConfig.getStringList("Locations");
        locations.add(serializeLocation(location));
        fileConfig.set("Locations", locations);
        try {
            fileConfig.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void delete(PlacedCrate placedCrate) {
        placedCrate.delete();
        DataManager.getInstance().getCache().getPlacedCrates().remove(placedCrate.getLocation());

        File file = placedCrate.getCrate().getFile();
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        List<String> locations = fileConfig.getStringList("Locations");
        locations.remove(serializeLocation(placedCrate.getLocation()));
        fileConfig.set("Locations", locations);
        try {
            fileConfig.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCrates() {
        File folder = new File(VoltzCrates.get().getDataFolder(), "/crates");
        File[] files = folder.listFiles((file, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

            String name = file.getName().replace(".yml", "");
            ItemStack key = ItemBuilder.build(fileConfig, "Key").build();
            List<String> hologramLines = fileConfig.getStringList("Crate-Settings.hologram");

            for (int i = 0; i < hologramLines.size(); ++i) {
                hologramLines.set(i, ChatColor.translateAlternateColorCodes('&', hologramLines.get(i)));
            }

            List<Reward> rewards = new ArrayList<>(4);

            for (String rewardName : fileConfig.getConfigurationSection("Rewards").getKeys(false)) {
                double chance = fileConfig.getDouble("Rewards." + rewardName + ".chance", 0);
                ItemStack display = ItemBuilder.build(fileConfig, "Rewards." + rewardName + ".display").build();
                ItemStack itemToGive = fileConfig.contains("Rewards." + rewardName + ".to-give") ? ItemBuilder.build(fileConfig, "Rewards." + rewardName + ".to-give").build() : null;
                List<String> commands = fileConfig.getStringList("Rewards." + rewardName + ".commands");

                rewards.add(new Reward(chance, display, itemToGive, commands));
            }

            if (rewards.isEmpty()) continue;

            Crate crate = new Crate(file, name, key, hologramLines.toArray(new String[1]), rewards);
            DataManager.getInstance().cache(crate);

            for (String serializedLocation : fileConfig.getStringList("Locations")) {
                if (serializedLocation == null) continue;

                Location location = deserializeLocation(serializedLocation);
                DataManager.getInstance().cache(new PlacedCrate(location, crate));
            }
        }
    }

    private void showReward(Reward reward, PlacedCrate crate) {
        ItemStack display = reward.getDisplay();

        crate.getShowRewardsTask().showItem(display, 2);
    }

    private void changeChestState(Location location, boolean open) {
        Block block = location.getBlock();
        if (block.getType() != Material.ENDER_CHEST) return;

        CraftWorld craftWorld = ((CraftWorld) location.getWorld());
        if (craftWorld == null) return;

        int data = open ? 1 : 0;
        craftWorld.getHandle().playBlockAction(new BlockPosition(location.getX(), location.getY(), location.getZ()), CraftMagicNumbers.getBlock(block.getType()), 1, data);
        location.getWorld().playSound(location, Sound.CHEST_OPEN, 0.1f, 100f);

        BukkitTask task = openChestTask.remove(block);
        if (task != null) task.cancel();

        if (!open) return;

        openChestTask.put(block, new BukkitRunnable() {
            @Override
            public void run() {
                changeChestState(location, false);
                location.getWorld().playSound(location, Sound.CHEST_CLOSE, 0.1f, 100f);
            }
        }.runTaskLater(VoltzCrates.get(), 20L * 2));
    }

    private String serializeLocation(Location location) {
        if (location == null) return null;

        StringBuilder serialized = new StringBuilder(4);
        serialized.append(location.getWorld().getName());
        serialized.append("#" + location.getX());
        serialized.append("#" + location.getY());
        serialized.append("#" + location.getZ());

        return serialized.toString();
    }

    private Location deserializeLocation(String location) {
        if (location == null) return null;

        String[] locationSplit = location.split("#");
        double x = Double.parseDouble(locationSplit[1]);
        double y = Double.parseDouble(locationSplit[2]);
        double z = Double.parseDouble(locationSplit[3]);

        return new Location(Bukkit.getWorld(locationSplit[0]), x, y, z);
    }
}