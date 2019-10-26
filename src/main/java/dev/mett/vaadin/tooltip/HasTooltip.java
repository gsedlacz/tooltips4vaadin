package dev.mett.vaadin.tooltip;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;

/**
 * Allows to define Tooltips more easily by adding this mixin interface.
 * 
 * @author Gerrit Sedlaczek
 */
public interface HasTooltip extends HasElement {
	
	/**
	 * Adds a tooltip to the implementing {@link Component}.
	 * 
	 * @param tooltip the String to display (may contain html)
	 */
	default void setTooltip(String tooltip) {
		setTooltip(tooltip, UI.getCurrent());
	}
	
	/**
	 * Adds a tooltip to the implementing {@link Component}.
	 * 
	 * @param <T> the type of a styleable component
	 * @param tooltip the String to display (may contain html)
	 * @param ui {@link UI}
	 */
	@SuppressWarnings("unchecked")
	default <T extends Component & HasStyle> void setTooltip(String tooltip, UI ui) {
		getElement().getComponent().ifPresent(comp -> {
			if(comp instanceof HasStyle) {
				Tooltips.get(ui).setTooltip((T)comp, tooltip);
			}
		});
	}
	
	/*
	 * Gets the current Tooltip of the {@link Component}.
	 * 
	 * @return String its current tooltip
	 */
	default Optional<String> getTooltip() {
		return getTooltip((UI.getCurrent()));
	}
	
	/**
	 * Gets the current Tooltip of the {@link Component}.
	 * 
	 * @param ui {@link UI} the ui of this component.
	 * @return String its current tooltip
	 */
	default Optional<String> getTooltip(UI ui) {
		try {
			return Tooltips.get(ui).getTooltip(getElement().getComponent().get());
		} catch(NoSuchElementException e) {}
		
		return Optional.empty();
	}
}
