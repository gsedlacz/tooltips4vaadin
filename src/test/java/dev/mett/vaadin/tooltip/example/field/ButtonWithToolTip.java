package dev.mett.vaadin.tooltip.example.field;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import dev.mett.vaadin.tooltip.Tooltips;

/**
 * THIS IS NOT AN EXAMPLE IMPLEMENTATION
 *
 * https://gitlab.com/gsedlacz/tooltips4vaadin/-/issues/37
 *
 *
 * <h1>Button With Tool Tip</h1>
 *
 * <p>A button with a tool tip.
 *
 * @author <a href="mailto:paul@corbettcode.com">Paul Corbett <paul@corbettcode.com></a>
 */
public class ButtonWithToolTip extends Composite<Div>
//	implements HasTooltip
{
  //
  // Static
  //

  //
  // Constructors
  //

  /** No arguments constructor for {@link ButtonWithToolTip}. */
  public ButtonWithToolTip() {
    super();

    button = new Button();
    initializeTooltip();

    getContent()
        .add(
            button
            //			, buttonTooltip
        );
  }

  /** Button argument constructor for {@link ButtonWithToolTip}. */
  public ButtonWithToolTip(final Button _button) {
    super();

    button = _button;
    initializeTooltip();

    getContent()
        .add(
            button
            //			, buttonTooltip
        );
  }

  /**
   * Icon and click listener arguments constructor for {@link ButtonWithToolTip}.
   *
   * @param _icon {@link Component}
   * @param clickListener {@link ComponentEventListener}
   */
  public ButtonWithToolTip(
      final Component _icon, final ComponentEventListener<ClickEvent<Button>> clickListener) {
    this();

    button.setIcon(_icon);
    button.addClickListener(clickListener);
  }

  //
  // Operations
  //

  /**
   * Add the specified click listener to the button.
   *
   * @param _listener {@link ComponentEventListener}
   * @return {@link Registration}
   */
  public Registration addClickListener(final ComponentEventListener<ClickEvent<Button>> _listener) {
    return button.addClickListener(_listener);
  }

  /**
   * Adds a shortcut which 'clicks' the {@link Button} which implements the {@link ClickNotifier}
   * interface.
   *
   * @param _key {@link Key}
   * @param _keyModifiers array of type {@link KeyModifier}
   * @return {@link Registration}
   */
  public Registration addClickShortCut(final Key _key, final KeyModifier... _keyModifiers) {
    return button.addClickShortcut(_key, _keyModifiers);
  }

  /**
   * Adds theme variants to the component.
   *
   * @param _variants {@link ButtonVariant} array of theme variants to add
   */
  public void addThemeVariants(final ButtonVariant... _variants) {
    button.addThemeVariants(_variants);
  }

  /** Initialize the tooltip components. */
  private void initializeTooltip() {}

  /**
   * Removes theme variants from the component.
   *
   * @param _variants {@link ButtonVariant} array of theme variants to remove
   */
  public void removeThemeVariants(final ButtonVariant... _variants) {
    button.removeThemeVariants(_variants);
  }

  /**
   * Assign the text shown in the button.
   *
   * @param _buttonText {@link String}
   */
  public void setButtonText(final String _buttonText) {
    button.setText(_buttonText);
  }

  /**
   * Assign the enable state using the specified value.
   *
   * @param _value {@code boolean}
   */
  public void setEnabled(final boolean _value) {
    button.setEnabled(_value);
  }

  /**
   * Assign the content of the tooltip.
   *
   * @param _buttonText {@link String}
   */
  public void setTooltipContent(final String _buttonText) {
    if (lastToolTipText.equals(_buttonText) == true) {
      // nothing more to do
      return;
    }
    lastToolTipText = _buttonText;

    if ((_buttonText != null) && (_buttonText.isEmpty() == false)) {
      Tooltips.getCurrent().setTooltip(button, _buttonText);
    } else {
      Tooltips.getCurrent().removeTooltip(button);
    }
  }

  //
  // Attributes
  //

  @lombok.Getter private Button button;

  private String lastToolTipText = "";
}
