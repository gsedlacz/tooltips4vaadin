package dev.mett.vaadin.tooltip.config;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;

import com.vaadin.flow.component.JsonSerializable;
import com.vaadin.flow.internal.JsonSerializer;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class TooltipConfigurationJsonSerializer {
    public static JsonValue toJson(Object bean) {
        if (bean == null) {
            return null;
        }
        if (bean instanceof Collection) {
            return toJson((Collection<?>) bean);
        }
        if (bean.getClass().isArray()) {
            return toJsonArray(bean);
        }
        if (bean instanceof JsonSerializable) {
            return ((JsonSerializable) bean).toJson();
        }

        Optional<JsonValue> simpleType = tryToConvertToSimpleType(bean);
        if (simpleType.isPresent()) {
            return simpleType.get();
        }

        try {
            JsonObject json = Json.createObject();
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if ("class".equals(pd.getName())) {
                    continue;
                }
                Method reader = pd.getReadMethod();
                if (reader != null) {
                    JsonValue beanValue = toJson(reader.invoke(bean));
                    if (beanValue != null)
                        json.put(pd.getName(), beanValue);
                }
            }

            return json;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Could not serialize object of type "
                            + bean.getClass()
                            + " to JsonValue",
                    e
            );
        }
    }

    public static JsonArray toJson(Collection<?> beans) {
        JsonArray array = Json.createArray();
        if (beans == null) {
            return array;
        }

        beans.stream()
                .map(JsonSerializer::toJson)
                .forEachOrdered(json -> array.set(array.length(), json));
        return array;
    }

    private static JsonArray toJsonArray(Object javaArray) {
        int length = Array.getLength(javaArray);
        JsonArray array = Json.createArray();
        for (int i = 0; i < length; i++) {
            JsonValue beanValue = toJson(Array.get(javaArray, i));
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
        if (bean instanceof Enum) {
            return Optional.of(Json.create(((Enum<?>) bean).name()));
        }
        if (bean instanceof JsonValue) {
            return Optional.of((JsonValue) bean);
        }
        return Optional.empty();
    }
}
