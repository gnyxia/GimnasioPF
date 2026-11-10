package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.Miembro;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Componentes {

    public static Component avatarNombre(Miembro m) {
        Avatar avatar = new Avatar(m.getNombre()); // saca solo las iniciales

        Span nombre = new Span(m.getNombre());
        Span documento = new Span(m.getDocumento());
        documento.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)");

        VerticalLayout textos = new VerticalLayout(nombre, documento);
        textos.setPadding(false);
        textos.setSpacing(false);

        HorizontalLayout fila = new HorizontalLayout(avatar, textos);
        fila.setAlignItems(FlexComponent.Alignment.CENTER);
        fila.setPadding(false);
        return fila;
    }

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
