package fr.d0gma.infinite.command;

import fr.d0gma.core.timer.RunnableHelper;
import fr.d0gma.infinite.database.TopInventory;
import fr.d0gma.infinite.seed.ParkourSeed;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equalsIgnoreCase("top") || !(commandSender instanceof Player sender)) {
            return false;
        }

        Optional.ofNullable(args.length != 0 ? args[0] : null).flatMap(ParkourSeed::decode)
                .ifPresent(parkourSeed -> RunnableHelper.runAsynchronously(() -> TopInventory.open(sender, parkourSeed)));
        return true;
    }
}
