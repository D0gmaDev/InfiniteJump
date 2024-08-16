package fr.d0gma.infinite.modes;

import fr.d0gma.infinite.game.ParkourEndReason;
import fr.d0gma.infinite.parkour.ParkourSection;
import fr.d0gma.infinite.players.JumpPlayer;
import fr.d0gma.infinite.zone.Zone;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface ParkourMode {

    ParkourModeType getType();

    default boolean isSkipAllowed() {
        return true;
    }

    default boolean isManualRespawnAllowed() {
        return true;
    }

    default boolean canGenerateNext() {
        return true;
    }

    default float getLengthMultiplier() {
        return 1f;
    }

    default List<Zone> getAllowedZones() {
        return Zone.getPlayableZones();
    }

    default float getDifficultyMultiplier() {
        return 1f;
    }

    default Set<ParkourEndReason> getInvalidRankedEndReasons() {
        return Set.of();
    }

    default void editSidebar(Sidebar<Component> sidebar) {

    }

    default void init() {

    }

    default void onPlayerAdd(JumpPlayer jumpPlayer) {

    }

    default void onPlayerRemove(JumpPlayer jumpPlayer) {

    }

    default void onCheckpoint(ParkourSection completedSection) {

    }

    default boolean onRespawn(Player player) {
        return true;
    }

    default void onSkip() {

    }

    default void onTimerPause() {

    }

    default void onTimerResume() {

    }

    default void onEnd() {

    }

}
