package com.example.gimnasio.datos;

public enum Plan {
    MENSUAL(1), TRIMESTRAL(3), ANUAL(12);

    private final int meses;

    Plan(int meses) { this.meses = meses; }

    public int getMeses() {
        return meses;
    }
}
