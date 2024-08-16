package fr.d0gma.infinite.command;

import fr.d0gma.core.timer.RunnableHelper;
import fr.d0gma.infinite.parkour.MapSeed;
import fr.d0gma.infinite.parkour.Parkour;
import fr.d0gma.infinite.modes.ModeSelectionInventory;
import fr.d0gma.infinite.players.JumpPlayer;
import fr.d0gma.infinite.players.JumpPlayerService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static fr.d0gma.core.translation.TranslationService.translate;

public class ParkourCommand implements CommandExecutor {

    private static final Random RANDOM = new Random();

    private final JumpPlayerService jumpPlayerService;

    public ParkourCommand(JumpPlayerService jumpPlayerService) {
        this.jumpPlayerService = jumpPlayerService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equalsIgnoreCase("parkour") || !(commandSender instanceof Player sender)) {
            return false;
        }

        JumpPlayer player = jumpPlayerService.getPlayer(sender);

        if (player == null) {
            return false;
        }

        if (player.getParkour().isPresent()) {
            player.sendMessage(translate("parkour.message.already_in_parkour"));
            return true;
        }

        var mapSeed = MapSeed.safeParseFromHex(args.length != 0 ? args[0] : null);

        ModeSelectionInventory.open(sender, mapSeed, (mode, click) -> {
            click.getPlayer().closeInventory();
            Parkour parkour = new Parkour(mode, resolveSeed(mapSeed));
            RunnableHelper.runSynchronously(() -> parkour.startParkour(player));
        });
        return true;
    }

    private static long resolveSeed(MapSeed mapSeed) {
        return switch (mapSeed) {
            case MapSeed.RandomSeed.TRUE_RANDOM -> RANDOM.nextLong();
            case MapSeed.SetSeed(long seed) -> seed;
        };
    }
}
