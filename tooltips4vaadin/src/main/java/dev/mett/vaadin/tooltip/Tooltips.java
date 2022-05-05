package dev.mett.vaadin.tooltip;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.shared.Registration;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;
import dev.mett.vaadin.tooltip.exception.TooltipsAlreadyInitializedException;
import dev.mett.vaadin.tooltip.util.TooltipsJsProvider;
import dev.mett.vaadin.tooltip.util.TooltipsUtil;
import elemental.json.JsonNull;
import elemental.json.JsonValue;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Allows to easily define tooltips on Vaadin {@link Component}s.<br>
 * <br>
 * <br>
 * Usage:<code><br>
 * <br>
 * TextField tfData = new TextField("data");<br> Tooltips.setTooltip(tfData, "Allows you to enter some data");<br>
 * </code>
 *
 * @author Gerrit Sedlaczek
 * @see #setTooltip(Component, String)
 * @see #setTooltip(Component, TooltipStateData)
 * @see #setTooltip(Component, TooltipConfiguration)
 * @see #removeTooltip(Component)
 */
public final class Tooltips implements Serializable {

  public interface JS_METHODS {

    String SET_TOOLTIP = "return window.tooltips.setTooltipToElement($0,$1)"; // DOM-Element, tooltipConfig
    String UPDATE_TOOLTIP = "window.tooltips.updateTooltip($0,$1)"; // DOM-Element, tooltipConfig
    String CLOSE_TOOLTIP_FORCED = "window.tooltips.closeTooltipForced($0)"; // tippyId
    String REMOVE_TOOLTIP = "window.tooltips.removeTooltip($0,$1)"; // frontendId, tippyId
    String CLOSE_ALL_TOOLTIPS = "window.tooltips.closeAllTooltips()";
    String SHOW_TOOLTIP = "window.tooltips.showTooltip($0)"; // DOM-Element
    String HIDE_TOOLTIP = "window.tooltips.hideTooltip($0)"; // DOM-Element
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
    return (Tooltips) ComponentUtil.getData(ui, UI_TOOLTIPS_KEY);
  }

  /**
   * Associates a {@link Tooltips} instance with a given {@link UI}.
   *
   * @param ui       {@link UI}
   * @param tooltips {@link Tooltips}
   */
  private static void set(UI ui, Tooltips tooltips) {
    ComponentUtil.setData(ui, UI_TOOLTIPS_KEY, tooltips);
  }

  /**
   * TOOLTIPS INSTANCE
   **/
  private final UI defaultUI;

  public Tooltips(UI tooltipsUI) throws TooltipsAlreadyInitializedException {
    this.defaultUI = tooltipsUI;

    if (Tooltips.get(tooltipsUI) != null) {
      throw new TooltipsAlreadyInitializedException();
    }

    // adds the scripts to the currentUI
    tooltipsUI.add(new TooltipsJsProvider());

    Tooltips.set(tooltipsUI, this);
  }

  /**
   * CONSTANTS
   **/

  private static final long serialVersionUID = -7384516516590189446L;
  private static final Logger log = Logger.getLogger(Tooltips.class.getName());

  private static final String COMPONENT_STATE_KEY = "TOOLTIP_STATE";
  private static final String UI_TOOLTIPS_KEY = "TOOLTIPS";

  private static final String FRONTEND_ID_PREFIX = "tooltip-";
  private static final String FRONTEND_TAG_NAME = "tt4v";

  /**
   * STATE
   **/
  private static final AtomicLong tooltipIdGenerator = new AtomicLong();
  private static TooltipConfiguration defaultTooltipConfiguration = null;

  /**
   * TODO: 1. Bulk operations
   */

  /**
   * Defines a default configuration for all subsequently defined tooltips when using the .
   *
   * @param configuration the default configuration
   * @see TooltipConfiguration
   */
  public static void setDefaultTooltipConfiguration(TooltipConfiguration configuration) {
    defaultTooltipConfiguration = configuration;
  }

  /* *** SET / MODIFY *** */

