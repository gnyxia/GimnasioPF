package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "porteria", layout = MainLayout.class)
public class PorteriaView extends VerticalLayout {

    private final Almacen almacen;
    private final TextField documento = new TextField("Documento");
    private final VerticalLayout resultado = new VerticalLayout();

    public PorteriaView(Almacen almacen) {
        this.almacen = almacen;

        documento.setWidthFull();
        documento.setMaxWidth("500px");

        Button entrada = new Button("ENTRADA", e -> procesar(true));
        Button salida = new Button("SALIDA", e -> procesar(false));
        entrada.getElement().setAttribute("theme", "primary")
        for (Button b : new Button[]{entrada, salida}) {
            b.setWidthFull();
            b.setHeight("60px");
        }
        HorizontalLayout botones = new HorizontalLayout(entrada, salida);
        botones.setWidthFull();
        botones.setMaxWidth("500px");

        resultado.setPadding(false);
        resultado.setMaxWidth("500px");

        add(new H2("Portería"), documento, botones, resultado);
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
        if (r.exito()) documento.clear();
    }
}
