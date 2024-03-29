package dev.mett.vaadin.tooltip.example.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

public class RootView extends AppLayout implements RouterLayout {
  public RootView() {
    H1 title = new H1("Tooltips4Vaadin - Examples");
    title.getElement().setAttribute("style", "font-size:large;margin:.2em;");
    addToNavbar(new DrawerToggle(), title);

    addToDrawer(getTabs());
  }

  private Tabs getTabs() {
    Tabs tabs = new Tabs(
        new Tab(new RouterLink("Basic", BasicDemoView.class)),
        new Tab(new RouterLink("Basic - PreserveOnRefresh", PreserveOnRefreshDemoView.class)),
        new Tab(new RouterLink("Grid", GridDemoView.class)));

    tabs.setOrientation(Orientation.VERTICAL);

    return tabs;
  }
}
