package dev.mett.vaadin.tooltip.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Declares the preferred placement of the tooltip.
 *
 * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/#delay
 *
 * @author Gerrit Sedlaczek
 */
public enum TC_PLACEMENT implements JsonConvertible {
  TOP("top"),
  TOP_START("top-start"),
  TOP_END("top-end"),
  RIGHT("right"),
  RIGHT_START("right-start"),
  RIGHT_END("right-end"),
  BOTTOM("bottom"),
  BOTTOM_START("bottom-start"),
  BOTTOM_END("bottom-end"),
  LEFT("left"),
  LEFT_START("left-start"),
  LEFT_END("left-end"),
  AUTO("auto"),
  AUTO_START("auto-start"),
  AUTO_END("auto-end");

  @Getter
  private final Object value;
}
