package com.zpedroo.voltzcrates.objects.crate;

import com.zpedroo.voltzcrates.tasks.ShowRewardsTask;
import lombok.Getter;
import org.bukkit.Location;

@Getter
public class PlacedCrate {

    private final Location location;
    private final Crate crate;
    private final CrateHologram hologram;
    private final ShowRewardsTask showRewardsTask;

    public PlacedCrate(Location location, Crate crate) {
        this.location = location;
        this.crate = crate;
        this.hologram = new CrateHologram(this);
        this.showRewardsTask = new ShowRewardsTask(this);
    }

    public void delete() {
        hologram.remove();
        showRewardsTask.getItem().remove();
        showRewardsTask.cancel();
    }
}