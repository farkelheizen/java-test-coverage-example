package com.example.service;

import com.example.model.Order;
import com.example.model.Shipment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private Order order;

    @InjectMocks
    private ShippingService shippingService;

    // ─── calculateShippingRate ────────────────────────────────────────────────

    @Test
    void calculateShippingRate_weightUpTo1_domesticNotExpress() {
        double rate = shippingService.calculateShippingRate(0.5, "DOMESTIC", false);
        assertEquals(5.99, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_weightUpTo5_domesticNotExpress() {
        double rate = shippingService.calculateShippingRate(3.0, "DOMESTIC", false);
        assertEquals(9.99, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_weightUpTo20_domesticNotExpress() {
        double rate = shippingService.calculateShippingRate(10.0, "DOMESTIC", false);
        assertEquals(19.99, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_weightOver20_domesticNotExpress() {
        double rate = shippingService.calculateShippingRate(25.0, "DOMESTIC", false);
        assertEquals(39.99, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_international_multipliesBy3() {
        double rate = shippingService.calculateShippingRate(0.5, "INTERNATIONAL", false);
        assertEquals(5.99 * 3.0, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_internationalCaseInsensitive_multipliesBy3() {
        double rate = shippingService.calculateShippingRate(0.5, "international", false);
        assertEquals(5.99 * 3.0, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_remote_multipliesBy15() {
        double rate = shippingService.calculateShippingRate(0.5, "REMOTE", false);
        assertEquals(5.99 * 1.5, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_remoteCaseInsensitive_multipliesBy15() {
        double rate = shippingService.calculateShippingRate(0.5, "remote", false);
        assertEquals(5.99 * 1.5, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_express_doublesRate() {
        double rate = shippingService.calculateShippingRate(0.5, "DOMESTIC", true);
        assertEquals(5.99 * 2.0, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_internationalExpress_multipliesBy6() {
        double rate = shippingService.calculateShippingRate(0.5, "INTERNATIONAL", true);
        assertEquals(5.99 * 3.0 * 2.0, rate, 1e-9);
    }

    @Test
    void calculateShippingRate_otherDestination_noMultiplier() {
        double rate = shippingService.calculateShippingRate(0.5, "LOCAL", false);
        assertEquals(5.99, rate, 1e-9);
    }

    // ─── createShipment ───────────────────────────────────────────────────────

    @Test
    void createShipment_nullOrder_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> shippingService.createShipment(null, "FedEx"));
    }

    @Test
    void createShipment_nullCarrier_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> shippingService.createShipment(order, null));
    }

    @Test
    void createShipment_emptyCarrier_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> shippingService.createShipment(order, ""));
    }

    @Test
    void createShipment_validArgs_returnsShipmentWithCorrectCarrier() {
        Shipment shipment = shippingService.createShipment(order, "FedEx");
        assertEquals("FedEx", shipment.getCarrier());
    }

    @Test
    void createShipment_validArgs_returnsShipmentWithCorrectOrder() {
        Shipment shipment = shippingService.createShipment(order, "UPS");
        assertEquals(order, shipment.getOrder());
    }

    @Test
    void createShipment_validArgs_isDeliveredFalse() {
        Shipment shipment = shippingService.createShipment(order, "DHL");
        assertFalse(shipment.isDelivered());
    }

    @Test
    void createShipment_validArgs_trackingNumberStartsWithTRK() {
        Shipment shipment = shippingService.createShipment(order, "UPS");
        assertTrue(shipment.getTrackingNumber().startsWith("TRK"));
    }

    @Test
    void createShipment_validArgs_estimatedDeliveryIsInFuture() {
        Shipment shipment = shippingService.createShipment(order, "UPS");
        assertTrue(shipment.getEstimatedDelivery().isAfter(LocalDate.now()));
    }

    // ─── estimateDeliveryDays ─────────────────────────────────────────────────

    @Test
    void estimateDeliveryDays_local_notExpress_returns1() {
        assertEquals(1, shippingService.estimateDeliveryDays("LOCAL", false));
    }

    @Test
    void estimateDeliveryDays_local_express_returns1() {
        // Math.max(1, 1/2) = Math.max(1, 0) = 1
        assertEquals(1, shippingService.estimateDeliveryDays("LOCAL", true));
    }

    @Test
    void estimateDeliveryDays_domestic_notExpress_returns3() {
        assertEquals(3, shippingService.estimateDeliveryDays("DOMESTIC", false));
    }

    @Test
    void estimateDeliveryDays_domestic_express_returns1() {
        // Math.max(1, 3/2) = Math.max(1, 1) = 1
        assertEquals(1, shippingService.estimateDeliveryDays("DOMESTIC", true));
    }

    @Test
    void estimateDeliveryDays_regional_notExpress_returns5() {
        assertEquals(5, shippingService.estimateDeliveryDays("REGIONAL", false));
    }

    @Test
    void estimateDeliveryDays_regional_express_returns2() {
        // Math.max(1, 5/2) = Math.max(1, 2) = 2
        assertEquals(2, shippingService.estimateDeliveryDays("REGIONAL", true));
    }

    @Test
    void estimateDeliveryDays_international_notExpress_returns14() {
        assertEquals(14, shippingService.estimateDeliveryDays("INTERNATIONAL", false));
    }

    @Test
    void estimateDeliveryDays_international_express_returns7() {
        // Math.max(1, 14/2) = Math.max(1, 7) = 7
        assertEquals(7, shippingService.estimateDeliveryDays("INTERNATIONAL", true));
    }

    @Test
    void estimateDeliveryDays_unknownDestination_notExpress_returns7() {
        assertEquals(7, shippingService.estimateDeliveryDays("UNKNOWN", false));
    }

    @Test
    void estimateDeliveryDays_unknownDestination_express_returns3() {
        // Math.max(1, 7/2) = Math.max(1, 3) = 3
        assertEquals(3, shippingService.estimateDeliveryDays("UNKNOWN", true));
    }

    @Test
    void estimateDeliveryDays_caseInsensitive_local() {
        assertEquals(1, shippingService.estimateDeliveryDays("local", false));
    }
}
