package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "vencimientos", layout = MainLayout.class)
public class VencimientosView extends VerticalLayout {

    public VencimientosView(Almacen almacen) {
        Grid<Miembro> grid = new Grid<>();
        grid.addComponentColumn(Componentes::avatarNombre).setHeader("Miembro")
                .setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(Miembro::getTelefono).setHeader("Teléfono");
        grid.addColumn(Miembro::getFechaVencimiento).setHeader("Vence");
        grid.addColumn(m -> m.diasRestantes() + " días").setHeader("Faltan");
        grid.setItems(almacen.porVencer());

        add(new H2("Vencen en los próximos 5 días"), grid);
    }
}
