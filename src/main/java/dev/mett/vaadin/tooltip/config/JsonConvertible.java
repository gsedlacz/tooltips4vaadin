package dev.mett.vaadin.tooltip.config;

import java.io.Serializable;

import elemental.json.JsonValue;

interface JsonConvertible extends Serializable {
    Object getValue();

    default JsonValue toJson() {
        return TooltipConfigurationJsonSerializer.toJson(getValue());
    }
}
