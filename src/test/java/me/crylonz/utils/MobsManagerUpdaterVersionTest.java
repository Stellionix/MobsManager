package me.crylonz.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MobsManagerUpdaterVersionTest {

    @Test
    void compareVersionPartsTreatsFiveZeroZeroAsNewerThanFourTwentyFourZero() {
        int comparison = MobsManagerUpdater.compareVersionParts(
                MobsManagerUpdater.parseVersionParts("5.0.0"),
                MobsManagerUpdater.parseVersionParts("4.24.0")
        );

        assertEquals(1, comparison);
    }

    @Test
    void compareVersionPartsTreatsMultiDigitSegmentsNumerically() {
        int comparison = MobsManagerUpdater.compareVersionParts(
                MobsManagerUpdater.parseVersionParts("4.10.0"),
                MobsManagerUpdater.parseVersionParts("4.9.9")
        );

        assertEquals(1, comparison);
    }

    @Test
    void compareVersionPartsTreatsMissingSegmentsAsZero() {
        int comparison = MobsManagerUpdater.compareVersionParts(
                MobsManagerUpdater.parseVersionParts("5"),
                MobsManagerUpdater.parseVersionParts("5.0.0")
        );

        assertEquals(0, comparison);
    }

    @Test
    void parseVersionPartsSupportsSuffixesAfterNumericSegments() {
        List<Integer> parts = MobsManagerUpdater.parseVersionParts("5.0.0-RC1");

        assertEquals(List.of(5, 0, 0), parts);
    }

    @Test
    void parseVersionPartsRejectsVersionsWithoutNumericSegments() {
        assertThrows(IllegalArgumentException.class, () -> MobsManagerUpdater.parseVersionParts("SNAPSHOT"));
    }
}
