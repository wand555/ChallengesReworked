package wand555.github.io.challengesreworked;

import dev.dejvokep.boostedyaml.YamlDocument;
import wand555.github.io.challengesreworked.challenges.nocrafting.NoCraftingChallengeCommon;
import wand555.github.io.challengesreworked.goals.GoalCommon;
import wand555.github.io.challengesreworked.goals.itemcollect.ItemCollectGoalCommon;
import wand555.github.io.challengesreworked.goals.mob.MobGoalCommon;
import wand555.github.io.challengesreworked.punishments.AffectType;
import wand555.github.io.challengesreworked.punishments.health.HealthPunishmentCommon;
import wand555.github.io.challengesreworked.punishments.randomitem.RandomItemPunishmentCommon;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import wand555.github.io.challengesreworked.challenges.PluginChallenge;
import wand555.github.io.challengesreworked.challenges.nocrafting.PluginNoCraftingChallenge;
import wand555.github.io.challengesreworked.goals.ChallengeEnding;
import wand555.github.io.challengesreworked.goals.PluginGoal;
import wand555.github.io.challengesreworked.goals.itemcollect.PluginItemCollectGoal;
import wand555.github.io.challengesreworked.goals.mob.PluginMobGoal;
import wand555.github.io.challengesreworked.logging.ChatLogger;
import wand555.github.io.challengesreworked.punishment.health.PluginHealthPunishment;
import wand555.github.io.challengesreworked.punishment.PluginPunishment;
import wand555.github.io.challengesreworked.punishment.randomitem.PluginRandomItemPunishment;
import wand555.github.io.challengesreworked.timer.TimeOrder;
import wand555.github.io.challengesreworked.timer.Timer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChallengeManager {

    private static ChallengeManager instance;

    private final ChallengesReworked plugin;
    private final NamespacedKey key;

    private Collection<UUID> players;
    private GameState gameState;
    private Collection<PluginChallenge> activePluginChallenges;
    private Collection<PluginGoal> goals;
    private Timer timer;


    private final LanguageHandler languageHandler;

    private ChallengeManager() {

        this.plugin = JavaPlugin.getPlugin(ChallengesReworked.class);
        this.key = new NamespacedKey(plugin, "already_collected");
        this.gameState = GameState.SET_UP;
        this.players = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
        //this.players = new HashSet<>();
        this.languageHandler = new LanguageHandler();
        languageHandler.setCurrentLocale(Locale.ENGLISH);
        this.activePluginChallenges = new HashSet<>();
        this.goals = new HashSet<>();
        this.timer = new Timer(TimeOrder.ASC);
    }

    public void start() throws IllegalStateException{
        /*if(gameState != GameState.SET_UP) {
            throw new IllegalStateException("TODODODODO");
        }*/
        this.players = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
        Set<PluginPunishment> punishments = Set.of(
                new PluginRandomItemPunishment(new RandomItemPunishmentCommon(0, AffectType.CAUSER, 1)),
                new PluginHealthPunishment(new HealthPunishmentCommon(0, AffectType.CAUSER, 2)));
        PluginNoCraftingChallenge challenge = new PluginNoCraftingChallenge(new NoCraftingChallengeCommon());
        challenge.setPunishments(punishments);
        activePluginChallenges.add(challenge);
        Map<EntityType, Collect> map1 = new HashMap<>();
        map1.put(EntityType.PIG, new Collect(2));
        PluginGoal mobGoal = new PluginMobGoal(new MobGoalCommon(map1));
        goals.add(mobGoal);
        Map<Material, Collect> map = new HashMap<>();
        map.put(Material.STONE, new Collect(3));
        map.put(Material.COBBLESTONE, new Collect(2));
        goals.add(new PluginItemCollectGoal(new ItemCollectGoalCommon(false, map)));
        timer.start();
        gameState = GameState.RUNNING;
        ChatLogger.log("run.start");
    }

    public void pause() throws IllegalStateException {
        /*if(gameState != GameState.RUNNING) {
            throw new IllegalStateException();
        }*/
        gameState = GameState.PAUSED;
    }

    public void resume() throws IllegalStateException {
        if(gameState != GameState.PAUSED) {
            throw new IllegalStateException();
        }
        gameState = GameState.RUNNING;
    }

    public void end(ChallengeEnding ending) {
        players.forEach(uuid -> Bukkit.getPlayer(uuid).setGameMode(GameMode.SPECTATOR));

        gameState = GameState.ENDED;
        ChatLogger.log("run.end");
    }

    public void saveDataToFile() {
        try {
            YamlDocument storage = YamlDocument.create(new File(plugin.getDataFolder(), "data_storage.yml"));
            //storage.setGeneralSettings(GeneralSettings.builder().setSerializer(SpigotSerializer.getInstance()).build());
            storage.clear();
            //storage.set("players", players);
            storage.set("time", timer.getElapsedTime());
            gameState = GameState.PAUSED;
            storage.set("state", gameState.toString());
            storage.set("goals", new ArrayList<>(goals).stream().map(Commonable::getCommon).toList());
            storage.set("challenges", new ArrayList<>(activePluginChallenges).stream().map(Commonable::getCommon).toList());
            storage.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDataFromFile() {
        try {
            YamlDocument storage = YamlDocument.create(
                    new File(plugin.getDataFolder(), "data_storage.yml"));
            //storage.setGeneralSettings(GeneralSettings.builder().setSerializer(SpigotSerializer.getInstance()).build());
            //players = (Collection<UUID>) storage.getList("players");
            long elapsedTime = storage.getLong("time", 0L);
            timer = new Timer(elapsedTime, TimeOrder.ASC);
            timer.start();
            gameState = storage.getEnum("state", GameState.class, GameState.SET_UP);
            Collection<GoalCommon> goalCommons = ((List<GoalCommon>)storage.getList("goals", new ArrayList<>()));
            goals = goalCommons.stream().map(goalCommon -> (PluginGoal) Wrapper.wrap(goalCommon)).toList();
            activePluginChallenges = (Collection<PluginChallenge>) storage.getList("challenges", new ArrayList<>());
            System.out.println(activePluginChallenges);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean allGoalsComplete() {
        return goals.stream().allMatch(PluginGoal::isComplete);
    }

    public boolean isRunning() {
        return gameState == GameState.RUNNING;
    }

    public boolean isInChallenge(UUID uuid) {
        return players.contains(uuid);
    }

    public Collection<UUID> getPlayers() {
        return Collections.unmodifiableCollection(players);
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public ChallengesReworked getPlugin() {
        return plugin;
    }

    public PluginManager getPluginManager() {
        return plugin.getServer().getPluginManager();
    }

    public NamespacedKey getKey() {
        return key;
    }

    public static ChallengeManager getInstance() {
        if(instance == null) {
            instance = new ChallengeManager();
        }
        return instance;
    }
}
