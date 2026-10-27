package com.example.gimnasio.datos;

import java.time.LocalDate;

public record Pago(String documento, LocalDate fecha, double monto, Plan plan) {

    public String aLinea() {
        return documento + "|" + fecha + "|" + monto + "|" + plan.name();
    }
}
