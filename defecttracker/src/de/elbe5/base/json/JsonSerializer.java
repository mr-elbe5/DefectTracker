package de.elbe5.base.json;

import de.elbe5.base.log.Log;
import de.elbe5.request.ApiRequestData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JsonSerializer {

    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public String serializeObject(Object obj) {
        try {
            Object o = toJSONInstance(obj);
            if (o instanceof JSONObject){
                return ((JSONObject) o).toJSONString();
            }
            else if (o instanceof JSONArray){
                return ((JSONArray) o).toJSONString();
            }
            return "";
        }
        catch (Exception e){
            Log.warn("Unable to serialize object");
            return "";
        }
    }

    private Object toJSONInstance(Object object) {

        if (object == null) {
            return null;
        }

        if (object instanceof JSONObject || object instanceof JSONArray
                || object instanceof Byte || object instanceof Character
                || object instanceof Short || object instanceof Integer
                || object instanceof Long || object instanceof Boolean
                || object instanceof Float || object instanceof Double
                || object instanceof String || object instanceof BigInteger
                || object instanceof BigDecimal || object instanceof Enum) {
            return object;
        }

        if (object instanceof LocalDate) {
            object = LocalDateTime.of((LocalDate) object, LocalTime.of(0,0,0));
        }

        if (object instanceof LocalDateTime) {
            return ((LocalDateTime) object).format(DateTimeFormatter.ofPattern(ISO_8601_PATTERN));
        }

        if (object instanceof Calendar) {
            object = ((Calendar) object).getTime(); //sets object to date, will be converted in next if-statement:
        }

        if (object instanceof Date) {
            Date date = (Date) object;
            return new SimpleDateFormat(ISO_8601_PATTERN).format(date);
        }

        if (object instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) object);
        }

        if (object instanceof char[]) {
            return new String((char[]) object);
        }

        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            return toJSONObject(map);
        }

        if (object.getClass().isArray()) {
            Collection c = Arrays.asList(toObjectArray(object));
            return toJSONArray(c);
        }

        if (object instanceof Collection) {
            Collection<?> coll = (Collection<?>) object;
            return toJSONArray(coll);
        }

        if (object.getClass().isAnnotationPresent(JsonData.class)) {
            return toJSONObject(object);
        }

        Log.warn("Unable to serialize object of type " + object.getClass().getName());
        throw new RuntimeException();
    }

    @SuppressWarnings("unchecked")
    private JSONObject toJSONObject(Object object) {
        JSONObject obj = new JSONObject();
        for (Class<?> c = object.getClass(); c != null; c = c.getSuperclass()) {
            if (c.isAnnotationPresent(JsonData.class)) {
                for (Field field : c.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(JsonField.class)) {
                        try {
                            obj.put(field.getName(), toJSONInstance(field.get(object)));
                        } catch (IllegalAccessException e) {
                            Log.warn("got no json value");
                        }
                    }
                }
            }
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private JSONObject toJSONObject(Map<?, ?> m) {

        JSONObject obj = new JSONObject();

        for (Map.Entry<?, ?> entry : m.entrySet()) {
            Object k = entry.getKey();
            Object value = entry.getValue();

            String key = String.valueOf(k);
            try {
                value = toJSONInstance(value);
                obj.put(key, value);
            }
            catch (Exception e){
                Log.warn("got no json value");
            }

        }

        return obj;
    }

    public static Object[] toObjectArray(Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }

    @SuppressWarnings("unchecked")
    private JSONArray toJSONArray(Collection c) {

        JSONArray array = new JSONArray();

        for (Object o : c) {
            try {
                o = toJSONInstance(o);
                array.add(o);
            }
            catch (Exception e){
                Log.warn("got no json value");
            }

        }

        return array;
    }

}
