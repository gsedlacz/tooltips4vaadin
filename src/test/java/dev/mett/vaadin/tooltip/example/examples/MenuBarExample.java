package dev.mett.vaadin.tooltip.example.examples;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import dev.mett.vaadin.tooltip.Tooltips;

public class MenuBarExample {
  private Component getMenuBarExample() {
    MenuBar menuBar = new MenuBar();
    Text selected = new Text("");
    Div message = new Div(new Text("Selected: "), selected);

    MenuItem project = menuBar.addItem("Project");
    Tooltips.getCurrent().setTooltip(project, "A project!");

    MenuItem account = menuBar.addItem("Account");
    menuBar.addItem("Sign Out", e -> selected.setText("Sign Out"));

    SubMenu projectSubMenu = project.getSubMenu();
    MenuItem users = projectSubMenu.addItem("Users");
    Tooltips.getCurrent().setTooltip(users, "And a user!");

    MenuItem billing = projectSubMenu.addItem("Billing");

    SubMenu usersSubMenu = users.getSubMenu();
    usersSubMenu.addItem("List", e -> selected.setText("List"));
    usersSubMenu.addItem("Add", e -> selected.setText("Add"));

    SubMenu billingSubMenu = billing.getSubMenu();
    billingSubMenu.addItem("Invoices", e -> selected.setText("Invoices"));
    billingSubMenu.addItem("Balance Events",
        e -> selected.setText("Balance Events"));

    account.getSubMenu().addItem("Edit Profile",
        e -> selected.setText("Edit Profile"));
    account.getSubMenu().addItem("Privacy Settings",
        e -> selected.setText("Privacy Settings"));

    return new VerticalLayout(menuBar, message);
  }
}
