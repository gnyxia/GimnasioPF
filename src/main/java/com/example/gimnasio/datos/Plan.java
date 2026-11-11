package com.example.gimnasio.datos;

public enum Plan {
    MENSUAL(1), TRIMESTRAL(3), ANUAL(12);

    private final int meses;

    Plan(int meses) { this.meses = meses; }

    public int getMeses() {
        return meses;
    }

    public static Plan desdeDuracion(java.time.LocalDate inicio, java.time.LocalDate vencimiento) {
        for (Plan plan : values()) {
            if (inicio.plusMonths(plan.meses).equals(vencimiento)) {
                return plan;
            }
        }
        throw new IllegalArgumentException(
                "La duración del miembro no coincide con un plan válido: "
                        + inicio + " - " + vencimiento);
    }
}
