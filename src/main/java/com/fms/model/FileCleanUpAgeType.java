package com.fms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileCleanUpAgeType {
    /***
     * The unit of measure in days
     */
    DAY("DAY"),

    /***
     * The unit of measure in months
     */
    MONTH("MONTH"),

    /***
     * The unit of measure in years
     */
    YEAR("YEAR"),

    /***
     * The unit of measure in years (legacy, misspelled)
     */
    YAER("YAER")
    ;

    private final String name;
}
