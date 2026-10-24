package com.example.gimnasio.datos;

public enum Estado {
    ACTIVA("Activa"), POR_VENCER("Por Vencer"), VENCIDA("Vencida");

    private final String etiqueta;

    Estado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta; 
    }
}
