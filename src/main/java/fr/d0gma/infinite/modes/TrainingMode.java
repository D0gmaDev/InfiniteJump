package fr.d0gma.infinite.modes;

import fr.d0gma.infinite.parkour.Parkour;
import fr.d0gma.infinite.zone.Zone;

import java.util.List;
import java.util.Set;

class TrainingMode implements ParkourMode {

    private final ParkourModeType type;
    private final List<Zone> allowedZones;

    TrainingMode(ParkourModeType type, Parkour parkour) {
        this.type = type;
        this.allowedZones = List.copyOf(Set.of(Zone.TRI_LADDERS, Zone.HAY_STACK));
    }

    @Override
    public ParkourModeType getType() {
        return this.type;
    }

    @Override
    public List<Zone> getAllowedZones() {
        return this.allowedZones;
    }
}
