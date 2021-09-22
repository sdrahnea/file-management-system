package com.fms.service.id;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileIdStrategy {

    UUID("UUID"),
    INSTANT("INSTANT");

    private final String name;

}
