package dev.mett.vaadin.tooltip.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Defines the position of a tooltip relative to its element and the cursor.
 *
 * Documentation: https://atomiks.github.io/tippyjs/v6/all-props/#followcursor
 *
 * @author Gerrit Sedlaczek
 */
public enum TC_FOLLOW_CURSOR implements JsonConvertible {
  TRUE(true),
  FALSE(false),
  HORIZONTAL("horizontal"),
  VERTICAL("vertical"),
  INITIAL("initial");

  @Getter
  private final Object value;
}
