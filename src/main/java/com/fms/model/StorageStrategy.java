package com.fms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StorageStrategy {

    /**
     * File storage rule: ${file.db.location} / ${tenant} / ${file_id}
     */
    FILE("FILE"),

    /**
     * File storage rule: ${file.db.location} / ${tenant} / ${date} / ${file_id}
     */
    FILE_PER_DATE("FILE_PER_DATE"),

    /**
     * File storage rule: ${file.db.location} / ${tenant} / ${year} / ${date} /${file_id}
     */
    FILE_PER_YEAR_DATE("FILE_PER_YEAR_DATE"),

    /**
     * File storage rule: ${file.db.location} / ${tenant} / ${year} / ${month} / ${day} /${file_id}
     */
    FILE_PER_YEAR_MONTH_DAY("FILE_PER_YEAR_MONTH_DAY"),

    /**
     * File storage rule: ${file.db.location} / ${tenant} / ${year}/ ${month} / ${file_id}
     */
    FILE_PER_YEAR_MONTH("FILE_PER_YEAR_MONTH"),

    /**
     * File storage rule: ${file.db.location} / ${tenant} / ${year}/ ${month} / {date} / ${file_id}
     */
    FILE_PER_YEAR_MONTH_DATE("FILE_PER_YEAR_MONTH_DATE")
    ;

    private final String name;

}
