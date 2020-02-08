package com.xinchao.config;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *
 */
@JsonSerialize(using = BaseEnumSerializer.class)
public interface BaseEnum {
    /**
     * fetch enum code
     *
     * @return 枚举 Code 值
     */
    int getCode();
}
