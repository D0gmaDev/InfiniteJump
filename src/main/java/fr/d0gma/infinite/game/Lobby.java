package fr.d0gma.infinite.game;

import fr.d0gma.core.translation.TranslationService;
import fr.d0gma.core.world.schematic.Schematic;
import fr.d0gma.core.world.schematic.SchematicService;
import fr.d0gma.infinite.InfiniteJump;
import fr.d0gma.infinite.players.JumpPlayer;
import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import me.catcoder.sidebar.text.TextIterators;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import static fr.d0gma.core.translation.TranslationService.translate;

public class Lobby {

    private Location spawn;
    private Sidebar<Component> sidebar;

    public void load(World world) {

        // Paste Lobby Schematic
        Schematic schematic = SchematicService.loadSchematic("schematics/lobby.schem", InfiniteJump.getInstance()).orElseThrow();
        schematic.paste(new Location(world, -4, 140, -4));
        this.spawn = new Location(world, 0.5, 143, 0.5);

        // Create Lobby Sidebar
        this.sidebar = ProtocolSidebar.newAdventureSidebar(translate("parkour.scoreboard.title"), InfiniteJump.getInstance());
        this.sidebar.addBlankLine();
        TranslationService.getAllKeysStartingWith("parkour.scoreboard.lobby").stream()
                .map(TranslationService::translate).forEachOrdered(this.sidebar::addLine);
        this.sidebar.addBlankLine();
        this.sidebar.addUpdatableLine(sidebar.toLineUpdater(TextIterators.textFadeHypixel("rezoleo.fr")));
        this.sidebar.getObjective().scoreNumberFormatBlank();
        this.sidebar.updateLinesPeriodically(20 * 5, 1);
    }

    public void teleport(JumpPlayer jumpPlayer) {
        Player player = jumpPlayer.getPlayer();
        player.teleport(this.spawn);

        player.setGameMode(GameMode.ADVENTURE);
        player.setLevel(0);
        player.setExp(0);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();

        player.playSound(player.getLocation(), Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1f, 0.5f);

        jumpPlayer.setSidebar(this.sidebar);

        player.getInventory().setHeldItemSlot(4);

        player.getInventory().setItem(4, ParkourItem.menuItem());

        if (jumpPlayer.getLastParkourSeed().isPresent()) {
            player.getInventory().setItem(5, ParkourItem.replayItem());
        }

        player.getInventory().setItem(8, ParkourItem.historyItem());
    }

}
