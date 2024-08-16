package fr.d0gma.infinite.parkour;

import com.destroystokyo.paper.ParticleBuilder;
import fr.d0gma.core.team.ScoreboardTeam;
import fr.d0gma.core.team.ScoreboardTeamService;
import fr.d0gma.core.timer.RunnableHelper;
import fr.d0gma.core.timer.Timer;
import fr.d0gma.core.timer.TimerService;
import fr.d0gma.core.utils.TimeUtils;
import fr.d0gma.infinite.InfiniteJump;
import fr.d0gma.infinite.database.ParkourRun;
import fr.d0gma.infinite.game.ParkourEndReason;
import fr.d0gma.infinite.game.ParkourItem;
import fr.d0gma.infinite.modes.ParkourMode;
import fr.d0gma.infinite.modes.ParkourModeType;
import fr.d0gma.infinite.players.JumpPlayer;
import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import static fr.d0gma.core.translation.TranslationService.translate;

public class Parkour {

    private final ParkourMetadata parkourMetadata;
    private final ParkourMode parkourMode;

    private final List<JumpPlayer> players = new ArrayList<>();

    private final Timer timer;
    private final ScoreboardTeam<JumpPlayer> team;
    private final Sidebar<Component> sidebar;
    private final BossBar progressionBar;

    private final ParkourGenerator parkourGenerator;
    private final ParkourRoadmap parkourRoadmap;

    private boolean checkpointSkipped = false;

    private int target = Integer.MAX_VALUE;
    private Instant start;

    private double score = 0;

    public Parkour(ParkourModeType parkourModeType, long seed) {
        this.parkourMetadata = new ParkourMetadata(UUID.randomUUID(), parkourModeType, seed);

        this.parkourMode = parkourModeType.createMode(this);
        this.timer = createParkourTimer();

        /* Generation setup */
        int nextPos = InfiniteJump.getInstance().getNextPosition();
        Block spawn = InfiniteJump.getInstance().getParkourWorld().getBlockAt(nextPos * 1000, 100, 0);

        Random random = new Random(seed);

        this.parkourGenerator = new ParkourGenerator(random, spawn, this, this.parkourMode.getAllowedZones());
        this.parkourRoadmap = new ParkourRoadmap(spawn);

        this.team = ScoreboardTeamService.registerScoreboardTeam(JumpPlayer.class, "PK-" + parkourMetadata.uuid().toString().substring(0, 5), Component.empty());
        this.team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);

        this.parkourMode.init();

