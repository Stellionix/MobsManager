package me.crylonz;

import me.crylonz.commands.MMCommandExecutor;
import me.crylonz.commands.MMTabCompletion;
import me.crylonz.utils.MobsManagerConfig;
import me.crylonz.utils.MobsManagerUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class MobsManager extends JavaPlugin implements Listener {

    public final static Logger log = Logger.getLogger("Minecraft");
    public static ArrayList<MobsData> mobsData = new ArrayList<>();
    public MobsManagerConfig config = new MobsManagerConfig(this);

    public static boolean worldGuardDetection;
    public static FileManager fileManager;
    private static final Map<String, String> ENTITY_TYPE_ALIASES = createEntityTypeAliases();
    private static final Set<String> NON_USEFUL_ENTITY_TYPES = createNonUsefulEntityTypes();

    static {
        ConfigurationSerialization.registerClass(MobsData.class, "MobsData");
    }


    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        fileManager = new FileManager(this);

        Metrics metrics = new Metrics(this, 15773);

        Bukkit.getWorlds().forEach(world -> {
            for (EntityType entity : EntityType.values()) {
                if (isUsefulEntity(entity))
                    mobsData.add(new MobsData(entity.name(), world.getName(), true, true, true, true, true, true, true));
            }
        });

        //Updater from 4.X to 5.X
        if (!fileManager.getMobsDataFile().exists() && fileManager.getConfigFile().exists()) {
            fileManager.getConfigFile().renameTo(fileManager.getMobsDataFile());
            log.warning("[MobsManager] Old configuration detected ( < 5.0.0 )");
            log.warning("[MobsManager] Applying configuration migration...");
        }

        registerConfig();

        if (!fileManager.getMobsDataFile().exists()) {
            getConfig().options().header("PLEASE DON'T EDIT THIS FILE");
        } else {
            // Merging maybe new world configuration with existing one
            ArrayList<MobsData> tmp = (ArrayList<MobsData>) fileManager.getMobsDataConfig().get("mobs");
            if (tmp != null) {
                tmp.forEach(mobData -> mobData.setName(normalizeEntityTypeName(mobData.getName())));
            }
            mobsData.removeAll(tmp);
            mobsData.addAll(tmp);
        }

        fileManager.getMobsDataConfig().set("mobs", mobsData);
        fileManager.saveMobsDataConfig();

        if (!fileManager.getConfigFile().exists()) {
            saveDefaultConfig();
        } else {
            config.updateConfig();
        }

        if (config.getBoolean("auto-update")) {
            MobsManagerUpdater updater = new MobsManagerUpdater(this, 322365, this.getFile(), MobsManagerUpdater.UpdateType.DEFAULT, true);
        }

        worldGuardDetection = config.getBoolean("world-guard-detection");

        Objects.requireNonNull(this.getCommand("mobsmanager"), "Command mobsmanager not found")
                .setExecutor(new MMCommandExecutor(this));
        Objects.requireNonNull(getCommand("mobsmanager")).setTabCompleter(new MMTabCompletion());
    }


    public void registerConfig() {
        config.register("auto-update", true);
        config.register("world-guard-detection", false);
    }

    public void onDisable() {
        log.info("[MobsManager] is disabled !");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e == null)
            return;

        // Do nothing if WorldGuard spawn flag exists to prevent spawn issues
        if (worldGuardCheck(e)) return;

        if (mobsData != null) {
            Optional<Boolean> isCancelled = mobsData
                    .stream()
                    .filter(mobsData -> mobsData.getWorldName().equalsIgnoreCase(e.getEntity().getWorld().getName()))
                    .filter(mobsData -> matchesEntityTypeName(mobsData.getName(), e.getEntityType()))
                    .findFirst()
                    .map(mobData -> {
                        if (!mobData.isAllSpawn()) {
                            return true;
                        } else {
                            switch (e.getSpawnReason()) {
                                case NATURAL:
                                case DEFAULT:
                                    return !mobData.isNaturalSpawn();
                                case CUSTOM:
                                    return !mobData.isCustomSpawn();
                                case SPAWNER:
                                    return !mobData.isSpawnerSpawn();
                                case SPAWNER_EGG:
                                    return !mobData.isEggSpawn();
                                case BREEDING:
                                    return !mobData.isBreedingSpawn();
                                case BUILD_IRONGOLEM:
                                    return !mobData.isIronGolemSpawn();
                                default:
                                    return false;
                            }
                        }
                    });
            e.setCancelled(isCancelled.orElse(false));
        }
    }

    private static boolean worldGuardCheck(CreatureSpawnEvent e) {
        if (worldGuardDetection) {
            try {
                return WorldGuarderChecker.check(e);
            } catch (NoClassDefFoundError exception) {
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent e) {
        if (e.getChunk().isLoaded()) {
            if (mobsData != null && mobsData.size() > 0) {
                Arrays.stream(e.getChunk().getEntities())
                        .forEach(entity -> {
                            mobsData
                                    .stream()
                                    .filter(mobData -> matchesEntityTypeName(mobData.getName(), entity.getType()))
                                    .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(entity.getWorld().getName()))
                                    .filter(mobsData -> mobsData.getWorldName().equalsIgnoreCase(entity.getWorld().getName()))
                                    .forEach(mobData -> {
                                        if (!mobData.isAllSpawn() || !mobData.isNaturalSpawn()) {
                                            entity.remove();
                                        }
                                    });
                        });

                for (Entity entity : e.getChunk().getEntities()) {
                    for (MobsData mobData : mobsData) {
                        if (matchesEntityTypeName(mobData.getName(), entity.getType())) {
                            if (!mobData.isAllSpawn() || !mobData.isNaturalSpawn()) {
                                entity.remove();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public enum MMSpawnType {
        ALL,
        CUSTOM,
        NATURAL,
        SPAWNER,
        EGG,
        BREEDING,
        IRON_GOLEM
    }

    public static boolean asMyEnum(String str) {
        return resolveEntityType(str) != null;
    }

    public static boolean isUsefulEntity(EntityType e) {
        return !NON_USEFUL_ENTITY_TYPES.contains(normalizeEntityTypeName(e.name()));
    }

    public static EntityType resolveEntityType(String name) {
        if (name == null) {
            return null;
        }

        String normalizedName = normalizeEntityTypeName(name);
        for (EntityType entityType : EntityType.values()) {
            if (entityType.name().equalsIgnoreCase(normalizedName)) {
                return entityType;
            }
        }
        return null;
    }

    public static boolean matchesEntityTypeName(String name, EntityType entityType) {
        return entityType != null && normalizeEntityTypeName(name).equalsIgnoreCase(entityType.name());
    }

    public static String normalizeEntityTypeName(String name) {
        if (name == null) {
            return "";
        }

        String upperName = name.trim().toUpperCase();
        return ENTITY_TYPE_ALIASES.getOrDefault(upperName, upperName);
    }

    private static Map<String, String> createEntityTypeAliases() {
        Map<String, String> aliases = new HashMap<>();

        aliases.put("DROPPED_ITEM", "ITEM");
        aliases.put("LEASH_HITCH", "LEASH_KNOT");
        aliases.put("ENDER_SIGNAL", "EYE_OF_ENDER");
        aliases.put("SPLASH_POTION", "POTION");
        aliases.put("THROWN_EXP_BOTTLE", "EXPERIENCE_BOTTLE");
        aliases.put("PRIMED_TNT", "TNT");
        aliases.put("FIREWORK", "FIREWORK_ROCKET");
        aliases.put("MINECART_COMMAND", "COMMAND_BLOCK_MINECART");
        aliases.put("MINECART_CHEST", "CHEST_MINECART");
        aliases.put("MINECART_FURNACE", "FURNACE_MINECART");
        aliases.put("MINECART_TNT", "TNT_MINECART");
        aliases.put("MINECART_HOPPER", "HOPPER_MINECART");
        aliases.put("MINECART_MOB_SPAWNER", "SPAWNER_MINECART");
        aliases.put("ENDER_CRYSTAL", "END_CRYSTAL");
        aliases.put("FISHING_HOOK", "FISHING_BOBBER");
        aliases.put("LIGHTNING", "LIGHTNING_BOLT");
        aliases.put("PIG_ZOMBIE", "ZOMBIFIED_PIGLIN");

        return aliases;
    }

    private static Set<String> createNonUsefulEntityTypes() {
        return new HashSet<>(Arrays.asList(
                "ITEM",
                "EXPERIENCE_ORB",
                "AREA_EFFECT_CLOUD",
                "EGG",
                "LEASH_KNOT",
                "PAINTING",
                "ARROW",
                "SNOWBALL",
                "FIREBALL",
                "SMALL_FIREBALL",
                "ENDER_PEARL",
                "EYE_OF_ENDER",
                "POTION",
                "EXPERIENCE_BOTTLE",
                "ITEM_FRAME",
                "WITHER_SKULL",
                "TNT",
                "FALLING_BLOCK",
                "FIREWORK_ROCKET",
                "SPECTRAL_ARROW",
                "SHULKER_BULLET",
                "DRAGON_FIREBALL",
                "ARMOR_STAND",
                "EVOKER_FANGS",
                "COMMAND_BLOCK_MINECART",
                "ILLUSIONER",
                "BOAT",
                "MINECART",
                "CHEST_MINECART",
                "FURNACE_MINECART",
                "TNT_MINECART",
                "HOPPER_MINECART",
                "SPAWNER_MINECART",
                "LLAMA_SPIT",
                "END_CRYSTAL",
                "TRIDENT",
                "FISHING_BOBBER",
                "LIGHTNING_BOLT",
                "PLAYER",
                "GLOW_ITEM_FRAME",
                "MARKER",
                "CHEST_BOAT",
                "BLOCK_DISPLAY",
                "INTERACTION",
                "ITEM_DISPLAY",
                "TEXT_DISPLAY",
                "UNKNOWN"
        ));
    }
}
