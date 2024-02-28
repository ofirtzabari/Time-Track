package com.example.timetrack;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;

public class ObjectToMapConverter {
        public static Map<String, Object> convertObjectToMap(Object obj) {
            Map<String, Object> map = new HashMap<>();
            try {
                Class<?> clazz = obj.getClass();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true); // Enable access to private fields
                    Object value = field.get(obj);
                    map.put(field.getName(), value);
                }
            }catch (IllegalAccessException ignored) {

            }
            return map;
        }
}
