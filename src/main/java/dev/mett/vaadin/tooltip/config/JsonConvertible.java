package dev.mett.vaadin.tooltip.config;

import elemental.json.JsonValue;

interface JsonConvertible {
    Object getValue();

    default JsonValue toJson() {
        return TooltipConfigurationJsonSerializer.toJson(getValue());
    }
}
