package com.example.gimnasio.datos;

import java.time.LocalDateTime;

public class Asistencia {
    private final String documento;
    private final LocalDateTime entrada;
    private LocalDateTime salida; // null = todavia adentro

    public Asistencia(String documento, LocalDateTime entrada, LocalDateTime salida) {
        this.documento = documento;
        this.entrada = entrada;
        this.salida = salida;
    }

    public String aLinea() {
        return documento + "|" + entrada + "|" + (salida == null ? "" : salida.toString());
    }

    public static Asistencia desdeLinea(String linea) {
        String[] p = linea.split("\\|", -1); // el -1 conserva el campo vacío final
        LocalDateTime salida = p[2].isEmpty() ? null : LocalDateTime.parse(p[2]);
        return new Asistencia(p[0], LocalDateTime.parse(p[1]), salida);
    }

    public String getDocumento() { return documento; }
    public LocalDateTime getEntrada() { return entrada; }
    public LocalDateTime getSalida() { return salida; }
    public void setSalida(LocalDateTime salida) { this.salida = salida;}
}
