package fr.d0gma.infinite.parkour;

import java.util.Optional;

public sealed interface MapSeed permits MapSeed.RandomSeed, MapSeed.SetSeed {

    RandomSeed RANDOM_SEED = new RandomSeed();

    record SetSeed(long seed) implements MapSeed {}

    record RandomSeed() implements MapSeed {}

    static MapSeed of(Long seed){
        return Optional.ofNullable(seed).<MapSeed>map(SetSeed::new).orElse(RANDOM_SEED);
    }

}
