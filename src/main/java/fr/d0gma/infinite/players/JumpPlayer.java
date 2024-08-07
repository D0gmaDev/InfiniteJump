package fr.d0gma.infinite.players;

import fr.d0gma.core.player.CorePlayer;
import fr.d0gma.core.team.ScoreboardTeam;
import fr.d0gma.infinite.parkour.Parkour;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class JumpPlayer implements CorePlayer<JumpPlayer> {

    private final UUID uuid;
    private final Player player;

    private ScoreboardTeam<JumpPlayer> scoreboardTeam;
    private Sidebar<Component> sidebar;

    private Parkour parkour;

    private Parkour lastParkour;
    private boolean spectator = false;

    public JumpPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.player = player;
    }

    public void sendMessage(Component message) {
        getPlayer().sendMessage(message);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        getPlayer().playSound(getPlayer().getLocation(), sound, volume, pitch);
    }

    public Optional<Parkour> getParkour() {
        return Optional.ofNullable(this.parkour);
    }

    public void setParkour(Parkour parkour) {
        this.parkour = parkour;
    }

    public Optional<Parkour> getLastParkour() {
        return Optional.ofNullable(this.lastParkour);
    }

    public void setLastParkour(Parkour lastParkour) {
        this.lastParkour = lastParkour;
    }

    public boolean isSpectator() {
        return this.spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getPlayerName() {
        return this.player.getName();
    }

    @Override
    public Optional<ScoreboardTeam<JumpPlayer>> getScoreboardTeam() {
        return Optional.ofNullable(this.scoreboardTeam);
    }

    @Override
    public void setScoreboardTeam(ScoreboardTeam<JumpPlayer> scoreboardTeam) {
        this.scoreboardTeam = scoreboardTeam;
    }

    public Optional<Sidebar<Component>> getSidebar() {
        return Optional.ofNullable(this.sidebar);
    }

    public void setSidebar(Sidebar<Component> sidebar) {
        getSidebar().ifPresent(oldSidebar -> oldSidebar.removeViewer(getPlayer()));
        this.sidebar = sidebar;
        getSidebar().ifPresent(newSidebar -> newSidebar.addViewer(getPlayer()));
    }
}
