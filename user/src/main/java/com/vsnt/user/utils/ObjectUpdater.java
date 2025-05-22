package com.vsnt.user.utils;

import java.lang.reflect.Field;

public class ObjectUpdater {
    public static <D, T> T update(T target, D source) {
        if (target == null || source == null) return null;

        Class<?> targetClass = target.getClass();
        Class<?> sourceClass = source.getClass();

        for (Field sourceField : sourceClass.getDeclaredFields()) {
            try {
                Field targetField = targetClass.getDeclaredField(sourceField.getName());
               sourceField.setAccessible(true);
                Object value = sourceField.get(sourceField.getDeclaringClass().cast(source));

                if (value != null) {
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Optional: log or ignore fields that are not present in source
                e.printStackTrace();
            }
        }

        return target;
    }
}
