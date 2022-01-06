package dev.mett.vaadin.tooltip.shared;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import dev.mett.vaadin.tooltip.Tooltips;
import java.util.concurrent.atomic.AtomicLong;

public class LayoutWithActionButtons extends HorizontalLayout {

  private final AtomicLong atomicLong = new AtomicLong();

  public LayoutWithActionButtons(Component component) {
    this(component, true);
  }

  public LayoutWithActionButtons(Component component, boolean initiallyVisible) {
    component.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_COMPONENT);

    // initial visibility may change the intended behaviour
    component.setVisible(initiallyVisible);

    VerticalLayout buttons = getActionButtons(component);

    this.add(
        buttons,
        component);
  }

  private VerticalLayout getActionButtons(Component component) {
    VerticalLayout tooltipActions = getTooltipActions(component);

    VerticalLayout compActions = getCompActions(component);

    Button btCloseUI = new Button("[UI] close UI", evt ->
        evt.getSource().getUI().ifPresent(UI::close));
    btCloseUI.setId(LayoutWithActionButtonsConstants.ID_UI_CLOSE_BT);

    return new VerticalLayout(
        tooltipActions,
        compActions,
        btCloseUI);
  }

  private VerticalLayout getCompActions(Component component) {
    Button btSetVisibile = new Button("[comp] set visible",
        evt -> {
          if (!component.isVisible()) {
            component.setVisible(true);
          } else {
            throw new IllegalStateException("Cant make comp visible as its already visible");
          }
        });
    btSetVisibile.setId(LayoutWithActionButtonsConstants.ID_COMP_VISIBLE_BT);

    Button btSetInvisible = new Button("[comp] set invisible",
        evt -> {
          if (component.isVisible()) {
            component.setVisible(false);
          } else {
            throw new IllegalStateException("Cant make comp invisible as its already invisible");
          }
        });
    btSetInvisible.setId(LayoutWithActionButtonsConstants.ID_COMP_INVISIBLE_BT);

    Button btAttachField = new Button("[comp] attach", evt -> {
      if (this.getChildren().noneMatch(c -> component == c)) {
        component.getParent().ifPresent(parent ->
            parent.getElement().insertChild(1, component.getElement()));
      } else {
        throw new IllegalStateException("Cant attach comp as its already attached");
      }
    });
    btAttachField.setId(LayoutWithActionButtonsConstants.ID_COMP_ATTACH_BT);

    Button btDetachField = new Button("[comp] detach", evt -> {
      if (this.getChildren().anyMatch(c -> component == c)) {
        component.getParent().ifPresent(parent ->
            ((HasComponents) parent).remove(component));
      } else {
        throw new IllegalStateException("Cant detach comp as its already detached");
      }
    });
    btDetachField.setId(LayoutWithActionButtonsConstants.ID_COMP_DETACH_BT);

    return new VerticalLayout(
        btSetVisibile,
        btSetInvisible,
        btAttachField,
        btDetachField);
  }

  private VerticalLayout getTooltipActions(Component component) {
    Button btChangeTooltip = new Button("[tooltip] change / set",
        evt -> Tooltips.getCurrent().setTooltip(component, "value-" + atomicLong.getAndIncrement()));
    btChangeTooltip.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_CHANGE_OR_SET_BT);

    Button btRemoveTooltip = new Button("[tooltip] remove",
        evt -> Tooltips.getCurrent().removeTooltip(component));
    btRemoveTooltip.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_REMOVE_BT);

    Button btShowTooltip = new Button("[tooltip] show",
        evt -> Tooltips.getCurrent().showTooltip(component));
    btShowTooltip.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_SHOW_BT);

    Button btHideTooltip = new Button("[tooltip] hide",
        evt -> Tooltips.getCurrent().hideTooltip(component));
    btHideTooltip.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_HIDE_BT);

    Button btCloseAll = new Button("[tooltip] close all",
        evt -> Tooltips.getCurrent().closeAllTooltips());
    btCloseAll.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_CLOSE_ALL_BT);

    return new VerticalLayout(
        btChangeTooltip,
        btRemoveTooltip,
        btShowTooltip,
        btHideTooltip,
        btCloseAll);
  }
}
