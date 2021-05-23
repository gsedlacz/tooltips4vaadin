package dev.mett.vaadin.tooltip.example;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.Route;
import dev.mett.vaadin.tooltip.example.provider.BasicStateExample;

@Route("")
public class BasicDemo extends FlexLayout {

  private static final long serialVersionUID = 7591127437515385460L;

  public BasicDemo() {
    new BasicStateExample().exampleBasic(this);
  }
}