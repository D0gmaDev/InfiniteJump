package fr.d0gma.infinite.database;

import fr.d0gma.infinite.modes.ParkourModeType;
import fr.d0gma.infinite.seed.MapSeed;
import fr.d0gma.infinite.seed.ParkourSeed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

public record ParkourRun(int id, UUID playerId, String playerName, ParkourSeed seed, boolean ranked, double score,
                         Duration duration, Instant instant) implements Comparable<ParkourRun> {

    public ParkourRun {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(playerName);
        Objects.requireNonNull(seed);
        if (score < 0) {
            throw new IllegalArgumentException("negative score");
        }
        if (duration == null || duration.isNegative()) {
            throw new IllegalArgumentException("invalid duration");
        }
    }

    public ParkourRun(UUID playerId, String playerName, ParkourSeed seed, boolean ranked, double score, Duration duration, Instant instant) {
        this(-1, playerId, playerName, seed, ranked, score, duration, instant);
    }

    static ParkourRun buildFrom(ResultSet resultSet) throws SQLException {
        return new ParkourRun(
                resultSet.getInt("id"),
                UUID.fromString(resultSet.getString("uuid")),
                resultSet.getString("name"),
                new ParkourSeed(
                        new MapSeed(resultSet.getLong("seed")),
                        ParkourModeType.valueOf(resultSet.getString("mode"))),
                resultSet.getBoolean("ranked"),
                resultSet.getDouble("score"),
                Duration.ofMillis(resultSet.getLong("duration")),
                Instant.parse(resultSet.getString("instant"))
        );
    }

    public ParkourModeType mode() {
        return this.seed().mode();
    }

    public MapSeed mapSeed() {
        return this.seed().mapSeed();
    }

    @Override
    public int compareTo(ParkourRun other) {
        return (seed().equals(other.seed())) ? comparator(seed().mode()).compare(this, other) : 0;
    }

    public static Comparator<ParkourRun> comparator(ParkourModeType mode) {
        return switch (mode) {
            case SPEED_RUN -> Comparator
                    .comparing(ParkourRun::duration);
            case HARDCORE, THREE_LIVES, FUN_INFINITE -> Comparator
                    .comparingDouble(ParkourRun::score).reversed()
                    .thenComparing(ParkourRun::duration, Comparator.reverseOrder());
            case DEATH_REAPER -> Comparator
                    .comparing(ParkourRun::duration).reversed();
            case TRAINING, PROGRESSIVE -> Comparator
                    .comparingInt(ParkourRun::id);
        };
    }
}
