package me.crylonz;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobsManagerTest {

    @Test
    void asMyEnumRecognizesKnownEntityTypes() {
        assertTrue(MobsManager.asMyEnum("zombie"));
        assertTrue(MobsManager.asMyEnum("PLAYER"));
        assertTrue(MobsManager.asMyEnum("DROPPED_ITEM"));
        assertFalse(MobsManager.asMyEnum("not_an_entity"));
    }

    @Test
    void isUsefulEntityRejectsNonMobEntities() {
        assertFalse(MobsManager.isUsefulEntity(EntityType.ITEM));
        assertFalse(MobsManager.isUsefulEntity(EntityType.ARROW));
        assertFalse(MobsManager.isUsefulEntity(EntityType.PLAYER));
        assertFalse(MobsManager.isUsefulEntity(EntityType.BLOCK_DISPLAY));
    }

    @Test
    void isUsefulEntityKeepsRealMobEntities() {
        assertTrue(MobsManager.isUsefulEntity(EntityType.ZOMBIE));
        assertTrue(MobsManager.isUsefulEntity(EntityType.CREEPER));
        assertTrue(MobsManager.isUsefulEntity(EntityType.IRON_GOLEM));
    }

    @Test
    void normalizeEntityTypeNameMapsLegacyAliasesToCurrentNames() {
        assertTrue(MobsManager.matchesEntityTypeName("DROPPED_ITEM", EntityType.ITEM));
        assertTrue(MobsManager.matchesEntityTypeName("MINECART_CHEST", EntityType.CHEST_MINECART));
        assertTrue(MobsManager.matchesEntityTypeName("PIG_ZOMBIE", EntityType.ZOMBIFIED_PIGLIN));
    }

    @Test
    void shouldCancelSpawnMapsNewReasonsToExistingCategories() {
        MobsData mob = new MobsData("ZOMBIE", "world", true, false, false, false, false, false, false);

        assertTrue(MobsManager.shouldCancelSpawn(mob, CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER));
        assertTrue(MobsManager.shouldCancelSpawn(mob, CreatureSpawnEvent.SpawnReason.COMMAND));
        assertTrue(MobsManager.shouldCancelSpawn(mob, CreatureSpawnEvent.SpawnReason.DISPENSE_EGG));
        assertTrue(MobsManager.shouldCancelSpawn(mob, CreatureSpawnEvent.SpawnReason.REINFORCEMENTS));
    }

    @Test
    void shouldCancelSpawnAllowsConfiguredCategory() {
        MobsData mob = new MobsData("ZOMBIE", "world", true, true, true, true, true, true, true);

        assertFalse(MobsManager.shouldCancelSpawn(mob, CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER));
        assertFalse(MobsManager.shouldCancelSpawn(mob, CreatureSpawnEvent.SpawnReason.COMMAND));
        assertFalse(MobsManager.shouldCancelSpawn(mob, CreatureSpawnEvent.SpawnReason.DISPENSE_EGG));
        assertFalse(MobsManager.shouldCancelSpawn(mob, CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM));
    }
}
