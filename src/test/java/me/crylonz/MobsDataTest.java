package me.crylonz;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobsDataTest {

    @Test
    void serializeAndDeserializeRoundTripPreservesValues() {
        MobsData original = new MobsData(
                "ZOMBIE",
                "world",
                true,
                false,
                true,
                false,
                true,
                false,
                true
        );

        Map<String, Object> serialized = original.serialize();
        MobsData deserialized = MobsData.deserialize(serialized);

        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getWorldName(), deserialized.getWorldName());
        assertEquals(original.isAllSpawn(), deserialized.isAllSpawn());
        assertEquals(original.isNaturalSpawn(), deserialized.isNaturalSpawn());
        assertEquals(original.isCustomSpawn(), deserialized.isCustomSpawn());
        assertEquals(original.isSpawnerSpawn(), deserialized.isSpawnerSpawn());
        assertEquals(original.isEggSpawn(), deserialized.isEggSpawn());
        assertEquals(original.isBreedingSpawn(), deserialized.isBreedingSpawn());
        assertEquals(original.isIronGolemSpawn(), deserialized.isIronGolemSpawn());
    }

    @Test
    void equalityDependsOnTypeAndWorldOnly() {
        MobsData left = new MobsData("SKELETON", "world_nether", true, true, true, true, true, true, true);
        MobsData right = new MobsData("SKELETON", "world_nether", false, false, false, false, false, false, false);
        MobsData differentWorld = new MobsData("SKELETON", "world", true, true, true, true, true, true, true);

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
        assertNotEquals(left, differentWorld);
    }

    @Test
    void serializeContainsExpectedKeys() {
        MobsData data = new MobsData("CREEPER", "world", true, true, false, true, false, true, false);

        Map<String, Object> serialized = data.serialize();

        assertEquals("CREEPER", serialized.get("Name"));
        assertEquals("world", serialized.get("WorldName"));
        assertTrue((Boolean) serialized.get("AllSpawn"));
        assertTrue((Boolean) serialized.get("NaturalSpawn"));
        assertFalse((Boolean) serialized.get("CustomSpawn"));
        assertTrue((Boolean) serialized.get("SpawnerSpawn"));
        assertFalse((Boolean) serialized.get("EggSpawn"));
        assertTrue((Boolean) serialized.get("BreedingSpawn"));
        assertFalse((Boolean) serialized.get("IronGolemSpawn"));
    }
}
