package dev.mett.vaadin.tooltip;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.UIInitListener;

/**
 * Allows to easily define tooltips on Vaadin {@link Component}s.<br>
 * <br>
 * <br>
 * Usage:<code><br>
 * <br>
 * TextField tfData = new TextField("data");<br>
 * Tooltips.setTooltip(tfData, "Allows you to enter some data");<br>
 * </code>
 *
 * @author Gerrit Sedlaczek
 *
 * @see #setTooltip(Component, String, UI)
 * @see #removeTooltip(Component, UI)
 */
public final class Tooltips {
	private Tooltips() {}

	private final static AtomicLong tooltipIdGenerator 	= new AtomicLong();
	private final static String CLASS_PREFIX 			= "tooltip-";

	public interface JS_METHODS {
		String SET_TOOLTIP 		= "window.tooltips.setTooltip($0,$1)";
		String UPDATE_TOOLTIP 	= "window.tooltips.updateTooltip($0,$1)";
		String REMOVE_TOOLTIP 	= "window.tooltips.removeTooltip($0)";
	}

	/**
	 * Make sure to call this method once before calling any other mehtods of this class.<br>
	 * You can call this in your {@link UIInitListener}.
	 *
	 * @param ui used to deploy javascript sources
	 */
	public static void init(UI ui) {
		Page page = ui.getPage();
		page.addJavaScript("js/tooltip/popper.min.js");
		page.addJavaScript("js/tooltip/tippy.min.js");
		page.addJavaScript("js/tooltip/tooltips.js");

		//TODO: apply custom configurations
	}


	/* *** SET / MODIFY *** */

	/**
	 * Sets a tooltip to any kind of ui element.<br>
	 * Automatically deregisters itself upon the components detach.<br>
	 * <br>
	 * Uses {@link UI#getCurrent()}.
	 *
	 * @param <T> requires the supplied {@link Component} to implement {@link HasStyle}
	 * @param component the {@link Component} that is supposed to have a tooltip
	 * @param tooltip the tooltips information
	 *
	 * @see #setTooltip(Component, String, UI)
	 */
	public static <T extends Component & HasStyle> void setTooltip(final T component, final String tooltip) {
		setTooltip(component, tooltip, UI.getCurrent());
	}

	/**
	 * Sets a tooltip to the supplied {@link Component}.<br>
	 * Automatically deregisters itself upon the components detach.<br>
	 *
	 * @param <T> requires the supplied {@link Component} to implement {@link HasStyle}
	 * @param component the {@link Component} that is supposed to have a tooltip
	 * @param tooltip the tooltips information
	 * @param ui the {@link UI} associated with
	 *
	 * @see #setTooltip(Component, String)
	 */
	public static <T extends Component & HasStyle> void setTooltip(final T component, final String tooltip, final UI ui) {
		final Page page = ui.getPage();
		String uniqueClassName = getUniqueClassName(component);

		// initial setup
		if(uniqueClassName == null) {
			// 1. set unique class
			final String finalUniqueClassName = CLASS_PREFIX + tooltipIdGenerator.getAndIncrement();
			component.addClassName(finalUniqueClassName);

			// 2. register with tippy.js
			Consumer<UI> register = registerUI -> registerUI.access(() -> page.executeJs(JS_METHODS.SET_TOOLTIP, finalUniqueClassName, tooltip));
			register.accept(ui);
			component.addAttachListener(evt -> register.accept(ui));

			// 3. automatic deregistration
			component.addDetachListener(evt -> ui.access(() -> removeTooltip(component)));

		// update
		} else {
			final String finalUniqueClassName = uniqueClassName;
			ui.access(() -> page.executeJs(JS_METHODS.UPDATE_TOOLTIP, finalUniqueClassName, tooltip));
		}
	}


	/* *** REMOVE *** */

	/**
	 * Removes a tooltip form a {@link Component}.
	 *
	 * @param <T> requires the supplied {@link Component} to implement {@link HasStyle}
	 * @param component the {@link Component} that currently has a tooltip
	 *
	 * @see #removeTooltip(Component, UI)
	 */
	public static <T extends Component & HasStyle> void removeTooltip(final T component) {
		removeTooltip(component, UI.getCurrent());
	}

	/**
	 * Removes a tooltip form a {@link Component}.
	 *
	 * @param <T> requires the supplied {@link Component} to implement {@link HasStyle}
	 * @param component the {@link Component} that currently has a tooltip
	 * @param ui the {@link UI} associated with
	 *
	 * @see #removeTooltip(Component)
	 */
	public static <T extends Component & HasStyle> void removeTooltip(final T component, final UI ui) {
		final Page page = ui.getPage();
		final String uniqueClassName = getUniqueClassName(component);

		if(uniqueClassName != null) {
			ui.access(() -> {
				if(uniqueClassName != null && uniqueClassName.contains(CLASS_PREFIX)) {
					page.executeJs(JS_METHODS.REMOVE_TOOLTIP, uniqueClassName)
			    	.then(json -> component.removeClassName(uniqueClassName));
				}
			});
		}
	}


	/* *** UTIL *** */

	/**
	 *
	 *
	 * @param <T> <T> requires the supplied {@link Component} to implement {@link HasStyle}
	 * @param component the {@link Component} that might have a tooltip
	 * @return the unique classname if it has one or null
	 */
	private static <T extends Component & HasStyle> String getUniqueClassName(final T component) {
		for(String className : component.getClassNames()) {
			if(className.startsWith(CLASS_PREFIX)) {
				return className;
			}
		}
		return null;
	}
}
