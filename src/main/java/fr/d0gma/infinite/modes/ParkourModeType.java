package fr.d0gma.infinite.modes;

import fr.d0gma.infinite.parkour.Parkour;
import org.bukkit.Material;

import java.util.function.BiFunction;

public enum ParkourModeType {

    SPEED_RUN(Material.CLOCK, "speed_run", SpeedrunMode::new),
    HARDCORE(Material.GOLDEN_APPLE, "hardcore", HardcoreMode::new),
    THREE_LIVES(Material.APPLE, "three_lives", ThreeLivesMode::new),
    DEATH_REAPER(Material.DIAMOND_HOE, "death_reaper", DeathReaperMode::new),
    FUN_INFINITE(Material.BEACON, "fun_infinite", FunInfiniteMode::new),
    TRAINING(Material.ARMOR_STAND, "training", TrainingMode::new),
    PROGRESSIVE(Material.CHAINMAIL_CHESTPLATE, "progressive", ProgressiveMode::new);

    private final Material material;
    private final String key;
    private final ParkourModeGenerator mode;

    ParkourModeType(Material material, String key, ParkourModeGenerator mode) {
        this.material = material;
        this.key = key;
        this.mode = mode;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getKey() {
        return this.key;
    }

    public ParkourMode createMode(Parkour parkour) {
        return this.mode.apply(this, parkour);
    }

    private interface ParkourModeGenerator extends BiFunction<ParkourModeType, Parkour, ParkourMode> {

    }
}
