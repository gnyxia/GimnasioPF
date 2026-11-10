package com.example.gimnasio.datos;

public record Rutina(String documento, String texto) {

    public String aLinea() {
        return documento + "|" + texto.replace("\r", "").replace("\n", "¬").replace("|", "/");
    }

    public static Rutina desdeLinea(String linea) {
        String[] p = linea.split("\\|", -1);
        return new Rutina(p[0], p[1].replace("¬", "\n"));
    }
}
