package dev.mett.vaadin.tooltip.example.examples;

import com.vaadin.flow.component.Component;
import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;
import dev.mett.vaadin.tooltip.example.field.TooltipEmailField;

public class DefaultConfigurationExample {

  public Component exampleDefaultConfig() {
    TooltipConfiguration defaultConf = new TooltipConfiguration();
    defaultConf.setArrow(true);
    defaultConf.setDelay(2000);
    Tooltips.setDefaultTooltipConfiguration(defaultConf);

    TooltipEmailField field = new TooltipEmailField();
    field.setTooltip("test");

    return field;
  }
}
