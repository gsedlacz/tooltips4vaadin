package dev.mett.vaadin.tooltip;

import java.lang.ref.WeakReference;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

import lombok.Getter;
import lombok.Setter;

public class TooltipStateData {
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
}
