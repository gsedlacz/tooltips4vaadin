package dev.mett.vaadin.tooltip;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import dev.mett.vaadin.tooltip.exception.TooltipsAlreadyInitialized;
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
 * @see #setTooltip(Component, String)
 * @see #removeTooltip(Component)
 */
public final class Tooltips implements Serializable {
    private static final long serialVersionUID = 2847553469052560067L;
    
    private final static String UI_KEY 			= "TOOLTIPS";
	private final static String CLASS_PREFIX 	= "tooltip-";
	
	public interface JS_METHODS {
		String SET_TOOLTIP 		= "return window.tooltips.setTooltip($0,$1)";
		String UPDATE_TOOLTIP 	= "window.tooltips.updateTooltip($0,$1)";
		String REMOVE_TOOLTIP 	= "window.tooltips.removeTooltip($0,$1)";
	}
	
	
	/** STATIC METHODS **/
	
	/**
	 * Returns the {@link Tooltips} instance associated with the current UI.
	 * 
	 * @return {@link Tooltips}
	 */
	public static Tooltips getCurrent() {
		return get(UI.getCurrent());
	}
	
	/**
	 * Returns the {@link Tooltips} instance associated with the {@link UI} passed.
	 * 
	 * @param ui {@link UI}
	 * @return {@link Tooltips}
	 */
	public static Tooltips get(UI ui) {
		return (Tooltips) ComponentUtil.getData(ui, UI_KEY);
	}
	
	/**
	 * Associates a {@link Tooltips} instance with a given {@link UI}.
	 * 
	 * @param ui {@link UI}
	 * @param tooltips {@link Tooltips}
	 */
	private static void set(UI ui, Tooltips tooltips) {
		ComponentUtil.setData(ui, UI_KEY, tooltips);
	}
	
	
	/** TOOLTIPS INSTANCE **/
	
	private final AtomicLong tooltipIdGenerator 					= new AtomicLong();
	private final Map<Component, TooltipStateData> tooltipStorage 	= new IdentityHashMap<>();
	private final UI ui;
	
	public Tooltips(UI ui) throws TooltipsAlreadyInitialized {
		this.ui = ui;
		
		if(Tooltips.get(ui) != null) {
			throw new TooltipsAlreadyInitialized();
		}
		
		// adds the scripts to the currentUI
		ui.add(new TooltipsJsProvider());

		Tooltips.set(ui, this);
	}
	

	/**
	 * TODO:
	 * 1. Bulk operations
	 * 2. register UI init listener
	 */
	

	/* *** SET / MODIFY *** */

