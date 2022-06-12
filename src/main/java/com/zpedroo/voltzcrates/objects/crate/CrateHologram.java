package com.zpedroo.voltzcrates.objects.crate;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.zpedroo.voltzcrates.VoltzCrates;
import org.bukkit.entity.Player;

public class CrateHologram {

    private final String[] hologramLines;
    private TextLine[] textLines;

    private Hologram hologram;

    public CrateHologram(PlacedCrate crate) {
        this.hologramLines = crate.getCrate().getHologramLines();
        this.update(crate);
    }

    public void update(PlacedCrate crate) {
        if (hologram != null && hologram.isDeleted()) return;

        if (hologram == null) {
            hologram = HologramsAPI.createHologram(VoltzCrates.get(), crate.getLocation().clone().add(0.5D, 3.2, 0.5D));
            textLines = new TextLine[hologramLines.length];

            for (int i = 0; i < hologramLines.length; i++) {
                textLines[i] = hologram.insertTextLine(i, hologramLines[i]);
            }

            hologram.getVisibilityManager().setVisibleByDefault(false);
        } else {
            for (int i = 0; i < hologramLines.length; i++) {
                this.textLines[i].setText(hologramLines[i]);
            }
        }
    }

    public void showTo(Player player) {
        if (hologram == null) return;

        this.hologram.getVisibilityManager().showTo(player);
    }

    public void hideTo(Player player) {
        if (hologram == null) return;

        this.hologram.getVisibilityManager().hideTo(player);
    }

    public void remove() {
        if (hologram == null) return;

        this.hologram.delete();
        this.hologram = null;
    }
}