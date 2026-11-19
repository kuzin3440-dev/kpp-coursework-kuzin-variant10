package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CourseworkTest {

    @Test
    void f1ZeroSessionsIsZero() {
        assertEquals(0.0, RiskCalculator.factor1(0.0, 50.0), 0.001);
    }

    @Test
    void f1ForDemoLoadIs80() {
        assertEquals(80.0, RiskCalculator.factor1(40.0, 50.0), 0.001);
    }

    @Test
    void priorityIndexForDemoDataIs715() {
        assertEquals(71.50, RiskCalculator.calculate(80.0, 70.0, 60.0), 0.001);
    }

    @Test
    void riskRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(101.0, 70.0, 60.0));
    }

    @Test
    void recordedToDiagnosedAllowed() {
        assertTrue(IncidentService.isAllowedTransition(
                IncidentState.RECORDED, IncidentState.DIAGNOSED));
    }

    @Test
    void diagnosedToRepairAllowed() {
        assertTrue(IncidentService.isAllowedTransition(
                IncidentState.DIAGNOSED, IncidentState.REPAIR));
    }

    @Test
    void repairToRestoredAllowed() {
        assertTrue(IncidentService.isAllowedTransition(
                IncidentState.REPAIR, IncidentState.RESTORED));
    }

    @Test
    void recordedToRestoredRejected() {
        assertFalse(IncidentService.isAllowedTransition(
                IncidentState.RECORDED, IncidentState.RESTORED));
    }

    @Test
    void stationValidationWorks() {
        Station invalidStation = new Station(null, "", "ЕкоЗаряд Центр", "Полтава");
        assertThrows(IllegalArgumentException.class,
                () -> StationService.validate(invalidStation));
    }

    @Test
    void chargingModuleValidationWorks() {
        ChargingModule invalidModule = new ChargingModule(
                null, 1L, "MOD-101", "CCS2", 120.0, 120.0, 60.0);
        assertThrows(IllegalArgumentException.class,
                () -> ChargingModuleService.validate(invalidModule));
    }
}
