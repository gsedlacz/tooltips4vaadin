package dev.mett.vaadin.tooltip;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class TooltipStateData implements Serializable {
    private static final long serialVersionUID = 1718507240810676034L;

    @Getter
    private WeakReference<Registration> attachReg;
    @Getter
    private WeakReference<Registration> detachReg;
    @Getter
    @Setter
    private String cssClass;
    @Getter
    @Setter
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

    public void setAttachReg(WeakReference<Registration> attachReg) {
        setRegistration(this.attachReg, attachReg);
    }

    public void setDetachReg(WeakReference<Registration> detachReg) {
        setRegistration(this.detachReg, detachReg);
    }

    private void setRegistration(WeakReference<Registration> thisReg, WeakReference<Registration> newReg) {
        if (thisReg != null) {
            Registration oldReg = thisReg.get();
            if (oldReg != null) {
                oldReg.remove();
            }
        }
        thisReg = attachReg;
    }
}
