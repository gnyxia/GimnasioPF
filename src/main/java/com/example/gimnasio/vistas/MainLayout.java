package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.Almacen;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class MainLayout extends AppLayout {

    private final Almacen almacen;

    public MainLayout(Almacen almacen) {
        this.almacen = almacen;

        H1 titulo = new H1("Gimnasio");
        titulo.getStyle().set("font-size", "1.2rem").set("margin", "0 0 0 8px");
        addToNavbar(new DrawerToggle(), titulo);

        SideNav menu = new SideNav();
        menu.addItem(new SideNavItem("Miembros", MiembrosView.class, VaadinIcon.USERS.create()));
        menu.addItem(new SideNavItem("Pagos", PagosView.class, VaadinIcon.MONEY.create()));
        // AQUÍ se agregan los demás ítems, uno por rama
        addToDrawer(menu);
    }
}