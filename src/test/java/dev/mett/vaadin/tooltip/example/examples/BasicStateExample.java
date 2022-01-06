package dev.mett.vaadin.tooltip.example.examples;

import com.vaadin.flow.component.Component;
import dev.mett.vaadin.tooltip.example.field.TooltipEmailField;
import dev.mett.vaadin.tooltip.shared.LayoutWithActionButtons;

public class BasicStateExample {

  public Component getBasicExample() {
    TooltipEmailField emailField = new TooltipEmailField();
    emailField.setHeight("fit-content");
    emailField.setTooltip("initial tooltip");

    return new LayoutWithActionButtons(emailField, true);
  }
}