	/**
	 * Sets a tooltip to the supplied {@link Component}.<br>
	 * Automatically deregisters itself upon the components detach.<br>
	 *
	 * @param <T> requires the supplied {@link Component} to implement {@link HasStyle}
	 * @param component the {@link Component} that is supposed to have a tooltip
	 * @param tooltip the tooltips information
	 */
	public <T extends Component & HasStyle> void setTooltip(final T component, String tooltip) {
		if(component == null || tooltip == null) {
		    return;
		}
		
		final TooltipStateData state = getTooltipState(component);

		// newlines to html
		tooltip = tooltip.replaceAll("(\\r\\n|\\r|\\n)", "<br>");
		state.setTooltip(tooltip);
		
		if(!state.getWrapper().isEmpty()) {
			setTooltipToWrapper(state);
		
		} else {
			setTooltipToFrontend(state);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Component & HasStyle> void setTooltipToWrapper(TooltipStateData state) {
		state.getWrapper().ifPresent(weakWrapper -> {
			Component wrapper = weakWrapper.get();
			if(wrapper != null) {
				setTooltip((T) wrapper, state.getTooltip());
			}
		});
	}
	
	private <T extends Component & HasStyle> void setTooltipToFrontend(TooltipStateData state) {
		@SuppressWarnings("unchecked")
		final T component = (T) state.getComponent().get();
		if(component != null) {
			
			final boolean isAttached = component.getElement().getNode().isAttached();
			final Page page = ui.getPage();
			
			if(state.getCssClass() != null) {
				// update
				ensureCssClassIsSet(component, state);
				
				if (isAttached) {
				TooltipsUtil.securelyAccessUI(ui, () -> page.executeJs(JS_METHODS.UPDATE_TOOLTIP, state.getCssClass(), state.getTooltip()));
				}
				// else: automatically uses the new value upon attach

			} else {
				//initial setup
				// 1. set unique class
				final String finalUniqueClassName = CLASS_PREFIX + tooltipIdGenerator.getAndIncrement();
				component.addClassName(finalUniqueClassName);
				state.setCssClass(finalUniqueClassName);

				// 2. register with tippy.js
				Runnable register = () -> TooltipsUtil.securelyAccessUI(ui, () -> {
					TooltipStateData stateAttach = getTooltipState(component);
					ensureCssClassIsSet(component, stateAttach);
				
					if(stateAttach.getCssClass() != null && stateAttach.getTooltip() != null) {
					page.executeJs(JS_METHODS.SET_TOOLTIP, stateAttach.getCssClass(), stateAttach.getTooltip())
						.then(json -> stateAttach.setTooltipId((int) json.asNumber()));
					}
				});

				if(isAttached) {
					register.run();
				}

				Registration attachReg = component.addAttachListener(evt -> {
					if(!getTooltipState(component).getWrapper().isPresent()) {
						register.run();
					}
				});
				state.setAttachReg(Optional.of(attachReg));

				// 3. automatic deregistration
				state.setDetachReg(Optional.of(component.addDetachListener(evt -> TooltipsUtil.securelyAccessUI(ui, () -> deregisterTooltip(getTooltipState(component), ui, Optional.empty())))));
			}
		}
	}
	
	/* *** REMOVE *** */
	
	/**
	 * Removes a tooltip form a {@link Component}.
	 *
	 * @param <T> requires the supplied {@link Component} to implement {@link HasStyle}
	 * @param component the {@link Component} that currently has a tooltip
	 */
	public <T extends Component & HasStyle> void removeTooltip(final T component) {
		final TooltipStateData state = getTooltipState(component);

		if(state.getCssClass() != null && component != null) {

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
	private void deregisterTooltip(
			final TooltipStateData state,
			final UI ui,
			final Optional<SerializableConsumer<JsonValue>> afterFrontendDeregistration)
	{
		final String uniqueClassName 	= state.getCssClass();
		final int tooltipId				= state.getTooltipId();

		TooltipsUtil.securelyAccessUI(ui, () -> {
			ui.getPage().executeJs(JS_METHODS.REMOVE_TOOLTIP, uniqueClassName, tooltipId)
				.then(afterFrontendDeregistration.orElse(nothing -> {/* nothing */}));
		});
	}

	/* *** GET *** */
	
	/**
	 * Returns the components current tooltip value.
	 * 
	 * @param comp {@link Component}
	 * @return {@link Optional}<{@link String}> tooltip value
	 */
	public Optional<String> getTooltip(final Component comp) {
		if(comp == null) {
			return Optional.empty();
		}
		
		TooltipStateData state = tooltipStorage.get(comp.hashCode());
		
		if(state == null || state.getTooltip() == null || state.getTooltip().isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(state.getTooltip());
	}

	/* *** UTIL *** */

	TooltipStateData getTooltipState(final Component comp) {
		TooltipStateData state = tooltipStorage.get(comp);
		if(state == null) {
			state = new TooltipStateData();
			state.setComponent(new WeakReference<>(comp));
			tooltipStorage.put(comp, state);
		}
		return state;
	}

	private boolean removeTooltipState(final Component comp) {
		final TooltipStateData state = tooltipStorage.remove(comp);
		if(state != null) {
			state.getAttachReg().ifPresent(Registration::remove);
			state.getDetachReg().ifPresent(Registration::remove);
			return true;
		} else {
			return false;
		}
	}
	
	private static <T extends Component & HasStyle> void ensureCssClassIsSet(final T comp, final TooltipStateData state) {
		if(   comp.getClassName() != null 
		   && !comp.getClassName().contains(state.getCssClass())) 
	    {
			comp.addClassName(state.getCssClass());
		}
	}
}
