package dev.mett.vaadin.tooltip.example.views;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import dev.mett.vaadin.tooltip.example.ExampleApplication;
import dev.mett.vaadin.tooltip.example.ExampleRoutes;
import dev.mett.vaadin.tooltip.example.examples.BasicStateExample;

@PreserveOnRefresh
@Route(value = ExampleRoutes.PRESERVE_ON_REFRESH, layout = ExampleApplication.class)
public class PreserveOnRefreshDemoView extends FlexLayout {

  private static final long serialVersionUID = 123415812634786127L;

  public PreserveOnRefreshDemoView() {
    add("PRESERVE ON REFRESH");
    add(new BasicStateExample().getBasicExample());

  }
}
