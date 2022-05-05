package dev.mett.vaadin.tooltip.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Determines when the tooltip is shown / hidden.
 *
 * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/#followcursor
 *
 * @author Gerrit Sedlaczek
 */
public enum TC_HIDE_ON_CLICK implements JsonConvertible {
  /**
   * DEFAULT | hides when clicking somewhere
   */
  TRUE(true),
  /**
   * hides never due to clicks
   */
  FALSE(false),
  /**
   * hides only by clicking the element on which tooltip got registered (not anywhere else)
   */
  HORIZONTAL("toggle");

  @Getter
  private final Object value;
}
