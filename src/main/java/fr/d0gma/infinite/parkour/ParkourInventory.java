package fr.d0gma.infinite.parkour;


import fr.d0gma.core.translation.TranslationService;
import fr.d0gma.infinite.modes.ParkourModeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class ParkourInventory {

    public static void open(Player player, MapSeed seed, OnModeSelection onModeSelection) {

        List<Item> items = Arrays.stream(ParkourModeType.values())
                .map(parkourModeType -> modeItem(parkourModeType, onModeSelection))
                .toList();

        PagedGui<Item> gui = PagedGui.items().setStructure(". # # # # # # # .").addIngredient('#', Markers.CONTENT_LIST_SLOT_HORIZONTAL).setContent(items).build();
        var title = new AdventureComponentWrapper(Component.text("Mode (seed: ").append(seedString(seed)).append(Component.text(")")));
        Window.single().setTitle(title).setGui(gui).open(player);
    }

    private static Item modeItem(ParkourModeType parkourModeType, OnModeSelection onModeSelection) {
        var itemName = new AdventureComponentWrapper(TranslationService.translate("parkour.mode." + parkourModeType.getKey()));
        return new SimpleItem(new ItemBuilder(parkourModeType.getMaterial()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(itemName), click -> onModeSelection.accept(parkourModeType, click));
    }

    private static Component seedString(MapSeed mapSeed) {
        return switch (mapSeed) {
            case MapSeed.RandomSeed.TRUE_RANDOM -> Component.text("Aléatoire", NamedTextColor.GRAY);
            case MapSeed.SetSeed(long seed) -> Component.text(Long.toHexString(seed), NamedTextColor.GRAY);
        };
    }

    public interface OnModeSelection extends BiConsumer<ParkourModeType, Click> {

    }
}
