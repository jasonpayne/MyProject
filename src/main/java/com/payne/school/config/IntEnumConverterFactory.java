package com.payne.school.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @program: ccos-template
 * @description:
 * @author: lei.xu
 * @create: 2019-04-17 11:46
 **/
public class IntEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new IntegerToEnum(targetType);
    }

    private class IntegerToEnum<T extends BaseEnum> implements Converter<String, T> {

        private final T[] values;

        public IntegerToEnum(Class<T> targetType) {
            values = targetType.getEnumConstants();
        }

        @Override
        public T convert(String source) {
            for (T t : values) {
                if (t.getCode() == Integer.valueOf(source)) {
                    return t;
                }
            }
            return null;
        }
    }
}
