package fr.d0gma.infinite.players;

import fr.d0gma.infinite.game.Lobby;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static fr.d0gma.core.translation.TranslationService.translate;

public class JumpPlayerService implements Listener {

    private final Lobby lobby;

    private final Map<UUID, JumpPlayer> players = new HashMap<>();

    public JumpPlayerService(JavaPlugin plugin, Lobby lobby) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.lobby = lobby;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        JumpPlayer jumpPlayer = new JumpPlayer(event.getPlayer());
        this.players.put(jumpPlayer.getUniqueId(), jumpPlayer);

        lobby.teleport(jumpPlayer);
        event.joinMessage(translate("parkour.message.join", Placeholder.unparsed("name", event.getPlayer().getName())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        JumpPlayer jumpPlayer = this.players.remove(event.getPlayer().getUniqueId());

        if (jumpPlayer == null) {
            return;
        }

        jumpPlayer.getParkour().ifPresent(parkour -> parkour.removePlayer(jumpPlayer));
        event.quitMessage(translate("parkour.message.quit", Placeholder.unparsed("name", event.getPlayer().getName())));
    }

    public JumpPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public JumpPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public Collection<JumpPlayer> getPlayers() {
        return this.players.values();
    }

}
