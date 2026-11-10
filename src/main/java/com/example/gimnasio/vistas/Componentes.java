package com.example.gimnasio.vistas;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class Componentes {

    // Toda eliminacion pasa por aqui
    public static void confirmar(String titulo, String mensaje, Runnable accion) {
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle(titulo);
        dialogo.add(new Span(mensaje));

        Button eliminar = new Button("Eliminar", e -> {
            dialogo.close();
            accion.run();
        });
        eliminar.getElement().setAttribute("theme", "primary error");

        dialogo.getFooter().add(new Button("Cancelar", e -> dialogo.close()), eliminar);
        dialogo.open();
    }

    // Tarjeta verde o roja para los mensajes de porteria
    public static Div mensaje(String texto, boolean exito) {
        Div div = new Div(texto);
        div.getStyle().set("background", exito ? "#2e7d32" : "#c62828")
                .set("color", "white").set("padding", "16px")
                .set("border-radius", "8px").set("font-weight", "bold")
                .set("text-align", "center");
        return div;
    }
}
