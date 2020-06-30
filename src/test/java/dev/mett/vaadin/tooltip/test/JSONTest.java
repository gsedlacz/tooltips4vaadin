package dev.mett.vaadin.tooltip.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dev.mett.vaadin.tooltip.config.TC_FOLLOW_CURSOR;
import dev.mett.vaadin.tooltip.config.TC_HIDE_ON_CLICK;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;
import elemental.json.JsonObject;

public class JSONTest {

    @Test
    public void testJSONConverter() {
        TooltipConfiguration config = new TooltipConfiguration("test text");
        config.setDuration(null, 20);
        config.setContent("test \n<br> abc");
        config.setFollowCursor(TC_FOLLOW_CURSOR.HORIZONTAL);
        config.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
        config.setShowOnCreate(false);
        config.setTouch("hold", 500);

        JsonObject json = config.toJson();

        assertEquals(
                "{\"allowHTML\":true,\"content\":\"test <br><br> abc\",\"duration\":[null,20],\"followCursor\":\"horizontal\",\"hideOnClick\":false,\"showOnCreate\":false,\"touch\":[\"hold\",500]}",
                json.toJson()
        );
    }
}
