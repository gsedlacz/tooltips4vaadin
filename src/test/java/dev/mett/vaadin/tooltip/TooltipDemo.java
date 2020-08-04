package dev.mett.vaadin.tooltip;

import java.util.concurrent.atomic.AtomicLong;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.Route;
import dev.mett.vaadin.tooltip.mixin.HasTooltip;
import lombok.var;

@Route("")
public class TooltipDemo extends FlexLayout {
    private static final long serialVersionUID = 7591127437515385460L;
    private final AtomicLong atomicLong = new AtomicLong();

    public TooltipDemo() {
        var emailField = new TooltipEmailField();
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
            if (getChildren().filter(c -> emailField == c).count() != 0) {
                remove(emailField);
            } else {
                getElement().insertChild(0, emailField.getElement());
            }
        });

        var btCloseAll = new Button("close all", evt -> {
            Tooltips.getCurrent().removeAllTooltips();
        });

        add(emailField, new VerticalLayout(btChangeTooltip, btRemoveTooltip, btShowTooltip, btHideTooltip, btDetachAttachField, btCloseAll));
    }

    private static class TooltipEmailField extends EmailField implements HasTooltip {}
}
