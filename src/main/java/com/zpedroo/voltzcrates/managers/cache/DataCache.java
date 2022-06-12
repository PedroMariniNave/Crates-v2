package com.zpedroo.voltzcrates.managers.cache;

import com.zpedroo.voltzcrates.objects.crate.Crate;
import com.zpedroo.voltzcrates.objects.crate.PlacedCrate;
import com.zpedroo.voltzcrates.objects.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DataCache {

    private final Map<String, Crate> crates = new HashMap<>(8);
    private final Map<Location, PlacedCrate> placedCrates = new HashMap<>(8);
    private final Map<Player, PlayerData> playersData = new HashMap<>(32);

    public Map<String, Crate> getCrates() {
        return crates;
    }

    public Map<Location, PlacedCrate> getPlacedCrates() {
        return placedCrates;
    }

    public Map<Player, PlayerData> getPlayersData() {
        return playersData;
    }
}