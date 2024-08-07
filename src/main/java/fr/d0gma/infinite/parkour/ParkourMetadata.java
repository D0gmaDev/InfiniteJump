package fr.d0gma.infinite.parkour;

import fr.d0gma.infinite.modes.ParkourModeType;

import java.util.UUID;

public record ParkourMetadata(UUID uuid, ParkourModeType parkourModeType, long seed) {

}
