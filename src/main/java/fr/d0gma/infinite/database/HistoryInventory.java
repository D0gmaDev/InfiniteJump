package fr.d0gma.infinite.database;

import fr.d0gma.core.timer.RunnableHelper;
import fr.d0gma.core.translation.TranslationService;
import fr.d0gma.core.utils.AutoCache;
import fr.d0gma.core.utils.TimeUtils;
import fr.d0gma.infinite.InfiniteJump;
import fr.d0gma.infinite.modes.ParkourModeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.builder.SkullBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.util.MojangApiUtils;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.d0gma.core.translation.TranslationService.translate;

public class HistoryInventory {

    private static final AutoCache<UUID, List<ParkourRun>> runsCache = new AutoCache<>(
            Function.identity(),
            InfiniteJump.getInstance().getDatabaseManager()::getAllRunsOf,
            Duration.ofSeconds(15)
    );

    public static void open(Player player) {

        RunnableHelper.runAsynchronously(() -> {

            List<ParkourRun> runs = runsCache.getOrFetch(player.getUniqueId());
            List<Item> items = runs.stream().map(HistoryInventory::runItem).toList();

            Item statsItem = statsItem(player.getName(), runs);

            Item border = new SimpleItem(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setDisplayName(""));

            Gui gui = PagedGui.items()
                    .setStructure(
                            "# # # # s # # # #",
                            "# x x x x x x x #",
                            "# x x x x x x x #",
                            "# x x x x x x x #",
                            "# # # < # > # # #")
                    .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                    .addIngredient('#', border)
                    .addIngredient('s', statsItem)
                    .addIngredient('<', new BackItem())
                    .addIngredient('>', new ForwardItem())
                    .setContent(items)
                    .build();

            var record = Window.single().setTitle("Historique").setGui(gui).build(player);
            RunnableHelper.runSynchronously(record::open);
        });

        runsCache.removeExpiredEntries();
    }

    private static Item runItem(ParkourRun parkourRun) {
        var tagResolver = TagResolver.resolver(
                Placeholder.unparsed("id", String.valueOf(parkourRun.id())),
                Placeholder.unparsed("name", parkourRun.playerName()),
                Placeholder.component("mode", translate("parkour.mode." + parkourRun.mode().getKey())),
                Placeholder.unparsed("seed", String.valueOf(parkourRun.seed())),
                Placeholder.unparsed("score", String.valueOf(parkourRun.score())),
                Placeholder.unparsed("duration", TimeUtils.format(parkourRun.duration())),
                Placeholder.unparsed("date", TimeUtils.formatDateToCET(parkourRun.instant()))
        );

        var itemName = new AdventureComponentWrapper(translate("parkour.run_item.name", tagResolver));
        var lore = TranslationService.getAllKeysStartingWith("parkour.run_item.lore").stream()
                .map(key -> translate(key, tagResolver))
                .<ComponentWrapper>map(AdventureComponentWrapper::new)
                .toList();

        return new SimpleItem(new ItemBuilder(parkourRun.mode().getMaterial()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(itemName).setLore(lore));
    }

    private static Item statsItem(String playerName, List<ParkourRun> runs) {
        PlayerStats stats = computeStats(runs);

        Component favoriteMode = stats.favoriteMode().getValue() == 0
                ? Component.text("∅")
                : translate("parkour.mode." + stats.favoriteMode().getKey().getKey());

        TagResolver tagResolver = TagResolver.resolver(
                Placeholder.unparsed("name", playerName),
                Placeholder.unparsed("runs_played", String.valueOf(stats.runsPlayed())),
                Placeholder.unparsed("total_duration", TimeUtils.format(stats.totalDuration())),
                Formatter.number("total_score", stats.scoreStats().getSum()),
                Formatter.number("average_score", stats.scoreStats().getAverage()),
                Formatter.number("max_score", stats.scoreStats().getMax()),
                Placeholder.component("favorite_mode", favoriteMode),
                Placeholder.unparsed("favorite_mode_count", String.valueOf(stats.favoriteMode().getValue()))
        );

        var lore = TranslationService.getAllKeysStartingWith("parkour.stats_item.lore").stream()
                .map(key -> translate(key, tagResolver)).<ComponentWrapper>map(AdventureComponentWrapper::new)
                .toList();

        ItemProvider itemProvider;

        try {
            itemProvider = new SkullBuilder(playerName)
                    .setDisplayName(new AdventureComponentWrapper(translate("parkour.stats_item.name", tagResolver)))
                    .setLore(lore);

        } catch (MojangApiUtils.MojangApiException | IOException e) {
            throw new RuntimeException(e);
        }

        return new SimpleItem(itemProvider);
    }

    private static PlayerStats computeStats(List<ParkourRun> runs) {

        Duration totalDuration = runs.parallelStream().map(ParkourRun::duration).reduce(Duration.ZERO, Duration::plus);
        DoubleSummaryStatistics scoreStats = runs.parallelStream().mapToDouble(ParkourRun::score).summaryStatistics();

        var favoriteMode = runs.stream().collect(Collectors.groupingBy(ParkourRun::mode, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .orElse(Map.entry(ParkourModeType.FUN_INFINITE, 0L));

        return new PlayerStats(runs.size(), totalDuration, scoreStats, favoriteMode);
    }

    private record PlayerStats(int runsPlayed, Duration totalDuration, DoubleSummaryStatistics scoreStats,
                               Map.Entry<ParkourModeType, Long> favoriteMode) {
    }

    public static class BackItem extends PageItem {

        public BackItem() {
            super(false);
        }

        @Override
        public ItemProvider getItemProvider(PagedGui<?> gui) {
            ItemBuilder builder = new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
            builder.setDisplayName("Page précédente")
                    .addLoreLines(gui.hasPreviousPage()
                            ? "Aller à la page " + gui.getCurrentPage() + "/" + gui.getPageAmount()
                            : "Tu es à la première page");
            return builder;
        }

    }

    public static class ForwardItem extends PageItem {

        public ForwardItem() {
            super(true);
        }

        @Override
        public ItemProvider getItemProvider(PagedGui<?> gui) {
            ItemBuilder builder = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE);
            builder.setDisplayName("Page suivante")
                    .addLoreLines(gui.hasNextPage()
                            ? "Aller à la page " + gui.getCurrentPage() + "/" + gui.getPageAmount()
                            : "Tu es à la dernière page");

            return builder;
        }

    }

}
