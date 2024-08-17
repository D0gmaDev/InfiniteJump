package fr.d0gma.infinite.seed;

import fr.d0gma.infinite.modes.ParkourModeType;

import java.util.Objects;
import java.util.Optional;

public record ParkourSeed(MapSeed mapSeed, ParkourModeType mode) {

    public ParkourSeed {
        Objects.requireNonNull(mapSeed);
        Objects.requireNonNull(mode);
    }

    public String encode() {
        return mode.ordinal() + Long.toHexString(mapSeed().seed());
    }

    public static Optional<ParkourSeed> decode(String input) {
        try {
            ParkourModeType mode = ParkourModeType.values()[Integer.parseInt(input.substring(0, 1))];
            MapSeed mapSeed = new MapSeed(Long.parseUnsignedLong(input.substring(1), 16));
            return Optional.of(new ParkourSeed(mapSeed, mode));
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
