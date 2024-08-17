package fr.d0gma.infinite.database;

import fr.d0gma.infinite.seed.ParkourSeed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:plugins/InfiniteJump/runs.db";

    private Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(DATABASE_URL);
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        Connection connection = null;
        try {
            connection = connect();
            String sql = "CREATE TABLE IF NOT EXISTS parkour_runs ("
                         + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                         + "uuid TEXT NOT NULL, "
                         + "name TEXT NOT NULL, "
                         + "mode TEXT NOT NULL, "
                         + "ranked BOOLEAN NOT NULL, "
                         + "seed LONG NOT NULL, "
                         + "score DOUBLE NOT NULL, "
                         + "duration LONG NOT NULL, "
                         + "instant TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                         + ")";

            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public void insertRun(ParkourRun parkourRun) {
        Connection connection = null;
        try {
            connection = connect();
            String sql = "INSERT INTO parkour_runs (uuid, name, mode, ranked, seed, score, duration, instant) VALUES (?, ?, ?, ?,? , ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, parkourRun.playerId().toString());
                statement.setString(2, parkourRun.playerName());
                statement.setString(3, parkourRun.mode().name());
                statement.setBoolean(4, parkourRun.ranked());
                statement.setLong(5, parkourRun.mapSeed().seed());
                statement.setDouble(6, parkourRun.score());
                statement.setLong(7, parkourRun.duration().toMillis());
                statement.setString(8, parkourRun.instant().toString());
                statement.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    public List<ParkourRun> getAllRunsOf(UUID uuid) {
        List<ParkourRun> runs = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connect();
            String sql = "SELECT * FROM parkour_runs WHERE uuid = ? ORDER BY instant DESC";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    runs.add(ParkourRun.buildFrom(resultSet));
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return List.copyOf(runs);
    }

    public List<ParkourRun> getTopRunsFor(ParkourSeed seed, int maxNumber) {
        List<ParkourRun> runs = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connect();
            String sql = "SELECT * FROM parkour_runs WHERE mode = ? AND seed = ? AND ranked = TRUE";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, seed.mode().name());
                statement.setLong(2, seed.mapSeed().seed());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    runs.add(ParkourRun.buildFrom(resultSet));
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return runs.stream().sorted(ParkourRun.comparator(seed.mode())).limit(maxNumber).toList();
    }
}