        this.sidebar = createSidebar(translate("parkour.scoreboard.title"));
        this.progressionBar = BossBar.bossBar(translate("parkour.boss_bar.before_start"), 1f, Color.WHITE, Overlay.PROGRESS);
    }

    public void startParkour(JumpPlayer owner) {
        this.parkourRoadmap.getStartBlock().setType(Material.GOLD_BLOCK);
        generateNextSection();
        generateNextSection();
        addPlayer(owner);
        this.parkourRoadmap.incrementCurrentSegmentId();
    }

    private Timer createParkourTimer() {
        return TimerService.createTimer(Duration.ofSeconds(1), null, timer -> {
            this.players.forEach(jumpPlayer -> {
                jumpPlayer.getPlayer().sendActionBar(translate("parkour.zone." + this.parkourRoadmap.getZone(jumpPlayer.getPlayer().getLocation().getBlockZ()).name().toLowerCase()));

                if (jumpPlayer.getPlayer().getLocation().getY() < 40) {
                    RunnableHelper.runSynchronously(() -> respawnPlayer(jumpPlayer.getPlayer()));
                }
            });
        }, null);
    }

    private Sidebar<Component> createSidebar(Component title) {
        Sidebar<Component> sidebar = ProtocolSidebar.newAdventureSidebar(title, InfiniteJump.getInstance());
        sidebar.addBlankLine();
        sidebar.addLine(translate("parkour.scoreboard.mode", Placeholder.component("mode", translate("parkour.mode." + this.parkourMode.getType().getKey()))));
        sidebar.addUpdatableLine(() -> translate("parkour.scoreboard.checkpoint", Placeholder.unparsed("checkpoint", String.valueOf(this.parkourRoadmap.getCheckpointReached()))));
        sidebar.addUpdatableLine(() -> translate("parkour.scoreboard.score", Placeholder.unparsed("score", String.valueOf(this.score))));
        sidebar.addBlankLine();
        sidebar.addUpdatableLine(() -> translate("parkour.scoreboard.timer", Placeholder.unparsed("timer", this.timer.getIncreasingFormattedValue())));
        sidebar.addBlankLine();
        this.parkourMode.editSidebar(sidebar);
        sidebar.updateLinesPeriodically(5, 5);
        sidebar.getObjective().scoreNumberFormatBlank();
        return sidebar;
    }

    private void generateNextSection() {
        if (!this.parkourMode.canGenerateNext()) {
            return;
        }

        ParkourSection nextSection = this.parkourGenerator.generateNewSection(this.parkourRoadmap.getLastGeneratedSection(), this.parkourMode.getLengthMultiplier());
        this.parkourRoadmap.addSection(nextSection);
    }

    private void startPendingTask() {
        Consumer<Timer> pendingTask = pendingTimer -> {
            players.forEach(jumpPlayer -> jumpPlayer.getPlayer().sendActionBar(translate("parkour.message.action_bar_before_start")));

            if (hasAnyPlayerMoved()) {
                this.start = Instant.now();
                this.timer.start();
                this.parkourMode.onTimerResume();
                pendingTimer.cancel();
                players.forEach(jumpPlayer -> jumpPlayer.playSound(Sound.BLOCK_LANTERN_FALL, 1f, 0.5f));
                this.progressionBar.name(translate("parkour.boss_bar.in_game")).progress(0f).color(Color.BLUE).overlay(Overlay.NOTCHED_10);
            }
        };

        TimerService.createTimer(Duration.ofMillis(50), null, pendingTask, null).start();
    }

    private boolean hasAnyPlayerMoved() {
        return players.stream().mapToInt(jumpPlayer -> jumpPlayer.getPlayer().getLocation().getBlockZ()).anyMatch(z -> z != 0);
    }

    public void addPlayer(JumpPlayer player) {
        this.players.add(player);

        player.setParkour(this);
        player.setLastParkour(this);

        player.setSidebar(this.sidebar);
        player.getPlayer().showBossBar(this.progressionBar);

        if (this.players.size() > 1) {
            this.players.forEach(jumpPlayer -> jumpPlayer.getPlayer().getInventory().remove(Material.BARRIER));
        }

        this.parkourMode.onPlayerAdd(player);

        this.team.addPlayer(player);

        spawnInParkour(player.getPlayer());

        if (this.getPlayers().size() == 1) {
            RunnableHelper.runLaterSynchronously(this::startPendingTask, 20);
        }
    }

    public void removePlayer(JumpPlayer player) {
        if (!this.players.contains(player)) {
            return;
        }

        if (players.size() == 1) {
            stop(ParkourEndReason.LEAVE);
            return;
        }

        this.players.remove(player);

        player.setSidebar(null);
        player.getPlayer().hideBossBar(this.progressionBar);
        player.setParkour(null);

        if (this.players.size() == 1 && this.parkourMode.isSkipAllowed()) {
            this.players.getFirst().getPlayer().getInventory().setItem(8, ParkourItem.skipItem());
        }
        this.team.removePlayer(player);
        this.parkourMode.onPlayerRemove(player);
    }

    public long getSeed() {
        return this.parkourMetadata.seed();
    }

    public void checkpoint(Block block) {
        ParkourSection currentSection = currentSection();

        if (!block.getRelative(BlockFace.DOWN).equals(currentSection.endBlock())) {
            return;
        }

        this.parkourRoadmap.incrementCurrentSegmentId();

        if (checkpointSkipped) {
            checkpointSkipped = false;
        } else {
            this.score += currentSection.score() * this.parkourMode.getDifficultyMultiplier() * this.parkourMode.getLengthMultiplier();
        }

        this.progressionBar.progress(currentCompletion());

        if (this.parkourRoadmap.getCheckpointReached() != this.target) {
            this.players.forEach(jumpPlayer -> {
                jumpPlayer.sendMessage(translate("parkour.message.checkpoint_reached", Placeholder.unparsed("number", String.valueOf(this.parkourRoadmap.getCheckpointReached()))));
                jumpPlayer.playSound(Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1f, 1.2f);
            });
            new ParticleBuilder(Particle.VILLAGER_HAPPY).count(3).receivers(this.players.stream().map(JumpPlayer::getPlayer).toList())
                    .location(block.getLocation().add(.4, .2, .4)).spawn()
                    .location(block.getLocation().add(.6, .4, .6)).spawn();
        }

        RunnableHelper.runSynchronously(this::generateNextSection);

        this.parkourMode.onCheckpoint(currentSection);
    }

    private float currentCompletion() {
        int step = this.target == Integer.MAX_VALUE ? 10 : this.target;
        return ((float) this.parkourRoadmap.getCheckpointReached() % step) / step;
    }

    private void teleportPlayerToCheckpoint(Player player) {
        player.setVelocity(new Vector());
        player.teleportAsync(currentSection().startBlock().getLocation().clone().add(0.5, 1.5, 0.5));
    }

    private void spawnInParkour(Player player) {
        teleportPlayerToCheckpoint(player);

        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        if (this.parkourMode.isManualRespawnAllowed()) {
            player.getInventory().setItem(4, ParkourItem.checkpointItem());
        }

        if (this.players.size() == 1 && this.parkourMode.isSkipAllowed()) {
            player.getInventory().setItem(8, ParkourItem.skipItem());
        }
    }

    public void respawnPlayer(Player player) {
        if (this.parkourMode.onRespawn(player)) {
            teleportPlayerToCheckpoint(player);
            player.setCooldown(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 20);
        }
    }

    public void skip() {
        if (players.size() > 1) {
            players.forEach(jumpPlayer -> jumpPlayer.sendMessage(translate("parkour.message.skip_multi")));
            return;
        }

        if (!this.parkourMode.isSkipAllowed()) {
            players.forEach(jumpPlayer -> jumpPlayer.getPlayer().getInventory().remove(Material.BARRIER));
            return;
        }

        this.checkpointSkipped = true;

        JumpPlayer jumpPlayer = players.getFirst();

        jumpPlayer.getPlayer().setCooldown(Material.BARRIER, 20 * 5);
        jumpPlayer.sendMessage(translate("parkour.message.skipped"));

        jumpPlayer.getPlayer().setVelocity(new Vector());
        jumpPlayer.getPlayer().teleport(currentSection().endBlock().getLocation().clone().add(0.5, 1.5, 0.5));

        this.parkourMode.onSkip();
    }

    public void stop(ParkourEndReason reason) {
        this.timer.cancel();
        this.sidebar.destroy();

        Instant now = Instant.now();
        Duration duration = this.start == null ? Duration.ZERO : Duration.between(this.start, now);

        getPlayers().forEach(player -> {
            RunnableHelper.runSynchronously(() -> InfiniteJump.getLobby().teleport(player));

            player.setSidebar(null);
            player.getPlayer().hideBossBar(this.progressionBar);
            player.setParkour(null);

            this.parkourMode.onPlayerRemove(player);

            if (this.start == null) {
                return;
            }

            TagResolver tagResolver = TagResolver.resolver(
                    Placeholder.component("mode", translate("parkour.mode." + this.parkourMode.getType().getKey())),
                    Placeholder.unparsed("seed", Long.toHexString(getSeed())),
                    Placeholder.unparsed("score", String.valueOf(this.score)),
                    Placeholder.unparsed("duration", TimeUtils.format(duration))
            );

            boolean ranked = !this.parkourMode.getInvalidRankedEndReasons().contains(reason);

            player.sendMessage(translate("parkour.message.end_message", tagResolver));
            RunnableHelper.runAsynchronously(() -> InfiniteJump.getInstance().getDatabaseManager().insertRun(new ParkourRun(player.getUniqueId(), player.getPlayerName(), this.parkourMode.getType(), ranked, getSeed(), this.score, duration, now)));
        });

        this.parkourMode.onEnd();
        ScoreboardTeamService.deleteScoreboardTeam(this.team);
    }

    public List<JumpPlayer> getPlayers() {
        return this.players;
    }

    public int getCheckpointsReached() {
        return this.parkourRoadmap.getCheckpointReached();
    }

    public UUID getUuid() {
        return this.parkourMetadata.uuid();
    }

    public int getTarget() {
        return this.target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    private ParkourSection currentSection() {
        return this.parkourRoadmap.getCurrentSection();
    }

    public void startCopyFor(JumpPlayer owner) {
        Parkour copy = new Parkour(this.parkourMetadata.parkourModeType(), this.parkourMetadata.seed());
        RunnableHelper.runSynchronously(() -> copy.startParkour(owner));
    }

    @Override
    public String toString() {
        return "Parkour{" +
               "uuid=" + parkourMetadata.uuid() +
               ", modeType=" + parkourMode.getType().getKey() +
               ", seed=" + parkourMetadata.seed() +
               ", target=" + target +
               ", start=" + start +
               ", checkpointReached=" + this.parkourRoadmap.getCheckpointReached() +
               ", score=" + score +
               '}';
    }
}
