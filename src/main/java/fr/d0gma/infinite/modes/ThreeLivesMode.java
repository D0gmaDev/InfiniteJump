package fr.d0gma.infinite.modes;

import fr.d0gma.core.translation.TranslationService;
import fr.d0gma.infinite.game.ParkourEndReason;
import fr.d0gma.infinite.parkour.Parkour;
import fr.d0gma.infinite.players.JumpPlayer;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

class ThreeLivesMode implements ParkourMode {

    private final ParkourModeType type;
    private final Parkour parkour;

    private int lives = 3;

    ThreeLivesMode(ParkourModeType type, Parkour parkour) {
        this.type = type;
        this.parkour = parkour;
    }

    @Override
    public ParkourModeType getType() {
        return this.type;
    }

    @Override
    public boolean isManualRespawnAllowed() {
        return false;
    }

    @Override
    public float getDifficultyMultiplier() {
        return 1.5f;
    }

    @Override
    public void editSidebar(Sidebar<Component> sidebar) {
        sidebar.addUpdatableLine(() -> TranslationService.translate("parkour.scoreboard.three_lives", Placeholder.unparsed("lives", String.valueOf(this.lives))));
        sidebar.addBlankLine();
    }

    @Override
    public void onPlayerAdd(JumpPlayer jumpPlayer) {
        setHealth(jumpPlayer);
    }

    @Override
    public boolean onRespawn(Player player) {
        lives--;

        if (lives == 0) {
            parkour.stop(ParkourEndReason.DEATH);
            return false;
        }

        parkour.getPlayers().forEach(this::setHealth);
        return true;
    }

    private void setHealth(JumpPlayer jumpPlayer) {
        jumpPlayer.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.lives * 2);
        jumpPlayer.getPlayer().setHealth(this.lives * 2);
    }
}
