package dev.mett.vaadin.tooltip.mixin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;

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
   * @param tooltipConfig the configuration describing a tooltip
   * @param ui            {@link UI}
   */
  default void setTooltip(TooltipConfiguration tooltipConfig, UI ui) {
    getElement().getComponent().ifPresent(comp ->
        Tooltips.get(ui).setTooltip(comp, tooltipConfig));
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
   * @param tooltip the String to display (may contain html)
   * @param ui      {@link UI}
   */
  default void setTooltip(String tooltip, UI ui) {
    getElement().getComponent().ifPresent(comp ->
        Tooltips.get(ui).setTooltip(comp, tooltip)
    );
  }

  /**
   * Removes a tooltip from the implementing {@link Component}
   */
  default void removeTooltip() {
    removeTooltip(UI.getCurrent());
  }

  /**
   * Removes a tooltip from the implementing {@link Component}
   *
   * @param ui {@link UI}
   */
  default void removeTooltip(UI ui) {
    getElement().getComponent().ifPresent(comp ->
        Tooltips.get(ui).removeTooltip(comp)
    );
  }

  /**
   * Shows its tooltip if it exists.
   */
  default void showTooltip() {
    showTooltip(UI.getCurrent());
  }

  /**
   * Shows its tooltip if it exists.
   *
   * @param ui {@link UI}
   */
  default void showTooltip(UI ui) {
    getElement().getComponent().ifPresent(comp ->
        Tooltips.get(ui).showTooltip(comp)
    );
  }

  /**
   * Hides its tooltip if it exists.
   */
  default void hideTooltip() {
    hideTooltip(UI.getCurrent());
  }

  /**
   * Hides its tooltip if it exists.
   *
   * @param ui {@link UI}
   */
  default void hideTooltip(UI ui) {
    getElement().getComponent().ifPresent(comp ->
        Tooltips.get(ui).hideTooltip(comp)
    );
  }
}
