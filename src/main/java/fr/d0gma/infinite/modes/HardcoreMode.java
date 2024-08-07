package fr.d0gma.infinite.modes;

import fr.d0gma.infinite.game.ParkourEndReason;
import fr.d0gma.infinite.parkour.Parkour;
import org.bukkit.entity.Player;

class HardcoreMode implements ParkourMode {

    private final ParkourModeType type;
    private final Parkour parkour;

    int life = 1;

    HardcoreMode(ParkourModeType type, Parkour parkour) {
        this.type = type;
        this.parkour = parkour;
    }

    @Override
    public ParkourModeType getType() {
        return this.type;
    }

    @Override
    public boolean isSkipAllowed() {
        return false;
    }

    @Override
    public boolean isManualRespawnAllowed() {
        return false;
    }

    @Override
    public float getDifficultyMultiplier() {
        return 2;
    }

    @Override
    public boolean onRespawn(Player player) {
        life--;

        if (life == 0) {
            parkour.stop(ParkourEndReason.DEATH);
            return false;
        }
        return true;
    }
}
