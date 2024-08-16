package fr.d0gma.infinite.command;

import fr.d0gma.core.timer.RunnableHelper;
import fr.d0gma.infinite.database.TopInventory;
import fr.d0gma.infinite.parkour.MapSeed;
import fr.d0gma.infinite.parkour.ParkourInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equalsIgnoreCase("top") || !(commandSender instanceof Player sender)) {
            return false;
        }

        var mapSeed = MapSeed.safeParseFromHex(args.length != 0 ? args[0] : null);

        if (!(mapSeed instanceof MapSeed.SetSeed(long seed))) {
            return true;
        }

        ParkourInventory.open(sender, mapSeed, (mode, click) -> {
            click.getPlayer().closeInventory();
            RunnableHelper.runAsynchronously(() -> TopInventory.open(sender, mode, seed));
        });
        return true;
    }
}
