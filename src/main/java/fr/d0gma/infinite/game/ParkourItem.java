package fr.d0gma.infinite.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static fr.d0gma.core.translation.TranslationService.translate;

public class ParkourItem {

    public static ItemStack menuItem() {
        return item(Material.COMPASS, translate("parkour.item.menu"));
    }

    public static ItemStack historyItem() {
        return item(Material.KNOWLEDGE_BOOK, translate("parkour.item.history"));
    }

    public static ItemStack checkpointItem() {
        return item(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, translate("parkour.item.checkpoint"));
    }

    public static ItemStack skipItem() {
        return item(Material.BARRIER, translate("parkour.item.skip"));
    }

    public static ItemStack replayItem() {
        return item(Material.PAPER, translate("parkour.item.replay"));
    }

    private static ItemStack item(Material material, Component displayName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(displayName.decoration(TextDecoration.ITALIC, State.FALSE));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
