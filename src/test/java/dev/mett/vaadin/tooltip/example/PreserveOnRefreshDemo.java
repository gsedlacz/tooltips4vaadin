package dev.mett.vaadin.tooltip.example;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import dev.mett.vaadin.tooltip.example.examples.BasicStateExample;

@PreserveOnRefresh
@Route("preserve")
public class PreserveOnRefreshDemo extends FlexLayout {

  private static final long serialVersionUID = 123415812634786127L;

  public PreserveOnRefreshDemo() {
    add("PRESERVE ON REFRESH");
    add(new BasicStateExample().getBasicExample(this));

  }
}