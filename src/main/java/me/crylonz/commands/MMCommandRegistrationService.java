package me.crylonz.commands;

import me.crylonz.MobsData;
import me.crylonz.MobsManager;
import me.crylonz.MobsManager.MMSpawnType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static me.crylonz.MobsManager.*;

public class MMCommandRegistrationService extends MMCommandRegistration {

    private static final int LIST_PAGE_SIZE = 12;
    private static final String LIST_MODE_ALL = "all";
    private static final String LIST_MODE_CHANGED = "changed";
    private static final String LIST_MODE_BLOCKED = "blocked";
    private static final String errorMsg = ChatColor.WHITE + "[MobsManager]" + ChatColor.RED + " World not found or entity name is incorrect";
    private static final String errorMsgSpawnReason = ChatColor.WHITE + "[MobsManager]" + ChatColor.RED + " Valid reasons are : ALL CUSTOM NATURAL SPAWNER EGG BREEDING";

    public MMCommandRegistrationService(Plugin plugin) {
        super(plugin);
    }

    public void registerReload() {
        registerCommand("mobsmanager reload", "mobsmanager.reload", () -> {
            ((MobsManager) plugin).reloadPluginState();
            sender.sendMessage(ChatColor.GREEN + "[MobsManager] Plugin reload successfully");
        });
    }

    public void registerHelp() {
        registerCommand("mobsmanager help", "mobsmanager.help", () -> {
            sender.sendMessage("[MobsManager]" + ChatColor.GREEN + " List of command");
            sender.sendMessage(ChatColor.GOLD + "/mm reload" + ChatColor.WHITE + " Reload the plugin");
            sender.sendMessage(ChatColor.GOLD + "/mm list <World> [Mode] [Page]" + ChatColor.WHITE + " List entities for a world");
            sender.sendMessage(ChatColor.GOLD + "/mm status <Entity> <World>" + ChatColor.WHITE + " Alias of /mm info");
            sender.sendMessage(ChatColor.GOLD + "/mm enable <Entity> <SpawnReason> <World> " + ChatColor.WHITE + "Enable spawning for a mob");
            sender.sendMessage(ChatColor.GOLD + "/mm disable <Entity> <SpawnReason> <World> " + ChatColor.WHITE + "Disable spawning for a mob");
            sender.sendMessage(ChatColor.GOLD + "/mm info <Entity> <World> " + ChatColor.WHITE + "Display spawning info of a mob");
        });
    }

    public void registerList() {
        registerCommand("mobsmanager list {0}", "mobsmanager.info", this::displayWorldEntityList);
        registerCommand("mobsmanager list {0} {1}", "mobsmanager.info", this::displayWorldEntityList);
        registerCommand("mobsmanager list {0} {1} {2}", "mobsmanager.info", this::displayWorldEntityList);
    }

    private void displayWorldEntityList() {
        String worldName = args[1];
        String mode = LIST_MODE_CHANGED;
        int requestedPage = 1;

        if (args.length >= 3) {
            if (isPositiveInteger(args[2])) {
                requestedPage = Integer.parseInt(args[2]);
            } else {
                mode = args[2].toLowerCase();
            }
        }

        if (args.length >= 4) {
            try {
                requestedPage = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.WHITE + "[MobsManager]" + ChatColor.RED + " Page must be a valid number");
                return;
            }
        }

        if (requestedPage < 1) {
            sender.sendMessage(ChatColor.WHITE + "[MobsManager]" + ChatColor.RED + " Page must be greater than 0");
            return;
        }

        if (!LIST_MODE_ALL.equals(mode) && !LIST_MODE_CHANGED.equals(mode) && !LIST_MODE_BLOCKED.equals(mode)) {
            sender.sendMessage(ChatColor.WHITE + "[MobsManager]" + ChatColor.RED + " Valid list modes are: changed, blocked, all");
            return;
        }
        final String selectedMode = mode;