  /**
   * @param component the {@link Component} that is supposed to have a tooltip
   * @param tooltip   the tooltips text
   */
  public void setTooltip(
      final Component component,
      final String tooltip
  ) {
    getTooltipState(component, true)
        .ifPresent(state -> {
          if (doesTooltipChange(state, tooltip)) {
            state.getTooltipConfig().setContent(tooltip);
            setTooltip(component, state);
          }
        });
  }

  private boolean doesTooltipChange(TooltipStateData state, String newTooltip) {
    return newTooltip != null && !newTooltip.equals(state.getTooltipConfig().getContent());
  }

  /**
   * @param component            the {@link Component} that is supposed to have a tooltip
   * @param tooltipConfiguration {@link TooltipConfiguration} the configuration of the tooltip
   */
  public void setTooltip(
      final Component component,
      final TooltipConfiguration tooltipConfiguration
  ) {
    getTooltipState(component, true)
        .ifPresent(state -> {
          if (doesTooltipChange(state, tooltipConfiguration)) {
            state.setTooltipConfig(tooltipConfiguration);
            setTooltip(component, state);
          }
        });
  }

  private boolean doesTooltipChange(TooltipStateData state, TooltipConfiguration newConfiguration) {
    return !state.getTooltipConfig().equals(newConfiguration);
  }

  /**
   * Sets a tooltip to the supplied {@link Component}.<br> Automatically deregisters itself upon the components detach.<br>
   *
   * @param component    the {@link Component} that is supposed to have a tooltip
   * @param tooltipState {@link TooltipStateData}
   * @see #setTooltip(Component, TooltipConfiguration)
   */
  private void setTooltip(
      final Component component,
      final TooltipStateData tooltipState
  ) {
    if (component == null) {
      throw new IllegalArgumentException(
          "Tooltips4Vaadin requires a non null component in order to set a tooltip"
      );
    }

    if (tooltipState.getFrontendId() != null) {
      updateKnownComponent(component, tooltipState);

    } else {
      initiallySetupComponent(component, tooltipState);
    }
  }

  private void updateKnownComponent(Component component, TooltipStateData tooltipState) {
    if (isComponentAttached(component)) {
      callJsOnComponent(
          component,
          JS_METHODS.UPDATE_TOOLTIP,
          new Serializable[]{
              component,
              tooltipState.getTooltipConfig().toJsonObject()},
          json -> applyJsonTippyId(tooltipState, json));
    }
    // else: automatically uses the new value upon attach
  }

  private UI getUI(Optional<Component> component) {

    if (component.isPresent()) {
      Optional<UI> possibleComponentUI = component.get().getUI();

      if (possibleComponentUI.isPresent()) {
        UI componentUI = possibleComponentUI.get();

        if (componentUI != defaultUI) {
//          log.info("Preserve on refresh detected");
          return componentUI;
        }
      }
    }

    return defaultUI;
  }

  private boolean isComponentAttached(Component component) {
    return component.getElement().getNode().isAttached();
  }

  private void initiallySetupComponent(Component component, TooltipStateData state) {
    generateAndApplyUniqueFrontendId(component, state);

    registerWithTippyJS(component, state);
    setupAutomaticDeregistration(component, state);
  }

  private void setupAutomaticDeregistration(Component component, TooltipStateData state) {
    Registration detachReg = component.addDetachListener(
        evt ->
            TooltipsUtil.securelyAccessUI(
                getUI(Optional.of(component)),
                () -> closeFrontendTooltip(state.getTippyId())));

    state.setDetachReg(new WeakReference<>(detachReg));
  }

  private void registerWithTippyJS(Component component, TooltipStateData state) {
    Runnable register = getRegistrationRunnable(component, state);

    if (isComponentAttached(component)) {
      register.run();
    }

    Registration attachReg = component.addAttachListener(evt -> register.run());
    state.setAttachReg(new WeakReference<>(attachReg));
  }

  private Runnable getRegistrationRunnable(Component component, TooltipStateData state) {
    return () -> {
      callJsOnComponent(
          component,
          JS_METHODS.SET_TOOLTIP,
          new Serializable[]{
              component,
              state.getTooltipConfig().toJsonObject()},
          json -> applyJsonTippyId(state, json));
    };
  }

  private void generateAndApplyUniqueFrontendId(Component component, TooltipStateData state) {
    String frontendId = FRONTEND_ID_PREFIX + state.getTooltipId();
    applyTooltipTag(component.getElement(), frontendId);
    state.setFrontendId(frontendId);
  }

