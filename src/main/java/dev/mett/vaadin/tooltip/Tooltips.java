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

import dev.mett.vaadin.tooltip.config.TooltipConfiguration;
import dev.mett.vaadin.tooltip.exception.TooltipsAlreadyInitializedException;
import dev.mett.vaadin.tooltip.util.TooltipsJsProvider;
import dev.mett.vaadin.tooltip.util.TooltipsUtil;
import elemental.json.JsonNull;
import elemental.json.JsonValue;

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
 * @see #removeTooltip(Component)
 */
public final class Tooltips implements Serializable {

  private static final long serialVersionUID = -7384516516590189446L;
  private static final Logger log = Logger.getLogger(Tooltips.class.getName());

  private static final String UI_KEY = "TOOLTIPS";
  private static final String CLASS_PREFIX = "tooltip-";
  private static final String COMPONENT_TOOLTIP_ID = "tooltipId";
  private TooltipConfiguration defaultTooltipConfiguration = null;

  public interface JS_METHODS {

    String SET_TOOLTIP = "return window.tooltips.setTooltip($0,$1)";
    String UPDATE_TOOLTIP = "window.tooltips.updateTooltip($0,$1)";
    String CLOSE_TOOLTIP_FORCED = "window.tooltips.closeTooltipForced($0)";
    String REMOVE_TOOLTIP = "window.tooltips.removeTooltip($0,$1)";
    String CLOSE_ALL_TOOLTIPS = "window.tooltips.closeAllTooltips()";
    String SHOW_TOOLTIP = "window.tooltips.showTooltip($0)";
    String HIDE_TOOLTIP = "window.tooltips.hideTooltip($0)";
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
   * @param ui       {@link UI}
   * @param tooltips {@link Tooltips}
   */
  private static void set(UI ui, Tooltips tooltips) {
    ComponentUtil.setData(ui, UI_KEY, tooltips);
  }

  /**
   * TOOLTIPS INSTANCE
   **/

  private final AtomicLong tooltipIdGenerator = new AtomicLong();
  private final Map<Long, TooltipStateData> tooltipStorage = new ConcurrentHashMap<>();
  private final UI ui;

  public Tooltips(UI ui) throws TooltipsAlreadyInitializedException {
    this.ui = ui;

    if (Tooltips.get(ui) != null) {
      throw new TooltipsAlreadyInitializedException();
    }

    // adds the scripts to the currentUI
    ui.add(new TooltipsJsProvider());

    Tooltips.set(ui, this);
    TooltipsCleaner.INSTANCE.register(this);
  }

  /**
   * TODO: 1. Bulk operations
   */

  /**
   * Defines a default configuration for all subsequently defined tooltips when using the .
   *
   * @param configuration the default configuration
   * @see TooltipConfiguration
   */
  public void setDefaultTooltipConfiguration(TooltipConfiguration configuration) {
    defaultTooltipConfiguration = configuration;
  }

  /* *** SET / MODIFY *** */

  /**
   * @param <T>       requires the supplied {@link Component} to implement {@link HasStyle}
   * @param component the {@link Component} that is supposed to have a tooltip
   * @param tooltip   the tooltips text
   */
  public <T extends Component & HasStyle> void setTooltip(
      final T component,
      final String tooltip
  ) {
    TooltipStateData state = getTooltipState(component, true);
    state.getTooltipConfig().setContent(tooltip);
    setTooltip(component, state);
  }

  /**
   * @param <T>                  requires the supplied {@link Component} to implement {@link HasStyle}
   * @param component            the {@link Component} that is supposed to have a tooltip
   * @param tooltipConfiguration {@link TooltipConfiguration} the configuration of the tooltip
   */
  public <T extends Component & HasStyle> void setTooltip(
      final T component,
      final TooltipConfiguration tooltipConfiguration
  ) {
    TooltipStateData state = getTooltipState(component, true);
    state.setTooltipConfig(tooltipConfiguration);
    setTooltip(component, state);
  }

  /**
   * Sets a tooltip to the supplied {@link Component}.<br> Automatically deregisters itself upon the components detach.<br>
   *
   * @param <T>       requires the supplied {@link Component} to implement {@link HasStyle}
   * @param component the {@link Component} that is supposed to have a tooltip
   * @param state     {@link TooltipStateData}
   * @see #setTooltip(Component, TooltipConfiguration)
   */
  private <T extends Component & HasStyle> void setTooltip(
      final T component,
      final TooltipStateData state
  ) {
    if (component == null) {
      throw new IllegalArgumentException(
          "Tooltips4Vaadin requires a non null component in order to set a tooltip"
      );
    }

    final boolean attached = component.getElement().getNode().isAttached();
    final Page page = ui.getPage();

    if (state.getCssClass() != null) {
      // update
      ensureCssClassIsSet(state);

      if (attached) {
        TooltipsUtil.securelyAccessUI(
            ui,
            () -> page.executeJs(
                JS_METHODS.UPDATE_TOOLTIP,
                state.getCssClass(),
                state.getTooltipConfig().toJson()
            )
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

        page.executeJs(
            JS_METHODS.SET_TOOLTIP,
            state.getCssClass(),
            state.getTooltipConfig().toJson()
        )
            .then(
                json -> setTippyId(state, json),
                err -> log.fine(() -> "Tooltips: js error: " + err)
            );
      });

      if (attached) {
        register.run();
      }

      Registration attachReg = component.addAttachListener(evt -> register.run());
      state.setAttachReg(new WeakReference<>(attachReg));

      // 3. automatic deregistration
      Registration detachReg = component.addDetachListener(
          evt -> TooltipsUtil.securelyAccessUI(ui, () -> closeFrontendTooltip(state, Optional.empty()))
      );
      state.setDetachReg(new WeakReference<>(detachReg));
    }
  }

