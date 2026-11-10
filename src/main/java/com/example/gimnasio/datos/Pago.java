package com.example.gimnasio.datos;

import java.time.LocalDate;

public record Pago(String documento, LocalDate fecha, double monto, Plan plan) {

    public String aLinea() {
        return documento + "|" + fecha + "|" + monto + "|" + plan.name();
    }

    public static Pago desdeLinea(String linea) {
        String[] p = linea.split("\\|", -1);
        return new Pago(p[0], LocalDate.parse(p[1]), Double.parseDouble(p[2]), Plan.valueOf(p[3]));
    }
}
