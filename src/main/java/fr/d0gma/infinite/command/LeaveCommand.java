package fr.d0gma.infinite.command;

import fr.d0gma.infinite.game.Lobby;
import fr.d0gma.infinite.parkour.Parkour;
import fr.d0gma.infinite.players.JumpPlayer;
import fr.d0gma.infinite.players.JumpPlayerService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.d0gma.core.translation.TranslationService.translate;

public class LeaveCommand implements CommandExecutor {

    private final JumpPlayerService jumpPlayerService;
    private final Lobby lobby;

    public LeaveCommand(JumpPlayerService jumpPlayerService, Lobby lobby) {
        this.jumpPlayerService = jumpPlayerService;
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player sender)) {
            return false;
        }

        JumpPlayer jumpPlayer = jumpPlayerService.getPlayer(sender);

        if (jumpPlayer == null) {
            return false;
        }

        if (jumpPlayer.isSpectator()) {
            return true;
        }

        jumpPlayer.getParkour().ifPresentOrElse(
                parkour -> makeLeave(parkour, jumpPlayer),
                () -> jumpPlayer.sendMessage(translate("parkour.message.not_in_parkour"))
        );

        return true;
    }

    private void makeLeave(Parkour parkour, JumpPlayer jumpPlayer) {
        parkour.removePlayer(jumpPlayer);
        this.lobby.teleport(jumpPlayer);
    }
}
