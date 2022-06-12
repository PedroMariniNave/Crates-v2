package com.zpedroo.voltzcrates.managers;

import com.zpedroo.voltzcrates.managers.cache.DataCache;
import com.zpedroo.voltzcrates.objects.crate.Crate;
import com.zpedroo.voltzcrates.objects.crate.PlacedCrate;
import com.zpedroo.voltzcrates.objects.player.PlayerData;
import com.zpedroo.voltzcrates.utils.FileUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private static DataManager instance;
    public static DataManager getInstance() { return instance; }

    private final DataCache dataCache = new DataCache();

    public DataManager() {
        instance = this;
    }

    private PlayerData getDataFromFile(Player player) {
        FileUtils.Files file = FileUtils.Files.DATA;
        FileUtils.FileManager fileManager = FileUtils.get().getFile(file);
        FileConfiguration fileConfiguration = fileManager.get();
        if (!fileConfiguration.contains("Data." + player.getName())) return new PlayerData(player.getUniqueId(), new HashMap<>(2));

        Map<Crate, Integer> pendingKeys = new HashMap<>(2);
        for (String str : fileConfiguration.getStringList("Data." + player.getName())) {
            String[] split = str.split(":");
            if (split.length < 2) continue;

            Crate crate = getCrateByName(split[0]);
            if (crate == null) continue;

            int amount = Integer.parseInt(split[1]);
            if (amount <= 0) continue;

            pendingKeys.put(crate, amount);
        }

        return new PlayerData(player.getUniqueId(), pendingKeys);
    }

    public PlayerData getPlayerData(Player player) {
        PlayerData data = dataCache.getPlayersData().get(player);
        if (data == null) {
            data = getDataFromFile(player);
            dataCache.getPlayersData().put(player, data);
        }

        return data;
    }

    public Crate getCrateByName(String name) {
        return dataCache.getCrates().get(name.toUpperCase());
    }

    public PlacedCrate getPlacedCrateByLocation(Location location) {
        return dataCache.getPlacedCrates().get(location);
    }

    public void savePlayerData(Player player) {
        PlayerData data = dataCache.getPlayersData().get(player);
        if (data == null || !data.isUpdate()) return;

        FileUtils.Files file = FileUtils.Files.DATA;
        FileUtils.FileManager fileManager = FileUtils.get().getFile(file);
        FileConfiguration fileConfiguration = fileManager.get();

        Map<Crate, Integer> pendingKeys = data.getPendingKeys();
        List<String> listToSave = new ArrayList<>(pendingKeys.size());

        for (Map.Entry<Crate, Integer> entry : pendingKeys.entrySet()) {
            Crate crate = entry.getKey();
            int amount = entry.getValue();

            listToSave.add(crate.getName() + ":" + amount);
        }

        fileConfiguration.set("Data." + player.getName(), listToSave);
        fileManager.save();
        data.setUpdate(false);
    }

    public void saveAllPlayersData() {
        dataCache.getPlayersData().keySet().forEach(this::savePlayerData);
    }

    public void cache(Crate crate) {
        dataCache.getCrates().put(crate.getName().toUpperCase(), crate);
    }

    public void cache(PlacedCrate placedCrate) {
        dataCache.getPlacedCrates().put(placedCrate.getLocation(), placedCrate);
    }

    public DataCache getCache() {
        return dataCache;
    }
}