  private void setTippyId(TooltipStateData state, JsonValue json) {
    if (json != null && !(json instanceof JsonNull)) {
      Integer tippyId = (int) json.asNumber();
      state.setTippyId(tippyId);
    }
  }

  /* *** REMOVE *** */

  /**
   * Removes a tooltip form a {@link Component}.
   *
   * @param <T>       requires the supplied {@link Component} to implement {@link HasStyle}
   * @param component the {@link Component} that currently has a tooltip
   */
  public <T extends Component & HasStyle> void removeTooltip(final T component) {
    if (component != null) {
      TooltipStateData state = getTooltipState(component, false);
      if (state != null && state.getCssClass() != null) {

        deregisterTooltip(state, Optional.of(json -> {
          removeTooltipState(state);
          component.removeClassName(state.getCssClass());
          ComponentUtil.setData(component, COMPONENT_TOOLTIP_ID, null);
        }));
      }
    }
  }

  /**
   * Closes all currently opened tooltips and deregisters any
   */
  public void removeAllTooltips() {
    getTooltipStorage().values().forEach(this::removeTooltipState);
    callJs(JS_METHODS.CLOSE_ALL_TOOLTIPS);
  }

  /**
   * Close a tooltip if it is still open.
   *
   * @param state                       {@link TooltipStateData}
   * @param afterFrontendDeregistration an optional action to execute once the deregistration has finished
   */
  private void closeFrontendTooltip(
      final TooltipStateData state,
      final Optional<SerializableConsumer<JsonValue>> afterFrontendDeregistration
  ) {
    callJs(JS_METHODS.CLOSE_TOOLTIP_FORCED, afterFrontendDeregistration, state.getTippyId());
  }

  /**
   * Deregisters a tooltip in the frontend (tippy).
   *
   * @param state                       {@link TooltipStateData}
   * @param afterFrontendDeregistration an action to perform after the element has been deregistered
   */
  private void deregisterTooltip(
      final TooltipStateData state,
      final Optional<SerializableConsumer<JsonValue>> afterFrontendDeregistration
  ) {
    Integer tippyId = state.getTippyId();
    if (tippyId != null) {
      String uniqueClassName = state.getCssClass();

      callJs(JS_METHODS.REMOVE_TOOLTIP, afterFrontendDeregistration, uniqueClassName, tippyId);

    } else {
      log.fine(() -> "Tippy frontend id is null for " + state);
      afterFrontendDeregistration.ifPresent(task -> task.accept(null));
    }
  }

  /* *** SHOW / HIDE *** */

  /**
   * If the component passed has a tooltip associated to it it gets opened.
   *
   * @param component {@link Component}
   */
  public void showTooltip(Component component) {
    callJs(JS_METHODS.SHOW_TOOLTIP, component);
  }

  /**
   * If the component passed has a tooltip associated to it it gets closed.
   *
   * @param component {@link Component}
   */
  public void hideTooltip(Component component) {
    callJs(JS_METHODS.HIDE_TOOLTIP, component);
  }

  /* *** CONFIG *** */

  /**
   * Gives access to the {@link TooltipConfiguration} of a given component.
   *
   * @param <T>       a {@link Component} implementing {@link HasStyle}
   * @param component T your {@link Component}
   * @return {@link Optional} of {@link TooltipConfiguration}
   */
  public <T extends Component & HasStyle> Optional<TooltipConfiguration> getConfiguration(T component) {
    TooltipStateData state = getTooltipState(component, false);
    if (state == null) {
      return Optional.empty();
    } else {
      return Optional.of(state.getTooltipConfig());
    }
  }

  /* *** UTIL *** */

  private void callJs(
      String function,
      final Optional<SerializableConsumer<JsonValue>> afterFrontendDeregistration,
      Serializable... parameters
  ) {
    TooltipsUtil.securelyAccessUI(ui, () -> {
      ui.getPage()
          .executeJs(function, parameters)
          .then(afterFrontendDeregistration.orElse(nothing -> {
                /* nothing */
              }
          ));
    });
  }

  private void callJs(
      String function,
      Serializable... parameters
  ) {
    callJs(function, Optional.empty(), parameters);
  }

  private TooltipStateData getTooltipState(final Component comp, final boolean register) {
    Long tooltipId = (Long) ComponentUtil.getData(comp, COMPONENT_TOOLTIP_ID);
    if (tooltipId == null) {
      if (!register) {
        return null;
      }
      tooltipId = tooltipIdGenerator.incrementAndGet();
      ComponentUtil.setData(comp, COMPONENT_TOOLTIP_ID, tooltipId);
    }
    final long finalTooltipId = tooltipId;

    return tooltipStorage.computeIfAbsent(
        tooltipId,
        k -> new TooltipStateData(
            defaultTooltipConfiguration != null ? defaultTooltipConfiguration.clone() : new TooltipConfiguration(),
            finalTooltipId,
            new WeakReference<>(comp))
    );
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
      if (reg != null) {
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

    if (canCSSClassBeAddedToComponent(state, comp)) {
      comp.addClassName(state.getCssClass());
    }
  }

  private static boolean canCSSClassBeAddedToComponent(TooltipStateData state, HasStyle comp) {
    return ! (comp == null
        || state.getCssClass() == null
        || comp.getClassName() == null
        || comp.getClassName().contains(state.getCssClass()));
  }

  Map<Long, TooltipStateData> getTooltipStorage() {
    return this.tooltipStorage;
  }

  UI getUI() {
    return this.ui;
  }
}
