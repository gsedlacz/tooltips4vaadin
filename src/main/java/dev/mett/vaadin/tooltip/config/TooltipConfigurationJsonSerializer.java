package dev.mett.vaadin.tooltip.config;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Optional;

import com.vaadin.flow.component.JsonSerializable;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonValue;
import elemental.json.impl.JreJsonNull;

public class TooltipConfigurationJsonSerializer {
    public static JsonValue toJson(Object bean) {
        return toJson(bean, false);
    }

    private static JsonValue toJson(Object bean, final boolean isNullabel) {
        if (bean == null) {
            return isNullabel ? new JreJsonNull() : null;
        }
        if (bean instanceof Collection) {
            return toJson((Collection<?>) bean);
        }
        if (bean.getClass().isArray()) {
            return toJsonArray(bean);
        }

        Optional<JsonValue> simpleType = tryToConvertToSimpleType(bean);
        if (simpleType.isPresent()) {
            return simpleType.get();
        }

        if (bean instanceof JsonConvertible) {
            return ((JsonConvertible) bean).toJson();
        }
        if (bean instanceof JsonSerializable) {
            return ((JsonSerializable) bean).toJson();
        }
        if (bean instanceof Enum) {
            return Json.create(((Enum<?>) bean).name());
        }
        if (bean instanceof JsonValue) {
            return (JsonValue) bean;
        }

        return null;
    }

    public static JsonArray toJson(Collection<?> beans) {
        JsonArray array = Json.createArray();
        if (beans == null) {
            return array;
        }

        beans.stream()
                .map(bean -> TooltipConfigurationJsonSerializer.toJson(bean, true))
                .forEachOrdered(json -> array.set(array.length(), json));
        return array;
    }

    private static JsonArray toJsonArray(Object javaArray) {
        int length = Array.getLength(javaArray);
        JsonArray array = Json.createArray();
        for (int i = 0; i < length; i++) {
            JsonValue beanValue = toJson(Array.get(javaArray, i), true);
            if (beanValue != null)
                array.set(i, beanValue);
        }
        return array;
    }

    static Optional<JsonValue> tryToConvertToSimpleType(Object bean) {
        if (bean instanceof String) {
            return Optional.of(Json.create((String) bean));
        }
        if (bean instanceof Number) {
            return Optional.of(Json.create(((Number) bean).doubleValue()));
        }
        if (bean instanceof Boolean) {
            return Optional.of(Json.create((Boolean) bean));
        }
        if (bean instanceof Character) {
            return Optional.of(Json.create(Character.toString((char) bean)));
        }
        return Optional.empty();
    }
}
