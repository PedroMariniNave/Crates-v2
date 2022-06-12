package com.zpedroo.voltzcrates.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.zpedroo.voltzcrates.managers.DataManager;
import com.zpedroo.voltzcrates.objects.crate.PlacedCrate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ProtocolLibHook extends PacketAdapter {

    private final Map<Player, List<PlacedCrate>> cratesShowing = new HashMap<>(32);

    public ProtocolLibHook(Plugin plugin, PacketType packetType) {
        super(plugin, packetType);
    }

    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock((HashSet<Byte>) null, 15);
        Location location = block.getLocation();

        PlacedCrate crate = DataManager.getInstance().getPlacedCrateByLocation(location);
        if (crate == null) {
            if (!cratesShowing.containsKey(player)) return;

            List<PlacedCrate> showing = cratesShowing.remove(player);

            for (PlacedCrate toHide : showing) {
                toHide.getHologram().hideTo(player);
            }
            return;
        }

        crate.getHologram().showTo(player);

        List<PlacedCrate> holoList = cratesShowing.containsKey(player) ? cratesShowing.get(player) : new ArrayList<>(1);
        holoList.add(crate);

        cratesShowing.put(player, holoList);
    }
}