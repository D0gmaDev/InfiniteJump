package fr.d0gma.infinite.parkour;

import java.util.Optional;

public sealed interface MapSeed permits MapSeed.SetSeed, MapSeed.RandomSeed {

    static MapSeed of(Long seed) {
        return Optional.ofNullable(seed).<MapSeed>map(SetSeed::new).orElse(RandomSeed.TRUE_RANDOM);
    }

    static MapSeed safeParseFromHex(String hex) {
        try {
            return new SetSeed(Long.parseUnsignedLong(hex, 16));
        } catch (NumberFormatException e) {
            return RandomSeed.TRUE_RANDOM;
        }
    }

    enum RandomSeed implements MapSeed {
        TRUE_RANDOM
    }

    record SetSeed(long seed) implements MapSeed {
    }

}
