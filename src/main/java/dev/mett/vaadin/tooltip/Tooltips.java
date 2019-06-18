package dev.mett.vaadin.tooltip;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;

/**
 * TODO: javadoc
 *
 * @author Gerrit Sedlaczek
 */
public final class Tooltips {
	private Tooltips() {}

	private final static AtomicLong tooltipIdGenerator = new AtomicLong();
	private final static String CLASS_PREFIX = "tooltip-";

	/**
	 * Calls this method to initialize the tooltip addon.
	 */
	public static void init(UI ui) {
		Page page = ui.getPage();
		page.addJavaScript("js/tooltip/popper.min.js");
		page.addJavaScript("js/tooltip/tippy.min.js");
		page.addJavaScript("js/tooltip/tooltips.js");

		//TODO: apply config
	}

	public static <T extends Component & HasStyle> void setTooltip(T component, String tooltip) {
		setTooltip(component, tooltip, UI.getCurrent());
	}

	public static <T extends Component & HasStyle> void setTooltip(T component, String tooltip, UI ui) {
		Page page = ui.getPage();

		final String classNames = component.getClassName();
		if(classNames == null || !classNames.contains(CLASS_PREFIX)) {
			// 1. unique set class
			final String uniqueClassName = CLASS_PREFIX + tooltipIdGenerator.getAndIncrement();
			component.addClassName(uniqueClassName);

			// 2. register with tippy.js
			Consumer<UI> register = registerUI -> registerUI.access(() -> page.executeJs(JS_METHODS.SET_TOOLTIP, uniqueClassName, tooltip));
			register.accept(ui);
			component.addAttachListener(evt -> register.accept(ui));
		}

		// set values
	}

	public interface JS_METHODS {
		String SET_TOOLTIP = "window.tooltips.setTooltip($0,$1)";	}
}
