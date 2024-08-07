package fr.d0gma.infinite.modes;

import fr.d0gma.core.translation.TranslationService;
import fr.d0gma.infinite.parkour.Parkour;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;

class ProgressiveMode implements ParkourMode {

    private final ParkourModeType type;
    private final Parkour parkour;

    ProgressiveMode(ParkourModeType type, Parkour parkour) {
        this.type = type;
        this.parkour = parkour;
    }

    @Override
    public ParkourModeType getType() {
        return this.type;
    }

    @Override
    public float getLengthMultiplier() {
        return 1f + (parkour.getCheckpointsReached() / 25f);
    }

    @Override
    public float getDifficultyMultiplier() {
        return 1.2f;
    }

    @Override
    public void editSidebar(Sidebar<Component> sidebar) {
        sidebar.addUpdatableLine(() -> TranslationService.translate("parkour.scoreboard.progressive_multiplier", Formatter.number("multiplier", getLengthMultiplier())));
        sidebar.addBlankLine();
    }
}
