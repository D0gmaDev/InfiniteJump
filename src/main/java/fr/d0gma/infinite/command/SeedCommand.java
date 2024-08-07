package fr.d0gma.infinite.command;

import fr.d0gma.infinite.InfiniteJump;
import fr.d0gma.infinite.modes.ParkourModeType;
import fr.d0gma.infinite.players.JumpPlayer;
import fr.d0gma.infinite.players.JumpPlayerService;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.d0gma.core.translation.TranslationService.translate;

public class SeedCommand implements CommandExecutor {

    private final JumpPlayerService jumpPlayerService;

    public SeedCommand(JumpPlayerService jumpPlayerService) {
        this.jumpPlayerService = jumpPlayerService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (!(commandSender instanceof Player sender)) {
            return false;
        }

        System.out.println(InfiniteJump.getInstance().getDatabaseManager().getAllRunsOf(sender.getUniqueId()));
        System.out.println("-----");
        System.out.println(InfiniteJump.getInstance().getDatabaseManager().getQuickestRunsFor(ParkourModeType.SPEED_RUN, 0L, 15));

        JumpPlayer jumpPlayer = jumpPlayerService.getPlayer(sender);

        if (jumpPlayer == null) {
            return false;
        }

        jumpPlayer.getParkour().ifPresentOrElse(
                parkour -> jumpPlayer.sendMessage(translate("parkour.message.seed", Placeholder.parsed("seed", String.valueOf(parkour.getSeed())))),
                () -> jumpPlayer.sendMessage(translate("parkour.message.not_in_parkour"))
        );
        return true;
    }
}
