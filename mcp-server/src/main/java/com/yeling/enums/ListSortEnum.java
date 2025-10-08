package com.yeling.enums;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ListSortEnum
 * @Date 2025/10/8 16:13
 * @Version 1.0
 */
public enum ListSortEnum {

    ASC("ASC", "升序"),
    DESC("DESC", "降序");

    private final String type;
    private final String value;

    ListSortEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
