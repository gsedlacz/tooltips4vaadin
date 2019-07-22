package dev.mett.vaadin.tooltip;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.server.UIInitListener;
import com.vaadin.flow.shared.Registration;

import elemental.json.JsonValue;

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

	private final static AtomicLong tooltipIdGenerator 		= new AtomicLong();
	private final static String CLASS_PREFIX 				= "tooltip-";
	private final static Map<Integer, TooltipStateData> tooltipStorage = new HashMap<>();

	public interface JS_METHODS {
		String SET_TOOLTIP 		= "window.tooltips.setTooltip($0,$1)";
		String UPDATE_TOOLTIP 	= "window.tooltips.updateTooltip($0,$1)";
		String REMOVE_TOOLTIP 	= "window.tooltips.removeTooltip($0)";
	}

	/**
	 * Make sure to call this method once before calling any other methods of this class.<br>
	 * You can call this in your {@link UIInitListener}.
	 *
	 * @param ui used to deploy JavaScript sources
	 */
	public static void init(UI ui) {
		ui.add(new TooltipsJsProvider());
		//TODO: apply custom configurations
	}

	/**
	 * TODO:
	 * 1. Bulk operations
	 * 2. register UI init listener
	 */


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
	public static <T extends Component & HasStyle> void setTooltip(final T component, String tooltip, final UI ui) {
		final boolean isAttached = component.getElement().getNode().isAttached();
		final Page page = ui.getPage();
		final TooltipStateData state = getTooltipState(component);

		// newlines to html
		tooltip = tooltip.replaceAll("(\\r\\n|\\r|\\n)", "<br>");
		state.setTooltip(tooltip);

//		System.out.println("####### | Trying to define tooltip for " + component + " to " + tooltip);

		if(state.getCssClass() != null) {
			// update
//			System.out.println("UPDATE  | class: " + state.getCssClass());

			if (isAttached) {
				ui.access(() -> page.executeJs(JS_METHODS.UPDATE_TOOLTIP, state.getCssClass(), state.getTooltip()));
			}
			// else: automatically uses the new value upon attach

		} else {
			//initial setup
			// 1. set unique class
			final String finalUniqueClassName = CLASS_PREFIX + tooltipIdGenerator.getAndIncrement();
			component.addClassName(finalUniqueClassName);
			state.setCssClass(finalUniqueClassName);

//			System.out.println("Created new class: " + finalUniqueClassName);

			// 2. register with tippy.js
			Runnable register = () -> ui.access(() -> {
				TooltipStateData stateAttach = getTooltipState(component);
				if(stateAttach.getCssClass() != null && stateAttach.getTooltip() != null) {
					page.executeJs(JS_METHODS.SET_TOOLTIP, stateAttach.getCssClass(), stateAttach.getTooltip());
//					System.out.println("SET     | class: " + stateAttach.getCssClass() + " | tooltip " + stateAttach.getTooltip());
				}
			});

			if(isAttached) {
				register.run();
			}

			Registration attachReg = component.addAttachListener(evt -> register.run());
			state.setAttachReg(Optional.of(attachReg));

			// 3. automatic deregistration
			Registration detachReg = component.addDetachListener(evt -> ui.access(() -> deregisterTooltip(getTooltipState(component), ui, Optional.empty())));
			state.setDetachReg(Optional.of(detachReg));
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
		final TooltipStateData state = getTooltipState(component);

		if(state.getCssClass() != null) {
//			System.out.println("REMOVE | class: " + state.getCssClass());

			deregisterTooltip(
					state,
					ui,
					Optional.of(json -> {
						removeTooltipState(component);
						component.removeClassName(state.getCssClass());
					}));
		}
	}


	/**
	 * Deregisters a tooltip in the frontend (tippy).
	 *
	 * @param uniqueClassName the CSS classname that currently identifies the tooltip
	 * @param ui
	 * @param afterFrontendDeregistration
	 */
	private static void deregisterTooltip(
			final TooltipStateData state,
			final UI ui,
			final Optional<SerializableConsumer<JsonValue>> afterFrontendDeregistration)
	{
		final String uniqueClassName = state.getCssClass();
//		System.out.println("DEREG  | class " + uniqueClassName);

		ui.access(() -> {
			ui.getPage().executeJs(JS_METHODS.REMOVE_TOOLTIP, uniqueClassName)
				.then(afterFrontendDeregistration.orElse(nothing -> {/* nothing */}));
		});
	}


	/* *** UTIL *** */

	private static TooltipStateData getTooltipState(final Component comp) {
		final int hashCode = comp.hashCode();
		TooltipStateData state = tooltipStorage.get(hashCode);
		if(state == null) {
			state = new TooltipStateData();
			tooltipStorage.put(hashCode, state);
		}
		return state;
	}

	private static boolean removeTooltipState(final Component comp) {
		final int hashCode = comp.hashCode();
		final TooltipStateData state = tooltipStorage.remove(hashCode);
		if(state != null) {
			state.getAttachReg().ifPresent(reg -> reg.remove());
			state.getDetachReg().ifPresent(reg -> reg.remove());
			return true;
		} else {
			return false;
		}
	}
}