        List<MobsData> worldEntries = mobsData.stream()
                .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(worldName))
                .sorted(Comparator.comparing(MobsData::getName))
                .collect(Collectors.toList());

        if (worldEntries.isEmpty()) {
            sender.sendMessage(ChatColor.RED + errorMsg);
            return;
        }

        List<MobsData> filteredEntries = worldEntries.stream()
                .filter(mobData -> matchesListMode(mobData, selectedMode))
                .collect(Collectors.toList());

        if (filteredEntries.isEmpty()) {
            sender.sendMessage(ChatColor.WHITE + "[MobsManager] No " + ChatColor.GOLD + mode + ChatColor.WHITE + " entities found on " + ChatColor.GOLD + worldName);
            return;
        }

        List<String> entities = filteredEntries.stream()
                .map(this::formatListEntry)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) entities.size() / LIST_PAGE_SIZE);
        if (requestedPage > totalPages) {
            sender.sendMessage(ChatColor.WHITE + "[MobsManager]" + ChatColor.RED + " Page " + requestedPage + " does not exist. Last page is " + totalPages);
            return;
        }

        int fromIndex = (requestedPage - 1) * LIST_PAGE_SIZE;
        int toIndex = Math.min(fromIndex + LIST_PAGE_SIZE, entities.size());
        List<String> page = entities.subList(fromIndex, toIndex);

        sender.sendMessage(ChatColor.WHITE + "[MobsManager] " + StringUtils.capitalize(selectedMode) + " entities on " + ChatColor.GOLD + worldName
                + ChatColor.WHITE + " (" + ChatColor.GOLD + "page " + requestedPage + "/" + totalPages + ChatColor.WHITE + ")");
        sender.sendMessage(ChatColor.GRAY + "-------------------");

        for (String line : page) {
            sender.sendMessage(ChatColor.WHITE + " - " + line);
        }

        sender.sendMessage(ChatColor.GRAY + "-------------------");
        if (totalPages > 1) {
            sender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.WHITE + "/mm list " + worldName + " " + selectedMode + " <page>" + ChatColor.GRAY + " to navigate pages.");
        }
    }

    private boolean isPositiveInteger(String value) {
        return value != null && value.matches("\\d+");
    }

    private boolean matchesListMode(MobsData mobData, String mode) {
        switch (mode) {
            case LIST_MODE_ALL:
                return true;
            case LIST_MODE_BLOCKED:
                return isBlocked(mobData);
            case LIST_MODE_CHANGED:
            default:
                return isChanged(mobData);
        }
    }

    private boolean isChanged(MobsData mobData) {
        return !mobData.isAllSpawn()
                || !mobData.isCustomSpawn()
                || !mobData.isNaturalSpawn()
                || !mobData.isSpawnerSpawn()
                || !mobData.isEggSpawn()
                || !mobData.isBreedingSpawn()
                || !mobData.isIronGolemSpawn();
    }

    private boolean isBlocked(MobsData mobData) {
        return !mobData.isAllSpawn() || !enabledCategories(mobData).equals(allCategories());
    }

    private String formatListEntry(MobsData mobData) {
        List<String> disabled = disabledCategories(mobData);
        if (disabled.isEmpty()) {
            return ChatColor.GOLD + mobData.getName() + ChatColor.GRAY + " : " + ChatColor.GREEN + "DEFAULT";
        }
        return ChatColor.GOLD + mobData.getName() + ChatColor.GRAY + " : " + ChatColor.RED + String.join(", ", disabled);
    }

    private List<String> disabledCategories(MobsData mobData) {
        List<String> disabled = new ArrayList<>();
        if (!mobData.isAllSpawn()) {
            disabled.add("ALL");
        }
        if (!mobData.isCustomSpawn()) {
            disabled.add("CUSTOM");
        }
        if (!mobData.isNaturalSpawn()) {
            disabled.add("NATURAL");
        }
        if (!mobData.isSpawnerSpawn()) {
            disabled.add("SPAWNER");
        }
        if (!mobData.isEggSpawn()) {
            disabled.add("EGG");
        }
        if (!mobData.isBreedingSpawn()) {
            disabled.add("BREEDING");
        }
        if (!mobData.isIronGolemSpawn()) {
            disabled.add("IRON_GOLEM");
        }
        return disabled;
    }

    private List<String> enabledCategories(MobsData mobData) {
        List<String> enabled = new ArrayList<>();
        if (mobData.isAllSpawn()) {
            enabled.add("ALL");
        }
        if (mobData.isCustomSpawn()) {
            enabled.add("CUSTOM");
        }
        if (mobData.isNaturalSpawn()) {
            enabled.add("NATURAL");
        }
        if (mobData.isSpawnerSpawn()) {
            enabled.add("SPAWNER");
        }
        if (mobData.isEggSpawn()) {
            enabled.add("EGG");
        }
        if (mobData.isBreedingSpawn()) {
            enabled.add("BREEDING");
        }
        if (mobData.isIronGolemSpawn()) {
            enabled.add("IRON_GOLEM");
        }
        return enabled;
    }

    private List<String> allCategories() {
        return List.of("ALL", "CUSTOM", "NATURAL", "SPAWNER", "EGG", "BREEDING", "IRON_GOLEM");
    }

    public void registerStatus() {
        registerCommand("mobsmanager status {0} {1}", "mobsmanager.info", this::displayMobInfo);
    }

    public void registerInfo() {
        registerCommand("mobsmanager info {0} {1}", "mobsmanager.info", this::displayMobInfo);
    }

    public void registerDisable() {
        registerCommand("mobsmanager disable {0} {1} {2}", "mobsmanager.manageEntity", () -> {
            try {
                MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                commandDisable(spawnType, args);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(errorMsgSpawnReason);
            }
        });
    }

    public void registerEnable() {
        registerCommand("mobsmanager enable {0} {1} {2}", "mobsmanager.manageEntity", () -> {
            try {
                MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                commandEnable(spawnType, args);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(errorMsgSpawnReason);
            }
        });

    }

    private void commandDisable(MMSpawnType spawnType, String[] args) {
        if (enableOrDisableMob(args[1], false, spawnType, args[3])) {
            sender.sendMessage((ChatColor.WHITE + "[MobsManager] " + ChatColor.GREEN + StringUtils.capitalize(spawnType.name().toLowerCase())
                    + " " + args[1].toLowerCase() + " spawning is disable on " + args[3].toUpperCase()));
        } else {
            sender.sendMessage(ChatColor.RED + errorMsg);
        }
    }

    private void commandEnable(MMSpawnType spawnType, String[] args) {
        if (enableOrDisableMob(args[1], true, spawnType, args[3])) {
            sender.sendMessage(ChatColor.WHITE + "[MobsManager] " + ChatColor.GREEN + StringUtils.capitalize(spawnType.name().toLowerCase())
                    + " " + args[1].toLowerCase() + " spawning is enable on " + args[3].toUpperCase());
        } else {
            sender.sendMessage(ChatColor.RED + errorMsg);
        }
    }

    private boolean enableOrDisableMob(String mobs, boolean state, MMSpawnType type, String worldName) {
        AtomicBoolean updated = new AtomicBoolean(false);
        EntityType entity = MobsManager.resolveEntityType(mobs);
        if (entity == null || !MobsManager.isUsefulEntity(entity)) {
            return false;
        }

        mobsData.stream()
                .filter(mobData -> MobsManager.matchesEntityTypeName(mobData.getName(), entity))
                .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(worldName) || worldName.equalsIgnoreCase("*"))
                .forEach(mobData -> {
                            updated.set(true);
                            switch (type) {
                                case ALL:
                                    mobData.setAllSpawn(state);
                                    break;
                                case CUSTOM:
                                    mobData.setCustomSpawn(state);
                                    break;
                                case NATURAL:
                                    mobData.setNaturalSpawn(state);
                                    break;
                                case SPAWNER:
                                    mobData.setSpawnerSpawn(state);
                                    break;
                                case EGG:
                                    mobData.setEggSpawn(state);
                                    break;
                                case BREEDING:
                                    mobData.setBreedingSpawn(state);
                                    break;
                                case IRON_GOLEM:
                                    mobData.setIronGolemSpawn(state);
                                    break;
                                default:
                                    updated.set(false);
                                    break;
                            }
                            mobData.setName(entity.name());
                            fileManager.getMobsDataConfig().set("mobs", mobsData);
                            fileManager.saveMobsDataConfig();
                        }
                );

        return updated.get();
    }

    private void displayMobInfo() {
        List<MobsData> matches = mobsData.stream()
                .filter(mobData -> MobsManager.matchesEntityTypeName(args[1], MobsManager.resolveEntityType(mobData.getName())))
                .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(args[2]))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            sender.sendMessage(ChatColor.RED + errorMsg);
            return;
        }

        matches.forEach(data -> {
            sender.sendMessage(ChatColor.WHITE + "[MobsManager] Details of " + ChatColor.GOLD + data.getName() + ChatColor.WHITE + " spawning options on " + ChatColor.GOLD + data.getWorldName());
            sender.sendMessage(ChatColor.WHITE + "-------------------");
            sender.sendMessage(ChatColor.WHITE + "All spawn type : " + (data.isAllSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            sender.sendMessage(ChatColor.WHITE + "Custom spawn type : " + (data.isCustomSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            sender.sendMessage(ChatColor.WHITE + "Egg spawn type : " + (data.isEggSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            sender.sendMessage(ChatColor.WHITE + "Natural spawn type : " + (data.isNaturalSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            sender.sendMessage(ChatColor.WHITE + "Spawner spawn type : " + (data.isSpawnerSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            sender.sendMessage(ChatColor.WHITE + "Breeding spawn type : " + (data.isBreedingSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            sender.sendMessage(ChatColor.WHITE + "Iron Golem spawn type : " + (data.isIronGolemSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            sender.sendMessage(ChatColor.WHITE + "-------------------");
        });
    }
}
