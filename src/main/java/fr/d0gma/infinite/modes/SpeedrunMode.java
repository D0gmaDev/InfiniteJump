package fr.d0gma.infinite.modes;

import fr.d0gma.core.translation.TranslationService;
import fr.d0gma.infinite.game.ParkourEndReason;
import fr.d0gma.infinite.parkour.Parkour;
import fr.d0gma.infinite.parkour.ParkourSection;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;

class SpeedrunMode implements ParkourMode {

    private final ParkourModeType type;
    private final Parkour parkour;

    SpeedrunMode(ParkourModeType type, Parkour parkour) {
        this.type = type;
        this.parkour = parkour;
    }

    @Override
    public ParkourModeType getType() {
        return this.type;
    }

    @Override
    public boolean isSkipAllowed() {
        return false;
    }

    @Override
    public boolean canGenerateNext() {
        return parkour.getCheckpointsReached() + 1 < parkour.getTarget();
    }

    @Override
    public void editSidebar(Sidebar<Component> sidebar) {
        sidebar.addLine(TranslationService.translate("parkour.scoreboard.speed_run_target", Formatter.number("target", parkour.getTarget())));
        sidebar.addBlankLine();
    }

    @Override
    public void init() {
        parkour.setTarget(10);
    }

    @Override
    public void onCheckpoint(ParkourSection completedSection) {
        if (parkour.getCheckpointsReached() >= parkour.getTarget()) {
            parkour.stop(ParkourEndReason.COMPLETE);
        }
    }

    @Override
    public void onEnd() {
        parkour.setTarget(-1);
    }

}
