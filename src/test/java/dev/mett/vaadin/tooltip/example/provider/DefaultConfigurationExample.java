package dev.mett.vaadin.tooltip.example.provider;

import com.vaadin.flow.component.Component;
import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;

public class DefaultConfigurationExample {

  public Component exampleDefaultConfig() {
    var defaultConf = new TooltipConfiguration();
    defaultConf.setArrow(true);
    defaultConf.setDelay(2000);
    Tooltips.setDefaultTooltipConfiguration(defaultConf);

    var field = new TooltipEmailField();
    field.setTooltip("test");

    return field;
  }
}
