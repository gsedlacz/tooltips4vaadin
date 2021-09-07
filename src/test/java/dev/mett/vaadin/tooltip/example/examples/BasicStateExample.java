package dev.mett.vaadin.tooltip.example.examples;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.example.field.TooltipEmailField;
import java.util.concurrent.atomic.AtomicLong;

public class BasicStateExample {

  private final AtomicLong atomicLong = new AtomicLong();

  public <P extends Component & HasComponents> Component getBasicExample(P parent) {
    TooltipEmailField emailField = new TooltipEmailField();
    emailField.setHeight("fit-content");
    emailField.setId("abc");
    emailField.setTooltip("initial Value");

    Button btChangeTooltip = new Button("change tooltip",
        evt -> emailField.setTooltip("value-" + atomicLong.getAndIncrement()));

    Button btRemoveTooltip = new Button("remove tooltip",
        evt -> emailField.removeTooltip());

    Button btShowTooltip = new Button("show",
        evt -> emailField.show());

    Button btHideTooltip = new Button("hide",
        evt -> emailField.hide());

    Button btDetachAttachField = new Button("detach/attach field", evt -> {
      if (parent.getChildren().anyMatch(c -> emailField == c)) {
        parent.remove(emailField);
      } else {
        parent.getElement().insertChild(0, emailField.getElement());
      }
    });

    Button btCloseAll = new Button("close all",
        evt -> Tooltips.getCurrent().closeAllTooltips());

    Button btCloseUI = new Button("close UI", evt ->
        evt.getSource().getUI().ifPresent(UI::close));

    return new VerticalLayout(
        emailField,
        new VerticalLayout(
            btChangeTooltip,
            btRemoveTooltip,
            btShowTooltip,
            btHideTooltip,
            btDetachAttachField,
            btCloseAll,
            btCloseUI));
  }

//  public <P extends Component & HasComponents> Component getDebug(P parent) {
//    var field = new ButtonWithToolTip();
//
//    var btChangeTooltip = new Button("change tooltip",
//        evt -> {
////          Tooltips.getCurrent().setTooltip(field, "value-" + atomicLong.getAndIncrement());
//          field.setTooltipContent("value-" + atomicLong.getAndIncrement());
//        });
//
//    var btRemoveTooltip = new Button("remove tooltip",
//        evt -> Tooltips.getCurrent().removeTooltip(field));
//
//    var btShowTooltip = new Button("show",
//        evt -> Tooltips.getCurrent().showTooltip(field));
//
//    var btHideTooltip = new Button("hide",
//        evt -> Tooltips.getCurrent().hideTooltip(field));
//
//    var btDetachAttachField = new Button("detach/attach field", evt -> {
//      if (parent.getChildren().anyMatch(c -> field == c)) {
//        parent.remove(field);
//      } else {
//        parent.getElement().insertChild(0, field.getElement());
//      }
//    });
//
//    var btCloseAll = new Button("close all",
//        evt -> Tooltips.getCurrent().closeAllTooltips());
//
//    var btCloseUI = new Button("close UI", evt ->
//        evt.getSource().getUI().ifPresent(UI::close));
//
//    return new VerticalLayout(
//        field,
//        new VerticalLayout(
//            btChangeTooltip,
//            btRemoveTooltip,
//            btShowTooltip,
//            btHideTooltip,
//            btDetachAttachField,
//            btCloseAll,
//            btCloseUI));
//  }
}
