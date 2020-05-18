package dev.mett.vaadin.tooltip;

import java.io.Serializable;

import elemental.json.JsonObject;
import lombok.Data;

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
@Data
public class TooltipConfiguration implements Serializable {
    private static final long serialVersionUID = 7099314666522619319L;

    /**
     * INTERNAL
     */
    TooltipConfiguration() {
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

    // TODO: use enums

    /** Defines if the content is rendered as HTML or plain text */
    private Boolean allowHTML = true;
//    private boolean animateFill; //TODO: requires CSS import
    /** Defines if the tooltip points to its parent element */
    private Boolean arrow; // TODO: support 1. SVG 2. Element
    /** This is the tooltips text itself */
    private String content = "";
    /** Delay in ms before the tooltip is shown or hidden */
    private Integer delay; // TODO: individual in and out values
    /** Duration of transition animations in ms */
    private Integer duration; // TODO: individual in and out
    /**
     * Defines the positon of a tooltip relative to its element and the cursor.<br>
     * Supported values: 1. 'horizontal' 2. 'vertical' 3. 'initial'
     */
    private String followCursor; // TODO: support booleans
    /** Determines when the tooltip is shown / hidden */
    private Boolean hideOnClick; // TODO: support 'toggle'
    /**
     * Determines if a spring-like animation is applied to the transition animation
     */
    private Boolean inertia;
    /** Defines if content of a tooltip can be selected */
    private Boolean interactive;
    /**
     * Defines the invisible space around the tooltip whithin which the mouse wont
     * leave the tooltip (in px)
     */
    private Integer interactiveBorder;
    /**
     * The time in ms until the tooltip disappears after the mouse left the tooltips
     * area
     */
    private Integer interactiveDebounce;
    /** The maximum width of a tooltip in px */
    private Integer maxWidth; // TODO: support 'none'
    /** If the tooltip should be shown right after its creation */
    private Boolean showOnCreate;
    /** JS events that should trigger opening the tooltip (separated by spaces) */
    private String trigger;
    /** CSS z-index */
    private Integer zIndex;

    /**
     * Defines the tooltips text.
     *
     * @param content text
     */
    public void setContent(String content) {
        if (content == null || content.isEmpty())
            throw new RuntimeException("The content of a tooltip may never be null or empty");

        // newlines to html
        content = content.replaceAll("(\\r\\n|\\r|\\n)", "<br>");
        this.content = content;
    }

    public JsonObject toJson() {
        return (JsonObject) TooltipsUtil.convertToJson(this);
    }
}
