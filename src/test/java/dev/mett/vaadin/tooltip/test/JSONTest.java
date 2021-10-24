package dev.mett.vaadin.tooltip.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.mett.vaadin.tooltip.config.TC_FOLLOW_CURSOR;
import dev.mett.vaadin.tooltip.config.TC_HIDE_ON_CLICK;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;
import elemental.json.JsonObject;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

public class JSONTest {

  public static final String DEFAULT_JSON_BODY = "\"allowHTML\":true,\"content\":\"\"";

  @Test
  public void defaultConversion() {
    assertEquals("{" + DEFAULT_JSON_BODY + "}", new TooltipConfiguration().toJsonObject().toJson());
  }

  @Test
  public void basicConversion() {
    TooltipConfiguration config = new TooltipConfiguration("test text");
    config.setDuration(null, 20);
    config.setContent("test \n<br> abc");
    config.setFollowCursor(TC_FOLLOW_CURSOR.HORIZONTAL);
    config.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
    config.setShowOnCreate(false);
    config.setTouch("hold", 500);

    JsonObject json = config.toJsonObject();
    assertEquals(
        "{\"allowHTML\":true,\"content\":\"test <br><br> abc\",\"duration\":[null,20],\"followCursor\":\"horizontal\",\"hideOnClick\":false,\"showOnCreate\":false,\"touch\":[\"hold\",500]}",
        json.toJson()
    );
  }


  @Test
  public void popperOptions() {
    TooltipConfiguration config = new TooltipConfiguration();
    config.addPopperOption("strategy", "fixed");
    config.addPopperOption("something", "else");

    JsonObject json = config.toJsonObject();
    assertEquals("{" + DEFAULT_JSON_BODY + ",\"popperOptions\":{\"strategy\":\"fixed\",\"something\":\"else\"}}", json.toJson());
  }


  @RequiredArgsConstructor
  private static class PopperOptionsModifier {

    private final String name;
    private final Map<String, Object> options;
  }

  @Test
  public void popperOptionsComplex() {
    TooltipConfiguration config = new TooltipConfiguration();
    config.addPopperOption(
        "modifiers",
        new PopperOptionsModifier[]{
            new PopperOptionsModifier(
                "flip",
                mapOf(
                    "fallbackPlacements",
                    new String[]{
                        "bottom",
                        "right"
                    })),
            new PopperOptionsModifier(
                "preventOverflow",
                mapOf(
                    "altAxis",
                    true,
                    "tether",
                    false
                )
            )
        }
    );

    JsonObject json = config.toJsonObject();
    assertEquals("{"
            + DEFAULT_JSON_BODY
            + ",\"popperOptions\":{\"modifiers\":[{\"name\":\"flip\",\"options\":{\"fallbackPlacements\":[\"bottom\",\"right\"]}},{\"name\":\"preventOverflow\",\"options\":{\"altAxis\":true,\"tether\":false}}]}}",
        json.toJson());
  }

  // Map.of(a, b, ...) replacement due Java 1.8 constraint

  private Map<String, Object> mapOf(String key1, Object value1, String key2, Object value2) {
    Map<String, Object> map = new HashMap<>();
    map.put(key1, value1);
    map.put(key2, value2);
    return map;
  }

  private Map<String, Object> mapOf(String key1, Object value1) {
    Map<String, Object> map = new HashMap<>();
    map.put(key1, value1);
    return map;
  }
}