  private void applyJsonTippyId(TooltipStateData state, JsonValue json) {
    if (json != null && !(json instanceof JsonNull)) {
      Integer tippyId = (int) json.asNumber();
      state.setTippyId(tippyId);
    }
  }

  /* *** REMOVE *** */

  /**
   * Removes a tooltip form a {@link Component}.
   *
   * @param component the {@link Component} that currently has a tooltip
   */
  public void removeTooltip(final Component component) {
    if (component != null) {
      getTooltipState(component, false)
          .ifPresent(state -> {
            if (state.getFrontendId() != null) {

              deregisterTooltip(
                  state,
                  () -> {
                    removeTooltipState(state);
                    removeTooltipTag(component.getElement());
                  });
            }
          });
    }
  }

  /**
   * Closes all currently opened tooltips.
   */
  public void closeAllTooltips() {
    callJsOnPage(JS_METHODS.CLOSE_ALL_TOOLTIPS);
  }

  /**
   * Close a tooltip if it is still open.
   *
   * @param tippyId the id of the tooltip itself
   */
  private void closeFrontendTooltip(Integer tippyId) {
    callJsOnPage(JS_METHODS.CLOSE_TOOLTIP_FORCED, new Serializable[]{tippyId});
  }

  /**
   * Deregisters a tooltip in the frontend (tippy).
   * Even if the frontend operation fails the afterFrontendDeregistration is guaranteed to be executed.
   *
   * @param state                       {@link TooltipStateData}
   * @param afterFrontendDeregistration an action to perform after the element has been deregistered
   */
  private void deregisterTooltip(
      TooltipStateData state,
      SerializableRunnable afterFrontendDeregistration) {
    Integer tippyId = state.getTippyId();
    if (tippyId != null) {
      String frontendId = state.getFrontendId();

      callJsOnPage(
          JS_METHODS.REMOVE_TOOLTIP,
          new Serializable[]{
              frontendId,
              tippyId},
          json -> afterFrontendDeregistration.run(),
          onError -> afterFrontendDeregistration.run());

    } else {
      log.warning(() -> "Tippy frontend id is null for " + state);
      afterFrontendDeregistration.run();
    }
  }

  /* *** SHOW / HIDE *** */

  /**
   * If the component passed has a tooltip associated to it it gets opened.
   *
   * @param component {@link Component}
   */
  public void showTooltip(Component component) {
    callJsOnComponent(component, JS_METHODS.SHOW_TOOLTIP, new Serializable[]{component});
  }

  /**
   * If the component passed has a tooltip associated to it it gets closed.
   *
   * @param component {@link Component}
   */
  public void hideTooltip(Component component) {
    callJsOnComponent(component, JS_METHODS.HIDE_TOOLTIP, new Serializable[]{component});
  }

  /* *** CONFIG *** */

  /**
   * Gives access to the {@link TooltipConfiguration} of a given component.
   *
   * @param component T your {@link Component}
   * @return {@link Optional} of {@link TooltipConfiguration}
   */
  public Optional<TooltipConfiguration> getConfiguration(Component component) {
    return getTooltipState(component, false)
        .map(TooltipStateData::getTooltipConfig);
  }

  /* *** UTIL *** */

  private void callJsOnComponent(
      Component component,
      String function,
      Serializable[] parameters,
      SerializableConsumer<JsonValue> callbackAfterJsExecution,
      SerializableConsumer<String> callbackAfterJsExecutionOnError) {
    UI ui = getUI(Optional.of(component));

    TooltipsUtil.securelyAccessUI(ui, () -> {
      getTooltipState(component, true)
          .ifPresent(Tooltips::ensureTagIsSet);

      // TODO: cancel handle pendingjsresult
      component.getElement().executeJs(
              function,
              parameters
          )
          .then(callbackAfterJsExecution,
              callbackAfterJsExecutionOnError);
    });
  }

  private void callJsOnComponent(
      Component component,
      String function,
      Serializable[] parameters,
      SerializableConsumer<JsonValue> callbackAfterJsExecution) {
    callJsOnComponent(
        component,
        function,
        parameters,
        callbackAfterJsExecution,
        err -> log.warning(() -> "Tooltips: js error: " + err));
  }

