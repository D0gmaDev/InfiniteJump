package fr.d0gma.infinite.modes;

import fr.d0gma.core.timer.RunnableHelper;
import fr.d0gma.core.timer.Timer;
import fr.d0gma.core.timer.TimerService;
import fr.d0gma.infinite.game.ParkourEndReason;
import fr.d0gma.infinite.parkour.Parkour;
import fr.d0gma.infinite.parkour.ParkourSection;
import fr.d0gma.infinite.players.JumpPlayer;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.time.Duration;

import static fr.d0gma.core.translation.TranslationService.translate;

class DeathReaperMode implements ParkourMode {

    private final ParkourModeType type;
    private final Parkour parkour;

    private Timer timer;
    private BossBar bossBar;

    DeathReaperMode(ParkourModeType type, Parkour parkour) {
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
    public void editSidebar(Sidebar<Component> sidebar) {
        sidebar.addUpdatableLine(() -> this.timer.getStatus() != Timer.Status.ENDED ?
                translate("parkour.scoreboard.reaper_timer", Placeholder.unparsed("timer", this.timer.getDecreasingFormattedValue())) :
                translate("parkour.scoreboard.reaper_dead")
        );
    }

    @Override
    public void init() {
        this.timer = TimerService.createTimer(Duration.ofSeconds(1), Duration.ofSeconds(30), this::updateBar, timer -> parkour.stop(ParkourEndReason.DEATH));
        this.bossBar = BossBar.bossBar(Component.text("DEATH REAPER", NamedTextColor.RED, TextDecoration.BOLD), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    @Override
    public void onPlayerAdd(JumpPlayer jumpPlayer) {
        this.bossBar.addViewer(jumpPlayer.getPlayer());
    }

    @Override
    public void onPlayerRemove(JumpPlayer jumpPlayer) {
        this.bossBar.removeViewer(jumpPlayer.getPlayer());
    }

    private void updateBar(Timer timer) {
        this.bossBar.progress((timer.getMaxValue() - timer.getCurrentValue()) / 120.5f);
    }

    @Override
    public void onCheckpoint(ParkourSection completedSection) {
        int seconds = Math.round(completedSection.score() * 0.6f * Math.max(0.3f, 0.02f * (60 - parkour.getCheckpointsReached())));
        this.bossBar.color(BossBar.Color.WHITE);
        RunnableHelper.runLaterAsynchronously(() -> this.bossBar.color(BossBar.Color.RED), 30);
        this.timer.setMaxValue(Math.min(this.timer.getCurrentValue() + 120, this.timer.getMaxValue() + seconds));
    }

    @Override
    public void onTimerPause() {
        if (this.timer != null) {
            this.timer.pause();
        }
    }

    @Override
    public void onTimerResume() {
        if (this.timer != null) {
            this.timer.start();
        }
    }

    @Override
    public void onEnd() {
        this.timer.cancel();
    }
}
