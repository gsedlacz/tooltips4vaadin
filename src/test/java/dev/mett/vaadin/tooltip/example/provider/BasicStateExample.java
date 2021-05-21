package dev.mett.vaadin.tooltip.example.provider;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import dev.mett.vaadin.tooltip.Tooltips;
import java.util.concurrent.atomic.AtomicLong;
import lombok.var;

public class BasicStateExample {
  private final AtomicLong atomicLong = new AtomicLong();
  public <P extends Component & HasComponents> void exampleBasic(P parent) {
    var emailField = new TooltipEmailField();
    emailField.setHeight("fit-content");
    emailField.setId("abc");
    emailField.setTooltip("initial Value");

    var btChangeTooltip = new Button("change tooltip",
        evt -> emailField.setTooltip("value-" + atomicLong.getAndIncrement()));

    var btRemoveTooltip = new Button("remove tooltip",
        evt -> emailField.removeTooltip());

    var btShowTooltip = new Button("show",
        evt -> emailField.show());

    var btHideTooltip = new Button("hide",
        evt -> emailField.hide());

    var btDetachAttachField = new Button("detach/attach field", evt -> {
      if (parent.getChildren().anyMatch(c -> emailField == c)) {
        parent.remove(emailField); //FIXME: remove is broken
      } else {
        parent.getElement().insertChild(0, emailField.getElement());
      }
    });

    var btCloseAll = new Button("close all",
        evt -> Tooltips.getCurrent().removeAllTooltips());

    parent.add(new VerticalLayout(
        emailField,
        new VerticalLayout(btChangeTooltip, btRemoveTooltip, btShowTooltip, btHideTooltip, btDetachAttachField, btCloseAll)));
  }
}
