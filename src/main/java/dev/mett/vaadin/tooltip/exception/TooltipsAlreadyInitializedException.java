package dev.mett.vaadin.tooltip.exception;

public class TooltipsAlreadyInitializedException extends Exception {

  private static final long serialVersionUID = -4847616951890701625L;

  public TooltipsAlreadyInitializedException() {
    super("There is already another instance of Tooltips class for this UI.\nCall Tooltips.get(UI) to get it.");
  }
}
