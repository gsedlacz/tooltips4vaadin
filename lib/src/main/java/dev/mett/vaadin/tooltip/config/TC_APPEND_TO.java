package dev.mett.vaadin.tooltip.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the element to which the tooltip should be appended
 *
 * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/#appendto
 */
@RequiredArgsConstructor
public enum TC_APPEND_TO implements JsonConvertible {
  PARENT("parent"),
  DOCUMENT_BODY("document.body");

  @Getter
  private final String value;
}
