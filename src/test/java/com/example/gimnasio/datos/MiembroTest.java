package com.example.gimnasio.datos;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiembroTest {

    @Test
    void renovarVigenteSumaAlVencimiento() {
        LocalDate hoy = LocalDate.now();
        Miembro m = new Miembro("1", "A", "3", Plan.MENSUAL, hoy.minusDays(20), hoy.plusDays(10));
        m.renovar(Plan.MENSUAL);
        assertEquals(hoy.plusDays(10).plusMonths(1), m.getFechaVencimiento());
    }

    @Test
    void renovarVencidaCuentaDesdeHoy() {
        LocalDate hoy = LocalDate.now();
        Miembro m = new Miembro("2", "B", "3", Plan.MENSUAL, hoy.minusDays(60), hoy.minusDays(30));
        m.renovar(Plan.TRIMESTRAL);
        assertEquals(hoy.plusMonths(3), m.getFechaVencimiento());
    }

    @Test
    void estadoSegunDiasRestantes() {
        LocalDate hoy = LocalDate.now();
        assertEquals(Estado.ACTIVA, new Miembro("3", "C", "3", Plan.MENSUAL, hoy, hoy.plusDays(20)).getEstado());
        assertEquals(Estado.POR_VENCER, new Miembro("4", "D", "3", Plan.MENSUAL, hoy, hoy.plusDays(5)).getEstado());
        assertEquals(Estado.POR_VENCER, new Miembro("5", "E", "3", Plan.MENSUAL, hoy, hoy).getEstado());
        assertEquals(Estado.VENCIDA, new Miembro("6", "F", "3", Plan.MENSUAL, hoy, hoy.minusDays(1)).getEstado());
    }
}