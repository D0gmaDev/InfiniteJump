package fr.d0gma.infinite.database;

import fr.d0gma.core.utils.TimeUtils;
import fr.d0gma.infinite.InfiniteJump;
import fr.d0gma.infinite.modes.ParkourModeType;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.d0gma.core.translation.TranslationService.translate;

public class TopInventory {

    public static void open(Player player, ParkourModeType mode, long seed) {

        Component bookTitle = Component.text("Top Book");
        Component bookAuthor = Component.text("InfiniteJump");

        List<Component> pages = new ArrayList<>();

        List<Component> lines = new ArrayList<>();

        lines.add(translate("parkour.top_book.title", List.of(Placeholder.component("mode", translate("parkour.mode." + mode.getKey())), Placeholder.unparsed("seed", Long.toHexString(seed)))));

        List<ParkourRun> topRuns = InfiniteJump.getInstance().getDatabaseManager().getTopRunsFor(mode, seed, 9);

        int i = 0;
        for (ParkourRun topRun : topRuns) {
            if (lines.size() == 5) {
                pages.add(Component.join(JoinConfiguration.newlines(), lines));
                lines.clear();
            }

            var tagResolver = TagResolver.resolver(
                    Placeholder.unparsed("rank", String.valueOf(++i)),
                    Placeholder.unparsed("id", String.valueOf(topRun.id())),
                    Placeholder.unparsed("name", topRun.playerName()),
                    Placeholder.unparsed("score", String.valueOf(topRun.score())),
                    Placeholder.unparsed("duration", TimeUtils.format(topRun.duration())),
                    Placeholder.unparsed("date", TimeUtils.formatDateToCET(topRun.instant()))
            );
            lines.add(translate("parkour.top_book.record", tagResolver));
        }

        pages.add(Component.join(JoinConfiguration.newlines(), lines));

        Book myBook = Book.book(bookTitle, bookAuthor, pages);
        player.openBook(myBook);
    }
}
