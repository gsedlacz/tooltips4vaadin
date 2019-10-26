package dev.mett.vaadin.tooltip;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

import lombok.Getter;
import lombok.Setter;

public class TooltipStateData implements Serializable {
    private static final long serialVersionUID = -2994087447549309682L;
    
    @Getter @Setter
	private Optional<Registration> attachReg;
	@Getter @Setter
	private Optional<Registration> detachReg;
	@Getter @Setter
	private String cssClass;
	@Getter @Setter
	private String tooltip;
	@Getter @Setter
	private int tooltipId;
	@Getter @Setter
	private WeakReference<Component> component;
	
	@Getter @Setter
	private Optional<WeakReference<Component>> wrapper;
	
	TooltipStateData() {
		this.attachReg = Optional.empty();
		this.detachReg = Optional.empty();
		this.wrapper = Optional.empty();
	}
}
