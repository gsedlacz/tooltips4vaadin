package dev.mett.vaadin.tooltip.example.provider;

import com.vaadin.flow.component.Component;
import dev.mett.vaadin.tooltip.config.TC_PLACEMENT;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;

public class ExtendedConfigurationExample {

  public Component exampleExtendedConfiguration() {
    TooltipEmailField field = new TooltipEmailField();
    field.setTooltip(new TooltipConfigurationExt("test", TC_PLACEMENT.BOTTOM_END));
    return field;
  }

  private class TooltipConfigurationExt extends TooltipConfiguration {

    public TooltipConfigurationExt(String content, TC_PLACEMENT placement) {
      super();

      setContent(content);
      setPlacement(placement);
    }
  }
}
