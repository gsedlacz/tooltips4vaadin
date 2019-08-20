package dev.mett.vaadin.tooltip;

public class TooltipsAlreadyInitialized extends Exception {
	private static final long serialVersionUID = -4847616951890701625L;

	public TooltipsAlreadyInitialized() {
		super("There is already another instance of Tooltips class for this UI.\nCall Tooltips.get(UI) to get it.");
	}
}
