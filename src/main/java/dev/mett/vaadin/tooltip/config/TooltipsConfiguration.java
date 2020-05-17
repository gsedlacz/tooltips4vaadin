package dev.mett.vaadin.tooltip.config;

import java.io.Serializable;

import lombok.Data;

@Data
/**
 * Allows you to customize tooltips properties.<br>
 * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/
 *
 * @author Gerrit Sedlaczek
 */
public class TooltipsConfiguration implements Serializable {
    private static final long serialVersionUID = 7099314666522619319L;

    /**
     * Creates a new tooltip configuration.
     *
     * @param content the initial text of the tooltip (<code>null</code> is not
     *                supported.
     */
    public TooltipsConfiguration(String content) {
        if (content == null || content.isEmpty())
            throw new RuntimeException("The content of a tooltip may never be null or empty");

        // newlines to html
        content = content.replaceAll("(\\r\\n|\\r|\\n)", "<br>");
        this.content = content;
    }

    // TODO: support:
    // 1. placement
    // 2. animateFill
    // 3. animation
    // 4. appendTo
    // 5. aria

    /** Defines if the content is rendered as HTML or plain text */
    private boolean allowHTML = true;
//    private boolean animateFill; //TODO: requires CSS import
    /** Defines if the tooltip points to its parent element */
    private boolean arrow; // TODO: support 1. SVG 2. Element
    /** This is the tooltips text itself */
    private String content;
    /** Delay in ms before the tooltip is shown or hidden */
    private int delay; // TODO: individual in and out values
    /** Duration of transition animations in ms */
    private int duration; // TODO: individual in and out
    /**
     * Defines the positon of a tooltip relative to its element and the cursor.<br>
     * Supported values: 1. 'horizontal' 2. 'vertical' 3. 'initial'
     */
    private String followCursor; // TODO: support booleans
    /** Determines when the tooltip is shown / hidden */
    private boolean hideOnClick; // TODO: support 'toggle'
    /**
     * Determines if a spring-like animation is applied to the transition animation
     */
    private boolean inertia;
    /** Defines if content of a tooltip can be selected */
    private boolean interactive;
    /**
     * Defines the invisible space around the tooltip whithin which the mouse wont
     * leave the tooltip (in px)
     */
    private int interactiveBorder;
    /**
     * The time in ms until the tooltip disappears after the mouse left the tooltips
     * area
     */
    private int interactiveDebounce;
    /** The maximum width of a tooltip in px */
    private int maxWidth; // TODO: support 'none'
    /** If the tooltip should be shown right after its creation */
    private boolean showOnCreate;
    /** JS events that should trigger opening the tooltip (separated by spaces) */
    private String trigger;
    /** CSS z-index */
    private int zIndex;
}
