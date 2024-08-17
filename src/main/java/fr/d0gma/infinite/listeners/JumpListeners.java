package fr.d0gma.infinite.listeners;

import fr.d0gma.core.timer.RunnableHelper;
import fr.d0gma.infinite.database.HistoryInventory;
import fr.d0gma.infinite.game.Lobby;
import fr.d0gma.infinite.parkour.Parkour;
import fr.d0gma.infinite.players.JumpPlayer;
import fr.d0gma.infinite.players.JumpPlayerService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class JumpListeners implements Listener {

    private final JumpPlayerService jumpPlayerService;
    private final Lobby lobby;

    public JumpListeners(JumpPlayerService jumpPlayerService, Lobby lobby) {
        this.jumpPlayerService = jumpPlayerService;
        this.lobby = lobby;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            event.setCancelled(true);
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true);

            ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();

            if (itemStack.getType() != Material.AIR) {
                JumpPlayer player = jumpPlayerService.getPlayer(event.getPlayer());

                if (player == null || event.getPlayer().hasCooldown(itemStack.getType())) {
                    return;
                }

                switch (itemStack.getType()) {
                    case LIGHT_WEIGHTED_PRESSURE_PLATE ->
                            player.getParkour().ifPresent(parkour -> parkour.respawnPlayer(event.getPlayer()));
                    case BARRIER -> player.getParkour().ifPresent(Parkour::skip);
                    case COMPASS -> player.getPlayer().performCommand("parkour");
                    case KNOWLEDGE_BOOK -> {
                        if (player.getParkour().isEmpty() && !player.getPlayer().hasCooldown(Material.KNOWLEDGE_BOOK)) {
                            player.getPlayer().setCooldown(Material.KNOWLEDGE_BOOK, 20 * 2);
                            HistoryInventory.open(player.getPlayer());
                        }
                    }
                    case PAPER -> {
                        if (player.getParkour().isEmpty() && !player.getPlayer().hasCooldown(Material.PAPER)) {
                            player.getPlayer().setCooldown(Material.PAPER, 20 * 5);
                            player.getLastParkourSeed().map(Parkour::new).ifPresent(parkour -> RunnableHelper.runSynchronously(() -> parkour.startParkour(player)));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCheckpoint(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            JumpPlayer jumpPlayer = jumpPlayerService.getPlayer(event.getPlayer());

            if (jumpPlayer != null) {
                jumpPlayer.getParkour().ifPresent(parkour -> parkour.checkpoint(event.getClickedBlock()));
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        event.setCancelled(true);

        if (event.getEntity() instanceof Player player && event.getCause() == DamageCause.VOID) {
            JumpPlayer jumpPlayer = jumpPlayerService.getPlayer(player);

            if (jumpPlayer == null) {
                return;
            }

            jumpPlayer.getParkour().ifPresentOrElse(
                    parkour -> parkour.respawnPlayer(player),
                    () -> this.lobby.teleport(jumpPlayer)
            );
        }
    }

    @EventHandler
    public void onSpectatorLeave(PlayerToggleSneakEvent event) {
        if (event.isSneaking() && event.getPlayer().getSpectatorTarget() != null && jumpPlayerService.getPlayer(event.getPlayer()).isSpectator()) {
            JumpPlayer jumpPlayer = jumpPlayerService.getPlayer(event.getPlayer());
            //jumpPlayer.getParkour().exitSpectate(jumpPlayer);
            //todo exit spectate
        }
    }
}
