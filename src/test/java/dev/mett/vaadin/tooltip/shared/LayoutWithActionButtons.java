package dev.mett.vaadin.tooltip.shared;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.example.field.TooltipEmailField;
import dev.mett.vaadin.tooltip.mixin.HasTooltip;
import java.util.concurrent.atomic.AtomicLong;

public class LayoutWithActionButtons extends HorizontalLayout {

  private final AtomicLong atomicLong = new AtomicLong();

  public LayoutWithActionButtons(boolean initiallyVisible) {
    TooltipEmailField emailField = new TooltipEmailField();
    emailField.setHeight("fit-content");
    emailField.setId(LayoutWithActionButtonsConstants.ID_TEXTFIELD);
    emailField.setTooltip("initial tooltip");

    // initial visibility may change the intended behaviour
    emailField.setVisible(initiallyVisible);

    VerticalLayout buttons = getActionButtons(emailField);

    this.add(
        buttons,
        emailField);
  }

  private VerticalLayout getActionButtons(TooltipEmailField emailField) {
    VerticalLayout tooltipActions = getTooltipActions(emailField);

    VerticalLayout compActions = getCompActions(emailField);

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
        this.getElement().insertChild(1, component.getElement());
      } else {
        throw new IllegalStateException("Cant attach comp as its already attached");
      }
    });
    btAttachField.setId(LayoutWithActionButtonsConstants.ID_COMP_ATTACH_BT);

    Button btDetachField = new Button("[comp] detach", evt -> {
      if (this.getChildren().anyMatch(c -> component == c)) {
        this.remove(component);
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

  private VerticalLayout getTooltipActions(HasTooltip hasTooltipField) {
    Button btChangeTooltip = new Button("[tooltip] change / set",
        evt -> hasTooltipField.setTooltip("value-" + atomicLong.getAndIncrement()));
    btChangeTooltip.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_CHANGE_OR_SET_BT);

    Button btRemoveTooltip = new Button("[tooltip] remove",
        evt -> hasTooltipField.removeTooltip());
    btRemoveTooltip.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_REMOVE_BT);

    Button btShowTooltip = new Button("[tooltip] show",
        evt -> hasTooltipField.show());
    btShowTooltip.setId(LayoutWithActionButtonsConstants.ID_TOOLTIP_SHOW_BT);

    Button btHideTooltip = new Button("[tooltip] hide",
        evt -> hasTooltipField.hide());
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
