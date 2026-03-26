package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographyServiceTest {

    private GeographyService service;

    @BeforeEach
    void setUp() {
        service = new GeographyService();
    }

    // ── calculateDistance ──────────────────────────────────────────────────

    @Test
    void calculateDistance_samePoint_returnsZero() {
        double distance = service.calculateDistance(0, 0, 0, 0);
        assertEquals(0.0, distance, 1e-9);
    }

    @Test
    void calculateDistance_newYorkToLondon_approximatelyCorrect() {
        // NYC (40.7128, -74.0060) → London (51.5074, -0.1278)
        // Known distance ≈ 5570 km
        double distance = service.calculateDistance(40.7128, -74.0060, 51.5074, -0.1278);
        assertTrue(distance > 5500 && distance < 5700,
                "Expected ~5570 km but got " + distance);
    }

    @Test
    void calculateDistance_antipodes_approximatelyHalfCircumference() {
        // Two antipodal points should be ~20015 km apart (half of earth's circumference)
        double distance = service.calculateDistance(0, 0, 0, 180);
        assertEquals(20015.0, distance, 50.0);
    }

    @Test
    void calculateDistance_poleToEquator_approximatelyCorrect() {
        // North Pole to equator ≈ 10008 km
        double distance = service.calculateDistance(90, 0, 0, 0);
        assertEquals(10008.0, distance, 20.0);
    }

    // ── getRegion ──────────────────────────────────────────────────────────

    @Test
    void getRegion_unitedStates_returnsNorthAmerica() {
        assertEquals("NORTH_AMERICA", service.getRegion("US"));
    }

    @Test
    void getRegion_canada_returnsNorthAmerica() {
        assertEquals("NORTH_AMERICA", service.getRegion("CA"));
    }

    @Test
    void getRegion_mexico_returnsNorthAmerica() {
        assertEquals("NORTH_AMERICA", service.getRegion("MX"));
    }

    @Test
    void getRegion_greatBritain_returnsEurope() {
        assertEquals("EUROPE", service.getRegion("GB"));
    }

    @Test
    void getRegion_germany_returnsEurope() {
        assertEquals("EUROPE", service.getRegion("DE"));
    }

    @Test
    void getRegion_france_returnsEurope() {
        assertEquals("EUROPE", service.getRegion("FR"));
    }

    @Test
    void getRegion_italy_returnsEurope() {
        assertEquals("EUROPE", service.getRegion("IT"));
    }

    @Test
    void getRegion_spain_returnsEurope() {
        assertEquals("EUROPE", service.getRegion("ES"));
    }

    @Test
    void getRegion_netherlands_returnsEurope() {
        assertEquals("EUROPE", service.getRegion("NL"));
    }

    @Test
    void getRegion_china_returnsAsiaPacific() {
        assertEquals("ASIA_PACIFIC", service.getRegion("CN"));
    }

    @Test
    void getRegion_japan_returnsAsiaPacific() {
        assertEquals("ASIA_PACIFIC", service.getRegion("JP"));
    }

    @Test
    void getRegion_korea_returnsAsiaPacific() {
        assertEquals("ASIA_PACIFIC", service.getRegion("KR"));
    }

    @Test
    void getRegion_australia_returnsAsiaPacific() {
        assertEquals("ASIA_PACIFIC", service.getRegion("AU"));
    }

    @Test
    void getRegion_india_returnsAsiaPacific() {
        assertEquals("ASIA_PACIFIC", service.getRegion("IN"));
    }

    @Test
    void getRegion_singapore_returnsAsiaPacific() {
        assertEquals("ASIA_PACIFIC", service.getRegion("SG"));
    }

    @Test
    void getRegion_brazil_returnsLatinAmerica() {
        assertEquals("LATIN_AMERICA", service.getRegion("BR"));
    }

    @Test
    void getRegion_argentina_returnsLatinAmerica() {
        assertEquals("LATIN_AMERICA", service.getRegion("AR"));
    }

    @Test
    void getRegion_chile_returnsLatinAmerica() {
        assertEquals("LATIN_AMERICA", service.getRegion("CL"));
    }

    @Test
    void getRegion_colombia_returnsLatinAmerica() {
        assertEquals("LATIN_AMERICA", service.getRegion("CO"));
    }

    @Test
    void getRegion_southAfrica_returnsAfrica() {
        assertEquals("AFRICA", service.getRegion("ZA"));
    }

    @Test
    void getRegion_nigeria_returnsAfrica() {
        assertEquals("AFRICA", service.getRegion("NG"));
    }

    @Test
    void getRegion_egypt_returnsAfrica() {
        assertEquals("AFRICA", service.getRegion("EG"));
    }

    @Test
    void getRegion_kenya_returnsAfrica() {
        assertEquals("AFRICA", service.getRegion("KE"));
    }

    @Test
    void getRegion_unknown_returnsOther() {
        assertEquals("OTHER", service.getRegion("XX"));
    }

    @Test
    void getRegion_lowercaseInput_worksViaNormalization() {
        // toUpperCase() is called internally
        assertEquals("NORTH_AMERICA", service.getRegion("us"));
    }

    // ── isValidCoordinate ──────────────────────────────────────────────────

    @Test
    void isValidCoordinate_validCenter_returnsTrue() {
        assertTrue(service.isValidCoordinate(0, 0));
    }

    @Test
    void isValidCoordinate_validMaxBoundaries_returnsTrue() {
        assertTrue(service.isValidCoordinate(90, 180));
    }

    @Test
    void isValidCoordinate_validMinBoundaries_returnsTrue() {
        assertTrue(service.isValidCoordinate(-90, -180));
    }

    @Test
    void isValidCoordinate_latitudeTooHigh_returnsFalse() {
        assertFalse(service.isValidCoordinate(90.1, 0));
    }

    @Test
    void isValidCoordinate_latitudeTooLow_returnsFalse() {
        assertFalse(service.isValidCoordinate(-90.1, 0));
    }

    @Test
    void isValidCoordinate_longitudeTooHigh_returnsFalse() {
        assertFalse(service.isValidCoordinate(0, 180.1));
    }

    @Test
    void isValidCoordinate_longitudeTooLow_returnsFalse() {
        assertFalse(service.isValidCoordinate(0, -180.1));
    }

    @Test
    void isValidCoordinate_typicalCoordinate_returnsTrue() {
        assertTrue(service.isValidCoordinate(51.5074, -0.1278));
    }

    // ── formatCoordinate ───────────────────────────────────────────────────

    @Test
    void formatCoordinate_positiveLatPositiveLon_northEast() {
        String result = service.formatCoordinate(51.5074, 0.1278);
        assertTrue(result.contains("N"), "Expected N in: " + result);
        assertTrue(result.contains("E"), "Expected E in: " + result);
    }

    @Test
    void formatCoordinate_negativeLatPositiveLon_southEast() {
        String result = service.formatCoordinate(-33.8688, 151.2093);
        assertTrue(result.contains("S"), "Expected S in: " + result);
        assertTrue(result.contains("E"), "Expected E in: " + result);
    }

    @Test
    void formatCoordinate_positiveLatNegativeLon_northWest() {
        String result = service.formatCoordinate(40.7128, -74.0060);
        assertTrue(result.contains("N"), "Expected N in: " + result);
        assertTrue(result.contains("W"), "Expected W in: " + result);
    }

    @Test
    void formatCoordinate_negativeLatNegativeLon_southWest() {
        String result = service.formatCoordinate(-34.6037, -58.3816);
        assertTrue(result.contains("S"), "Expected S in: " + result);
        assertTrue(result.contains("W"), "Expected W in: " + result);
    }

    @Test
    void formatCoordinate_zeroZero_northEast() {
        String result = service.formatCoordinate(0.0, 0.0);
        assertTrue(result.contains("N"), "Expected N for lat=0: " + result);
        assertTrue(result.contains("E"), "Expected E for lon=0: " + result);
    }

    @Test
    void formatCoordinate_formatsToFourDecimalPlaces() {
        String result = service.formatCoordinate(1.23456, 2.34567);
        // 1.2346 N, 2.3457 E (rounded to 4 decimal places)
        assertTrue(result.matches(".*\\d\\.\\d{4}°.*"), "Expected 4 decimals in: " + result);
    }
}
