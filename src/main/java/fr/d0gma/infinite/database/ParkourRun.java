package fr.d0gma.infinite.database;

import fr.d0gma.infinite.modes.ParkourModeType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ParkourRun(int id, UUID playerId, String playerName, ParkourModeType mode, long seed, double score,
                         Duration duration, Instant instant) {

    public ParkourRun {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(playerName);
        Objects.requireNonNull(mode);
        if (score < 0) {
            throw new IllegalArgumentException("negative score");
        }
        if (duration == null || duration.isNegative()) {
            throw new IllegalArgumentException("invalid duration");
        }
    }

    public ParkourRun(UUID playerId, String playerName, ParkourModeType mode, long seed, double score, Duration duration, Instant instant) {
        this(-1, playerId, playerName, mode, seed, score, duration, instant);
    }

    static ParkourRun buildFrom(ResultSet resultSet) throws SQLException {
        return new ParkourRun(
                resultSet.getInt("id"),
                UUID.fromString(resultSet.getString("uuid")),
                resultSet.getString("name"),
                ParkourModeType.valueOf(resultSet.getString("mode")),
                resultSet.getLong("seed"),
                resultSet.getDouble("score"),
                Duration.ofMillis(resultSet.getLong("duration")),
                Instant.parse(resultSet.getString("instant"))
        );
    }
}
