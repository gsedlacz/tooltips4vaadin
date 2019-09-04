package dev.mett.vaadin.tooltip;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;

/**
 * This mixin-interface allows to have tooltips on disabled components.
 * 
 * @author Gerrit Sedalczek
 *
 * @param <T> the type of the implementing {@link Component}
 */
public interface HasTooltipWrapper<T extends Component & HasStyle> extends HasTooltip, HasEnabled {
	/**
	 * Optionally you can define a CSS classnames for the wrapper.
	 * If you dont need these classes return {@link Optional#empty()}
	 * 
	 * @return {@link Optional} may contain a list of strings
	 */
	Optional<List<String>> getTooltipsWrapperClassNames();
	
	@Override
	default  void setEnabled(boolean enabled) {
		// modifies the enabled state first as the component can not define a tooltip otherwise
		HasEnabled.super.setEnabled(enabled);
		
		getElement().getComponent().ifPresent(comp -> {
			if(comp instanceof HasStyle) {
				@SuppressWarnings("unchecked")
				final T component = (T) comp;
				
				final int indexInOriginalParent;
				final Tooltips tooltips = Tooltips.getCurrent();
				final TooltipStateData state = tooltips.getTooltipState(component);
				
				// either the wrapper or the original parent
				Element parentElement = getElement().getParent();
			
				/* SET ENABLED */
				if(enabled && isEnabled()) {
					Element originalParentElement = parentElement.getParent();
					
					indexInOriginalParent = getIndexOfIn(parentElement, originalParentElement);

					originalParentElement.insertChild(indexInOriginalParent, getElement());
					originalParentElement.removeChild(parentElement);
					
					tooltips.registerDetachListener(component);
					
					state.setWrapper(Optional.empty());
					tooltips.setTooltip(component, state.getTooltip());
					
				/* SET DISABLED */
				} else if (!isEnabled()) {
					indexInOriginalParent = getIndexOfIn(getElement(), parentElement);
					
					Div wrapper = new Div();
					state.setWrapper(Optional.of(new WeakReference<>(wrapper)));
					getTooltipsWrapperClassNames().ifPresent(
							wrapperClasses -> wrapperClasses.forEach(
									wrapperClass -> wrapper.addClassName(wrapperClass)));
					
					tooltips.setTooltip(wrapper, state.getTooltip());
					
					//keeps the component from deregistering itself
					state.getDetachReg().ifPresent(detachReg -> detachReg.remove());
					wrapper.add(component);
					parentElement.insertChild(indexInOriginalParent, wrapper.getElement());
				}
			}
		});
	}
	
	private int getIndexOfIn(Element child, Element parent) {
		return parent.getChildren()
			     	 .collect(Collectors.toList())
			     	 .indexOf(child);
		
	}
}
