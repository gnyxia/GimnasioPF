package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.*;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "porteria", layout = MainLayout.class)
public class PorteriaView extends VerticalLayout {

    private final Almacen almacen;
    private final TextField documento = new TextField("Documento");
    private final Div resultado = new Div();

    public PorteriaView(Almacen almacen) {
        this.almacen = almacen;
        setAlignItems(Alignment.CENTER);
        setPadding(true);

        H2 titulo = new H2("Portería");
        titulo.getStyle().set("margin", "0");
        Span subtitulo = new Span("Registra la entrada o salida del miembro.");
        subtitulo.getStyle().set("opacity", "0.7");

        documento.setPlaceholder("Escribe el documento del miembro");
        documento.setPrefixComponent(VaadinIcon.USER.create());
        documento.setWidthFull();
        documento.setClearButtonVisible(true);
        documento.setAllowedCharPattern("[0-9]");
        documento.setMaxLength(12);
        documento.setAutofocus(true);

        Button entrada = new Button("ENTRADA", VaadinIcon.SIGN_IN.create(), e -> procesar(true));
        Button salida = new Button("SALIDA", VaadinIcon.SIGN_OUT.create(), e -> procesar(false));
        entrada.getElement().setAttribute("theme", "primary");
        entrada.addClickShortcut(Key.ENTER); // Enter = registrar entrada
        entrada.setHeight("56px");
        salida.setHeight("56px");

        HorizontalLayout botones = new HorizontalLayout(entrada, salida);
        botones.setWidthFull();
        botones.setFlexGrow(1, entrada, salida);

        resultado.setWidthFull();

        // Tarjeta centrada; los colores vienen del tema, así sirve en claro y oscuro
        VerticalLayout tarjeta = new VerticalLayout(titulo, subtitulo, documento, botones, resultado);
        tarjeta.setWidthFull();
        tarjeta.setMaxWidth("480px");
        tarjeta.setPadding(true);
        tarjeta.setSpacing(true);
        tarjeta.getStyle()
                .set("background", "var(--vaadin-background-container)")
                .set("border", "1px solid rgba(128, 128, 128, 0.25)")
                .set("border-radius", "16px")
                .set("box-shadow", "0 4px 16px rgba(0, 0, 0, 0.12)");

        add(tarjeta);
    }

    private void procesar(boolean esEntrada) {
        String error = Validador.documento(documento.getValue());
        if (error != null) {
            Notification.show(error, 4000, Notification.Position.MIDDLE);
            return;
        }
        String doc = documento.getValue().trim();
        Almacen.Resultado r = esEntrada ? almacen.registrarEntrada(doc) : almacen.registrarSalida(doc);
        resultado.removeAll();
        resultado.add(Componentes.mensaje(r.mensaje(), r.exito()));
        if (r.exito()) {
            documento.clear();
            documento.focus();
        }
    }
}