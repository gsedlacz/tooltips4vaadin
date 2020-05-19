package dev.mett.vaadin.tooltip.mixin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;

import dev.mett.vaadin.tooltip.TooltipConfiguration;
import dev.mett.vaadin.tooltip.Tooltips;

/**
 * Allows to define Tooltips more easily by adding this mixin interface.
 *
 * @author Gerrit Sedlaczek
 */
public interface HasTooltip extends HasElement {

    /**
     * Adds a tooltip to the implementing {@link Component}.
     *
     * @param tooltipConfig the configuration describing a tooltip
     */
    default void setTooltip(TooltipConfiguration tooltipConfig) {
        setTooltip(tooltipConfig, UI.getCurrent());
    }

    /**
     * Adds a tooltip to the implementing {@link Component}.
     *
     * @param <T>           the type of a styleable component
     * @param tooltipConfig the configuration describing a tooltip
     * @param ui            {@link UI}
     */
    @SuppressWarnings("unchecked")
    default <T extends Component & HasStyle> void setTooltip(TooltipConfiguration tooltipConfig, UI ui) {
        getElement().getComponent().ifPresent(comp -> {
            if (comp instanceof HasStyle) {
                Tooltips.get(ui).setTooltip((T) comp, tooltipConfig);
            }
        });
    }

    /**
     * Adds a tooltip to the implementing {@link Component}.
     * 
     * @param tooltip the String to display (may contain html)
     */
    default void setTooltip(String tooltip) {
        setTooltip(tooltip, UI.getCurrent());
    }

    /**
     * Adds a tooltip to the implementing {@link Component}.
     * 
     * @param <T>     the type of a styleable component
     * @param tooltip the String to display (may contain html)
     * @param ui      {@link UI}
     */
    @SuppressWarnings("unchecked")
    default <T extends Component & HasStyle> void setTooltip(String tooltip, UI ui) {
        getElement().getComponent().ifPresent(comp -> {
            if (comp instanceof HasStyle) {
                Tooltips.get(ui).setTooltip((T) comp, tooltip);
            }
        });
    }

    /**
     * Removes a tooltip from the implementing {@link Component}
     * 
     * @param <T> the type of a styleable component
     * @param ui  {@link UI}
     */
    @SuppressWarnings("unchecked")
    default <T extends Component & HasStyle> void removeTooltip(UI ui) {
        getElement().getComponent().ifPresent(comp -> {
            if (comp instanceof HasStyle) {
                Tooltips.get(ui).removeTooltip((T) comp);
            }
        });
    }
}
