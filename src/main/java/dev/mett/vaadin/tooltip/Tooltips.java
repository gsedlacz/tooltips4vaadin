package dev.mett.vaadin.tooltip;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import elemental.json.JsonNull;
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
	private static final long serialVersionUID = -7384516516590189446L;
	private static final Logger log = Logger.getLogger(Tooltips.class.getName());
	
	private static final String UI_KEY			   = "TOOLTIPS";
	private static final String CLASS_PREFIX		 = "tooltip-";
	private static final String COMPONENT_TOOLTIP_ID = "tooltipId";
	
	public interface JS_METHODS {
		String SET_TOOLTIP		  = "return window.tooltips.setTooltip($0,$1)";
		String UPDATE_TOOLTIP	   = "window.tooltips.updateTooltip($0,$1)";
		String REMOVE_TOOLTIP	   = "window.tooltips.removeTooltip($0,$1)";
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
	
	private final AtomicLong tooltipIdGenerator				 = new AtomicLong();
	private final Map<Long, TooltipStateData> tooltipStorage	= new ConcurrentHashMap<>();
	private final UI ui;
	
	public Tooltips(UI ui) throws TooltipsAlreadyInitialized {
		this.ui = ui;
		
		if(Tooltips.get(ui) != null) {
			throw new TooltipsAlreadyInitialized();
		}
		
		// adds the scripts to the currentUI
		ui.add(new TooltipsJsProvider());

		Tooltips.set(ui, this);
		TooltipsCleaner.INSTANCE.register(this);
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
	 * @param <T>   requires the supplied {@link Component} to implement
	 *			  {@link HasStyle}
	 * @param component the {@link Component} that is supposed to have a tooltip
	 * @param tooltip   the tooltips information
	 */
	public <T extends Component & HasStyle> void setTooltip(final T component, String tooltip) {
		if (component == null || tooltip == null || tooltip.isEmpty()) {
			return;
		}

		final boolean attached = component.getElement().getNode().isAttached();
		final Page page = ui.getPage();
		final TooltipStateData state = getTooltipState(component, true);

		// newlines to html
		tooltip = tooltip.replaceAll("(\\r\\n|\\r|\\n)", "<br>");
		state.setTooltip(tooltip);

		if (state.getCssClass() != null) {
			// update
			ensureCssClassIsSet(state);

			if (attached) {
				TooltipsUtil.securelyAccessUI(ui, () ->
					page.executeJs(JS_METHODS.UPDATE_TOOLTIP, state.getCssClass(), state.getTooltip())
					    .then(json -> setTippyId(state, json))
				);
			}
			// else: automatically uses the new value upon attach

		} else {
			// initial setup
			// 1. set unique css class
			String uniqueClassName = CLASS_PREFIX + state.getTooltipId();
			component.addClassName(uniqueClassName);
			state.setCssClass(uniqueClassName);

			// 2. register with tippy.js
			Runnable register = () -> TooltipsUtil.securelyAccessUI(ui, () -> {
				ensureCssClassIsSet(state);
				
				page.executeJs(JS_METHODS.SET_TOOLTIP, state.getCssClass(), state.getTooltip())
					.then(json -> setTippyId(state, json), 
						  err -> log.fine(()-> "Tooltips: js error: " + err));
			});

			if (attached) {
				register.run();
			}

			Registration attachReg = component.addAttachListener(evt -> register.run());
			state.setAttachReg(new WeakReference<>(attachReg));

			// 3. automatic deregistration
			Registration detachReg = component.addDetachListener(
				evt -> TooltipsUtil.securelyAccessUI(ui, () -> deregisterTooltip(state, ui, Optional.empty())));
			state.setDetachReg(new WeakReference<>(detachReg));
		}
	}
	
	private void setTippyId(TooltipStateData state, JsonValue json) {
		if(json != null && !(json instanceof JsonNull)) {
			Integer tippyId = (int) json.asNumber();
			state.setTippyId(tippyId);
		}
	}

	/* *** REMOVE *** */

	/**
	 * Removes a tooltip form a {@link Component}.
	 *
	 * @param <T>  requires the supplied {@link Component} to implement
	 *			 {@link HasStyle}
	 * @param component the {@link Component} that currently has a tooltip
	 */
	public <T extends Component & HasStyle> void removeTooltip(final T component) {
		if(component != null) {
			TooltipStateData state = getTooltipState(component, false);
			if (state != null && state.getCssClass() != null) {
	
				deregisterTooltip(
					state, 
					ui, 
					Optional.of(json -> {
						removeTooltipState(state);
						component.removeClassName(state.getCssClass());
						ComponentUtil.setData(component, COMPONENT_TOOLTIP_ID, null);
					})
				);
			}
		}
	}

	/**
	 * Deregisters a tooltip in the frontend (tippy).
	 *
	 * @param uniqueClassName the CSS classname that currently
	 *						identifies the tooltip
	 * @param ui
	 * @param afterFrontendDeregistration
	 */
	private void deregisterTooltip(
			final TooltipStateData state, 
			final UI ui,
			final Optional<SerializableConsumer<JsonValue>> afterFrontendDeregistration) {
		
		Integer tippyId = state.getTippyId();
		if(tippyId != null) {
			String uniqueClassName = state.getCssClass();
			TooltipsUtil.securelyAccessUI(ui, () -> {
				ui.getPage().executeJs(JS_METHODS.REMOVE_TOOLTIP, uniqueClassName, tippyId)
						.then(afterFrontendDeregistration.orElse(nothing -> {/* nothing */}));
			});
		
		} else {
			log.fine(()-> "Tippy frontend id is null for " + state);
			afterFrontendDeregistration.ifPresent(task -> task.accept(null));
		}
	}


	/* *** UTIL *** */

	private TooltipStateData getTooltipState(final Component comp, final boolean register) {
		Long tooltipID = (Long) ComponentUtil.getData(comp, COMPONENT_TOOLTIP_ID);
		if(tooltipID == null) {
			if(!register) {
				return null;
			}
			tooltipID = tooltipIdGenerator.incrementAndGet();
			ComponentUtil.setData(comp, COMPONENT_TOOLTIP_ID, tooltipID);
		}
		long tooltipId = tooltipID;
		return tooltipStorage.computeIfAbsent(tooltipID, k -> {
			TooltipStateData state = new TooltipStateData(tooltipId);
			state.setComponent(new WeakReference<>(comp));
			return state;
		});
	}

	private boolean removeTooltipState(final TooltipStateData state) {
		if (state != null) {
			removeReg(state.getAttachReg());
			removeReg(state.getDetachReg());
			
			tooltipStorage.remove(state.getTooltipId());
			return true;
		}

		return false;
	}

	private void removeReg(WeakReference<Registration> regRef) {
		if (regRef != null) {
			Registration reg = regRef.get();
			if(reg != null) {
				try {
					reg.remove();
				} catch (IllegalArgumentException ex) {
					/* ignore */
				}
			}
		}
	}

	private static void ensureCssClassIsSet(final TooltipStateData state) {
		HasStyle comp = (HasStyle) state.getComponent().get();

		if (comp != null 
		 && state.getCssClass() != null 
		 && comp.getClassName() != null 
		 && !comp.getClassName().contains(state.getCssClass())) 
		{
			comp.addClassName(state.getCssClass());
		}
	}

	final Map<Long, TooltipStateData> getTooltipStorage() {
		return this.tooltipStorage;
	}

	final UI getUI() {
		return this.ui;
	}
}
