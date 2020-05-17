package dev.mett.vaadin.tooltip;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

import dev.mett.vaadin.tooltip.config.TooltipsConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
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
    private TooltipsConfiguration tooltipConfig;
    @Getter
    private final long tooltipId;
    @Getter
    @Setter
    private Integer tippyId;
    @Getter
    private final transient WeakReference<Component> component;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("TooltipStateData [tooltipId=");
        sb.append(tooltipId);
        sb.append(", tippyId=");
        sb.append(tippyId);
        sb.append(", component=");

        Component comp = this.component != null ? this.component.get() : null;
        sb.append(comp != null ? comp.getClass() : null);

        return sb.toString();
    }
}
