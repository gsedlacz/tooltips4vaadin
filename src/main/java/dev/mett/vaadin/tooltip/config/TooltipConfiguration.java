package dev.mett.vaadin.tooltip.config;

import java.io.Serializable;

import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.exception.InvalidTooltipContentException;
import elemental.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Allows you to customize tooltips properties.<br>
 * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/<br>
 * <br>
 * NOTE: The configuration wont update on its own. Call to
 * {@link Tooltips#setTooltip(com.vaadin.flow.component.Component, TooltipConfiguration)}
 * in order to apply changes.
 *
 * @author Gerrit Sedlaczek
 */
@EqualsAndHashCode
@ToString
public class TooltipConfiguration implements Serializable {
    private static final long serialVersionUID = 7099314666522619319L;

    /**
     * INTERNAL
     */
    public TooltipConfiguration() {
        /* internal constructor */
    }

    /**
     * Creates a new {@link TooltipConfiguration}.<br>
     * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/
     *
     * @param text the tooltips text
     */
    public TooltipConfiguration(String text) {
        setContent(text);
    }

    // TODO: support:
    // 1. placement
    // 2. animateFill
    // 3. animation
    // 4. appendTo
    // 5. aria
    // 6. ignoreAttributes (TODO: check if it should always be true)
    // 7. inlinePositioning

    // TODO: use enums

    /** Defines if the content is rendered as HTML or plain text */
    @Getter
    @Setter
    private Boolean allowHTML = true;

//    private boolean animateFill; //TODO: requires CSS import

    /** Defines if the tooltip points to its parent element */
    @Getter
    @Setter
    private Boolean arrow; // TODO: support 1. SVG 2. Element

    /** This is the tooltips text itself */
    @Getter
    private String content = "";

    @Getter
    private Object delay;

    @Getter
    private Object duration;

    @Getter
    @Setter
    private TC_FOLLOW_CURSOR followCursor;

    @Getter
    @Setter
    private TC_HIDE_ON_CLICK hideOnClick;

    /**
     * Determines if a spring-like animation is applied to the transition animation
     */
    @Getter
    @Setter
    private Boolean inertia;

    /** Defines if content of a tooltip can be selected */
    @Getter
    @Setter
    private Boolean interactive;

    /**
     * Defines the invisible space around the tooltip whithin which the mouse wont
     * leave the tooltip (in px)
     */
    @Getter
    @Setter
    private Integer interactiveBorder;

    /**
     * The time in ms until the tooltip disappears after the mouse left the tooltips
     * area
     */
    @Getter
    @Setter
    private Integer interactiveDebounce;

    @Getter
    private Object maxWidth;

    /**
     * Describes the transition between position updates of a tooltip (see:
     * https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions)
     */
    @Getter
    @Setter
    private String moveTransition;

    @Getter
    private Integer[] offset;

    @Getter
    @Setter
    private TC_PLACEMENT placement;

    /** If the tooltip should be shown right after its creation */
    @Getter
    @Setter
    private Boolean showOnCreate;

    @Getter
    @Setter
    private TC_STICKY sticky;

    @Getter
    @Setter
    private String theme;

    /** JS events that should trigger opening the tooltip (separated by spaces) */
    private String trigger;

    @Getter
    @Setter
    private Integer zIndex;

    /*
     * ### SETTER ###
     */

    /**
     * Delay in ms before the tooltip is shown or hidden.
     *
     * @param delay ms (in/out)
     */
    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    /**
     * Delay in ms before the tooltip is shown or hidden.
     *
     * @param showDelay ms(in)
     * @param hideDelay ms(out)
     */
    public void setDelay(Integer showDelay, Integer hideDelay) {
        this.delay = new Integer[] { showDelay, hideDelay };
    }

    /**
     * Duration of transition animations in ms.
     *
     * @param duration ms (in/out)
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * Duration of transition animations in ms.
     *
     * @param showDuration ms (in)
     * @param hideDuration ms (out)
     */
    public void setDuration(Integer showDuration, Integer hideDuration) {
        this.duration = new Integer[] { showDuration, hideDuration };
    }

    /**
     * The maximum width of a tooltip in pixels.
     *
     * @param pixel max width
     */
    public void setMaxWidth(Integer pixel) {
        this.maxWidth = pixel;
    }

    /**
     * Removes the maximum width limit of the tooltip.
     */
    public void setMaxWidthNone() {
        this.maxWidth = "none";
    }

    /**
     * see: https://popper.js.org/docs/v2/modifiers/offset/
     *
     * @param skidding
     * @param distance
     */
    public void setOffset(int skidding, int distance) {
        this.offset = new Integer[] { skidding, distance };
    }

    /**
     * Defines the tooltips text.
     *
     * @param content text
     */
    public void setContent(String content) {
        if (content == null || content.isEmpty())
            throw new InvalidTooltipContentException("The content of a tooltip may never be null or empty");

        // newlines to html
        content = content.replaceAll("(\\r\\n|\\r|\\n)", "<br>");
        this.content = content;
    }

    public JsonObject toJson() {
        return (JsonObject) TooltipConfigurationJsonSerializer.toJson(this);
    }
}
