package fr.d0gma.infinite.modes;

import fr.d0gma.infinite.parkour.Parkour;

class FunInfiniteMode implements ParkourMode {

    private final ParkourModeType type;

    FunInfiniteMode(ParkourModeType type, Parkour parkour) {
        this.type = type;
    }

    @Override
    public ParkourModeType getType() {
        return this.type;
    }
}
