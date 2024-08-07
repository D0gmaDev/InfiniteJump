package fr.d0gma.infinite.parkour;


import fr.d0gma.core.timer.RunnableHelper;
import fr.d0gma.core.translation.TranslationService;
import fr.d0gma.infinite.modes.ParkourModeType;
import fr.d0gma.infinite.players.JumpPlayer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemFlag;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class ParkourInventory {

    private static final Random RANDOM = new Random();

    public static void open(JumpPlayer player, MapSeed seed) {

        List<Item> items = Arrays.stream(ParkourModeType.values())
                .map(parkourModeType -> modeItem(parkourModeType, player, seed))
                .toList();

        PagedGui<Item> gui = PagedGui.items().setStructure(". # # # # # # # .").addIngredient('#', Markers.CONTENT_LIST_SLOT_HORIZONTAL).setContent(items).build();
        Window.single().setTitle("Menu").setGui(gui).open(player.getPlayer());
    }

    private static Item modeItem(ParkourModeType parkourModeType, JumpPlayer player, MapSeed seed) {
        var itemName = new AdventureComponentWrapper(TranslationService.translate("parkour.mode." + parkourModeType.getKey()));
        return new SimpleItem(new ItemBuilder(parkourModeType.getMaterial()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(itemName), click -> {
            click.getPlayer().closeInventory();
            Parkour parkour = new Parkour(parkourModeType, resolveSeed(seed));
            RunnableHelper.runSynchronously(() -> parkour.startParkour(player));
        });
    }

    private static long resolveSeed(MapSeed mapSeed){
        return switch (mapSeed){
            case MapSeed.RandomSeed randomSeed -> RANDOM.nextLong();
            case MapSeed.SetSeed(long seed) -> seed;
        };
    }
}
