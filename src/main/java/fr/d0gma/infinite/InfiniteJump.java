package fr.d0gma.infinite;

import fr.d0gma.core.Core;
import fr.d0gma.core.translation.TranslationService;
import fr.d0gma.core.utils.GameUtils;
import fr.d0gma.infinite.command.LeaveCommand;
import fr.d0gma.infinite.command.ParkourCommand;
import fr.d0gma.infinite.command.SeedCommand;
import fr.d0gma.infinite.database.DatabaseManager;
import fr.d0gma.infinite.game.Lobby;
import fr.d0gma.infinite.listeners.JumpListeners;
import fr.d0gma.infinite.listeners.PlayerListeners;
import fr.d0gma.infinite.players.JumpPlayerService;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class InfiniteJump extends JavaPlugin {

    private static final String VOID_WORLD_NAME = "infiniteJumpWorld";
    private static InfiniteJump instance;
    private static Lobby lobby;

    private DatabaseManager databaseManager;

    private int position;

    private void loadWorld() {
        World voidWorld = GameUtils.createVoidWorld(VOID_WORLD_NAME);
        voidWorld.setPVP(false);
        voidWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        voidWorld.setGameRule(GameRule.NATURAL_REGENERATION, false);
        voidWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        voidWorld.setFullTime(6000);

        voidWorld.setThundering(false);
        voidWorld.setStorm(false);

        NamespacedKey positionKey = new NamespacedKey(this, "position");
        this.position = voidWorld.getPersistentDataContainer().getOrDefault(positionKey, PersistentDataType.INTEGER, 1);
        Bukkit.getLogger().info("Loaded position " + this.position);
    }

    @Override
    public void onDisable() {
        World voidWorld = getParkourWorld();
        NamespacedKey positionKey = new NamespacedKey(this, "position");
        voidWorld.getPersistentDataContainer().set(positionKey, PersistentDataType.INTEGER, getNextPosition());
    }

    @Override
    public void onEnable() {

        instance = this;

        Core.initialize(this);
        TranslationService.loadTranslations(Objects.requireNonNull(getResource("lang/fr.yml")));

        loadWorld();

        lobby = new Lobby();
        lobby.load(getParkourWorld());

        JumpPlayerService jumpPlayerService = new JumpPlayerService(this, lobby);

        Bukkit.getServer().getPluginManager().registerEvents(new JumpListeners(jumpPlayerService, lobby), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);

        Objects.requireNonNull(getCommand("parkour")).setExecutor(new ParkourCommand(jumpPlayerService));
        Objects.requireNonNull(getCommand("leave")).setExecutor(new LeaveCommand(jumpPlayerService, lobby));
        Objects.requireNonNull(getCommand("seed")).setExecutor(new SeedCommand(jumpPlayerService));

        /*CommandService commandService = apiInstance.getServiceManager().getCommandService();
        commandService.registerRuntimeCommand(new ParkourCommand(jumpPlayerService));
        commandService.registerRuntimeCommand(new JoinCommand(jumpPlayerService));
        commandService.registerRuntimeCommand(new SpectateCommand(jumpPlayerService));
        commandService.registerRuntimeCommand(new LeaveCommand(jumpPlayerService, lobby));
        commandService.registerRuntimeCommand(new SeedCommand(jumpPlayerService));*/

        getDataFolder().mkdir();

        this.databaseManager = new DatabaseManager();
        this.databaseManager.createTable();
    }

    public World getParkourWorld() {
        return Bukkit.getWorld(VOID_WORLD_NAME);
    }

    public int getNextPosition() {
        return this.position++;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public static InfiniteJump getInstance() {
        return instance;
    }

    public static Lobby getLobby() {
        return lobby;
    }
}
