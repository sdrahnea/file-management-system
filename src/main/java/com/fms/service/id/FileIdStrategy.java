package com.fms.service.id;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This enum is used for file id strategy.
 * The available file id strategy modes:
 * 1. UUID      generate an UUID value for file id
 * 2. INSTANT   now value of instant converted to epoch
 *
 * WARN: use INSTANT option in case of low traffic.
 */

@Getter
@AllArgsConstructor
public enum FileIdStrategy {

    UUID("UUID"),
    INSTANT("INSTANT");

    private final String name;

}
