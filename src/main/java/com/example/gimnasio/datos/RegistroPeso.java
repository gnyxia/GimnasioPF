package com.example.gimnasio.datos;

import java.time.LocalDate;

public record RegistroPeso(String documento, LocalDate fecha, Double peso) {

    public String aLinea() {
        return documento + fecha + peso;
    }

    public static RegistroPeso desdeLinea(String linea) {
        String[] p = linea.split("\\|");
        return new RegistroPeso(p[0], LocalDate.parse(p[1]), Double.parseDouble(p[2]));
    }
}
