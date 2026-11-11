package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.Estado;
import com.example.gimnasio.datos.Miembro;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.badge.Badge;
import com.vaadin.flow.component.badge.BadgeVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

public class Componentes {

    // Logica Avatar
    public static Component avatarNombre(Miembro m) {
        Avatar avatar = new Avatar(m.getNombre()); // saca solo las iniciales

        Span nombre = new Span(m.getNombre());
        Span documento = new Span(m.getDocumento());
        documento.getStyle().set("font-size", "0.8em").set("opacity", "0.7");

        VerticalLayout textos = new VerticalLayout(nombre, documento);
        textos.setPadding(false);
        textos.setSpacing(false);

        HorizontalLayout fila = new HorizontalLayout(avatar, textos);
        fila.setAlignItems(FlexComponent.Alignment.CENTER);
        fila.setPadding(false);
        return fila;
    }

    // Logica Badge
    public static Badge badge(Miembro m) {
        Estado estado = m.getEstado();
        Badge badge = new Badge(estado.getEtiqueta());
        switch (estado) {
            case ACTIVA -> badge.addThemeVariants(BadgeVariant.SUCCESS);
            case POR_VENCER -> badge.addThemeVariants(BadgeVariant.WARNING);
            case VENCIDA -> badge.addThemeVariants(BadgeVariant.ERROR);
        }
        return badge;
    }

    // Logica progressbar
    public static Component progreso(Miembro m) {
        double valor = (double) m.diasRestantes() / m.diasTotales();
        ProgressBar barra = new ProgressBar();
        barra.setValue(Math.max(0, Math.min(1, valor))); // nunca negativo ni mayor que 1
        barra.setWidthFull();
        String texto = m.diasRestantes() < 0 ? "Vencida" : m.diasRestantes() + " días";
        VerticalLayout caja = new VerticalLayout(barra, new Span(texto));
        caja.setPadding(false);
        caja.setSpacing(false);
        return  caja;
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
        div.getStyle().set("background", exito
                        ? "var(--lumo-success-color-10pct)"
                        : "var(--lumo-error-color-10pct)")
                .set("color", exito
                        ? "var(--lumo-success-text-color)"
                        : "var(--lumo-error-text-color)")
                .set("padding", "var(--lumo-space-m)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("font-weight", "600")
                .set("text-align", "center");
        return div;
    }
}
