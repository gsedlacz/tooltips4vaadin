package dev.mett.vaadin.tooltip;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
class TooltipStateData implements Serializable {

  private static final long serialVersionUID = 1718507240810676034L;

  @Getter
  private WeakReference<Registration> attachReg;
  @Getter
  private WeakReference<Registration> detachReg;
  @Getter
  @Setter
  private String frontendId;
  @Getter
  @Setter
  @NonNull
  private TooltipConfiguration tooltipConfig;
  @Getter
  private final long tooltipId;
  @Getter
  @Setter
  private Integer tippyId;
  @Getter
  private final transient WeakReference<Component> component;

  /**
   * INTERNAL
   */
  TooltipStateData(TooltipConfiguration config, long tooltipId, WeakReference<Component> component) {
    this.tooltipConfig = config;
    this.tooltipId = tooltipId;
    this.component = component;
  }

  void setAttachReg(WeakReference<Registration> attachReg) {
    clearRegistration(this.attachReg);
    this.attachReg = attachReg;
  }

  void setDetachReg(WeakReference<Registration> detachReg) {
    clearRegistration(this.detachReg);
    this.detachReg = detachReg;
  }

  private void clearRegistration(WeakReference<Registration> previousRegistration) {
    if (previousRegistration != null) {
      Registration oldReg = previousRegistration.get();
      if (oldReg != null) {
        oldReg.remove();
      }
    }
  }
}