  private void callJsOnComponent(
      Component component,
      String function,
      Serializable[] parameters) {
    callJsOnComponent(
        component,
        function,
        parameters,
        nothing -> { /* there is no optional action to perform */ });
  }

  private void callJsOnComponent(
      Component component,
      String function) {
    callJsOnComponent(component, function, new Serializable[0]);
  }

  private void callJsOnPage(
      String function,
      Serializable[] parameters,
      SerializableConsumer<JsonValue> callbackAfterJsExecution,
      SerializableConsumer<String> callbackAfterJsExecutionOnError) {
    UI ui = getUI(Optional.empty());

    TooltipsUtil.securelyAccessUI(ui, () ->
        // TODO: cancel handle pendingjsresult
        ui.getPage()
            .executeJs(function, parameters)
            .then(
                callbackAfterJsExecution,
                callbackAfterJsExecutionOnError));
  }

  private void callJsOnPage(
      String function,
      Serializable[] parameters,
      SerializableConsumer<JsonValue> callbackAfterJsExecution) {
    callJsOnPage(
        function,
        parameters,
        callbackAfterJsExecution,
        err -> log.warning(() -> "Tooltips: js error: " + err));
  }

  private void callJsOnPage(
      String function,
      Serializable[] parameters) {
    callJsOnPage(
        function,
        parameters,
        nothing -> { /* there is no optional action to perform */ });
  }

  private void callJsOnPage(
      String function) {
    callJsOnPage(function, new Serializable[0]);
  }


  private Optional<TooltipStateData> getTooltipState(final Component comp, final boolean register) {
    TooltipStateData state = (TooltipStateData) ComponentUtil.getData(comp, COMPONENT_STATE_KEY);

    if (state != null) {
      return Optional.of(state);

    } else {
      if (!register) {
        return Optional.empty();
      }

      long tooltipId = tooltipIdGenerator.incrementAndGet();
      TooltipStateData tooltipStateData = createTooltipStateData(comp, tooltipId);

      ComponentUtil.setData(
          comp,
          COMPONENT_STATE_KEY,
          tooltipStateData);

      return Optional.of(tooltipStateData);
    }
  }

  private TooltipStateData createTooltipStateData(
      Component comp,
      long finalTooltipId) {

    return new TooltipStateData(
        defaultTooltipConfiguration != null
            ? defaultTooltipConfiguration.clone()
            : new TooltipConfiguration(),
        finalTooltipId,
        new WeakReference<>(comp));
  }

  private boolean removeTooltipState(final TooltipStateData state) {
    if (state != null) {
      removeReg(state.getAttachReg());
      removeReg(state.getDetachReg());

      Component component = state.getComponent().get();
      if (component != null) {
        ComponentUtil.setData(
            component,
            COMPONENT_STATE_KEY,
            null
        );

        return true;
      }
    }

    return false;
  }

  private void removeReg(WeakReference<Registration> regRef) {
    if (regRef != null) {
      Registration reg = regRef.get();
      if (reg != null) {
        try {
          reg.remove();
        } catch (IllegalArgumentException ex) {
          /* ignore */
        }
      }
    }
  }

  private static void ensureTagIsSet(final TooltipStateData state) {
    Component comp = state.getComponent().get();
    if (comp != null) {
      Element element = comp.getElement();

      if (canTagBeApplied(state, element)) {
        applyTooltipTag(element, state.getFrontendId());
      }
    }
  }

  private static boolean canTagBeApplied(TooltipStateData state, Element element) {
    if (element == null
        || state.getFrontendId() == null) {
      return false;
    }

    String tagValue = element.getAttribute(FRONTEND_TAG_NAME);
    return !(tagValue == null
        || tagValue.equals(state.getFrontendId()));
  }

  private static void applyTooltipTag(Element element, String tagValue) {
    // THE ATTRIBUTE WONT BE RENDERED UNTIL ITS VISIBLE=TRUE
    element.setAttribute(FRONTEND_TAG_NAME, tagValue);
  }

  private static void removeTooltipTag(Element element) {
    element.removeAttribute(FRONTEND_TAG_NAME);
  }
}
