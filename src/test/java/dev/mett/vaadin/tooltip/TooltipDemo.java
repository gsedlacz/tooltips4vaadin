package dev.mett.vaadin.tooltip;

import java.util.concurrent.atomic.AtomicLong;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.Route;

@Route("")
public class TooltipDemo extends HorizontalLayout {
	private static final long serialVersionUID = 7591127437515385460L;
	private final AtomicLong atomicLong = new AtomicLong();

	public TooltipDemo() {
		Tooltips tooltips;
		try {
			tooltips = new Tooltips(UI.getCurrent());
		} catch (TooltipsAlreadyInitialized e) {
			tooltips = Tooltips.getCurrent();
		}
		

        EmailField emailField = new EmailField();
        tooltips.setTooltip(emailField, "initial Value");

        Button btChangeTooltip = new Button("change tooltip", evt -> {
        	Tooltips.getCurrent().setTooltip(emailField, "value-" + atomicLong.getAndIncrement());
        });

        Button btRemoveTooltip = new Button("remove tooltip", evt -> {
        	Tooltips.getCurrent().removeTooltip(emailField);
        });

        Button btDetachAttachField = new Button("detach/attach field", evt -> {
        	if(getChildren().filter(c -> emailField == c).count() != 0) {
        		remove(emailField);
        	} else {
        		getElement().insertChild(0, emailField.getElement());
        	}
        });

        add(emailField, new VerticalLayout(btChangeTooltip, btRemoveTooltip, btDetachAttachField));
	}
}
