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
    // 1. animateFill
    // 2. animation
    // 3. appendTo
    // 4. aria
    // 5. getReferenceClientRect
    // 6. inlinePositioning

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

    /**
     * Defines the position of a tooltip relative to its element and the cursor.
     *
     * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/#followcursor
     */
    @Getter
    @Setter
    private TC_FOLLOW_CURSOR followCursor;

    /**
     * Determines when the tooltip is shown / hidden.
     *
     * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/#followcursor
     */
    @Getter
    @Setter
    private TC_HIDE_ON_CLICK hideOnClick;

    /**
     * When using UI (component) libraries like React, this is generally not
     * necessary and slows down initialization perf a bit. | false by default
     */
    @Getter
    @Setter
    private Boolean ignoreAttributes;

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

    /**
     * Declares the preferred placement of the tooltip.
     *
     * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/#delay
     */
    @Getter
    @Setter
    private TC_PLACEMENT placement;

    /** Specifies the role attribute on the tippy element */
    @Getter
    @Setter
    private String role;

    /** If the tooltip should be shown right after its creation */
    @Getter
    @Setter
    private Boolean showOnCreate;

    /**
     * Determines if the tippy sticks to the reference element while it is mounted.
     *
     * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/#sticky
     */
    @Getter
    @Setter
    private TC_STICKY sticky;

    /** Determines the theme of the tippy element */
    @Getter
    @Setter
    private String theme;

    /** Determines the behavior on touch devices. */
    @Getter
    private Object touch;

    /** JS events that should trigger opening the tooltip (separated by spaces) */
    @Getter
    @Setter
    private String trigger;

    /** Specifies the z-index CSS on the root popper node */
    @Getter
    @Setter
    private Integer zIndex;

    public JsonObject toJson() {
        return (JsonObject) TooltipConfigurationJsonSerializer.toJson(this);
    }

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
     * @param skidding see: https://popper.js.org/docs/v2/modifiers/offset/#skidding
     * @param distance see: https://popper.js.org/docs/v2/modifiers/offset/#distance
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

    /**
     * Determines the behavior on touch devices<br>
     * <br>
     * see: https://atomiks.github.io/tippyjs/v6/all-props/#touch
     *
     * @param supportsTouch if touch input triggers a tooltip (default = true)
     */
    public void setTouch(Boolean supportsTouch) {
        this.touch = supportsTouch;
    }

    /**
     * Determines the behavior on touch devices<br>
     * <br>
     * see: https://atomiks.github.io/tippyjs/v6/all-props/#touch
     *
     * @param touchTrigger the type of touch trigger
     */
    public void setTouch(String touchTrigger) {
        this.touch = touchTrigger;
    }

    /**
     * Determines the behavior on touch devices<br>
     * <br>
     * see: https://atomiks.github.io/tippyjs/v6/all-props/#touch
     *
     * @param touchTrigger the type of touch trigger
     * @param duration     the it takes to trigger an action
     */
    public void setTouch(String touchTrigger, int duration) {
        this.touch = new Object[] { touchTrigger, duration };
    }
}
