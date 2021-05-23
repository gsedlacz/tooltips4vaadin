package dev.mett.vaadin.tooltip.example.provider;

import com.vaadin.flow.component.textfield.EmailField;
import dev.mett.vaadin.tooltip.mixin.HasTooltip;
import java.util.logging.Logger;

class TooltipEmailField extends EmailField implements HasTooltip {

  private final Logger log = Logger.getLogger(TooltipEmailField.class.getName());
}
