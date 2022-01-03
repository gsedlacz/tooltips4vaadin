package dev.mett.vaadin.tooltip.example.views;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.Route;
import dev.mett.vaadin.tooltip.example.ExampleApplication;
import dev.mett.vaadin.tooltip.example.ExampleRoutes;
import dev.mett.vaadin.tooltip.example.examples.BasicStateExample;

@Route(value = ExampleRoutes.ROOT, layout = ExampleApplication.class)
public class BasicDemoView extends FlexLayout {

  private static final long serialVersionUID = 7591127437515385460L;

  public BasicDemoView() {
    add(new BasicStateExample().getBasicExample());
  }
}
