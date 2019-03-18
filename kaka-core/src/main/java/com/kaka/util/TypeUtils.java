package com.kaka.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author zhoukai
 */
public class TypeUtils {

    public static final Object castNullTo(Object value, Object defVal) {
        if (value == null) {
            return defVal;
        }
        return value;
    }

    public static final String castToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static final Byte castToByte(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            return Byte.parseByte(strVal);
        }
        throw new TypeException("can not cast to byte, value : " + value);
    }

    public static final Character castToChar(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Character) {
            return (Character) value;
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            if (strVal.length() != 1) {
                throw new TypeException("can not cast to byte, value : " + value);
            }
            return strVal.charAt(0);
        }
        throw new TypeException("can not cast to byte, value : " + value);
    }

    public static final Short castToShort(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            return Short.parseShort(strVal);
        }
        throw new TypeException("can not cast to short, value : " + value);
    }

    public static final BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }
        if ("null".equals(strVal) || "NULL".equals(strVal)) {
            return null;
        }
        return new BigDecimal(strVal);
    }

    public static final BigInteger castToBigInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }
        if (value instanceof Float || value instanceof Double) {
            return BigInteger.valueOf(((Number) value).longValue());
        }
        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }
        if ("null".equals(strVal) || "NULL".equals(strVal)) {
            return null;
        }
        return new BigInteger(strVal);
    }

    public static final Float castToFloat(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            return Float.parseFloat(strVal);
        }
        throw new TypeException("can not cast to float, value : " + value);
    }

    public static final Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            return Double.parseDouble(strVal);
        }
        throw new TypeException("can not cast to double, value : " + value);
    }

    public static final Date castToDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        long longValue = -1;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
            return new Date(longValue);
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            if (strVal.indexOf('-') != -1) {
                String format;
                if (strVal.length() == 10) {
                    format = "yyyy-MM-dd";
                } else if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
                    format = "yyyy-MM-dd HH:mm:ss";
                } else {
                    format = "yyyy-MM-dd HH:mm:ss.SSS";
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                try {
                    return (Date) dateFormat.parse(strVal);
                } catch (ParseException e) {
                    throw new TypeException("can not cast to Date, value : " + strVal);
                }
            }
            if (strVal.length() == 0) {
                return null;
            }
            longValue = Long.parseLong(strVal);
        }
        if (longValue < 0) {
            throw new TypeException("can not cast to Date, value : " + value);
        }
        return new Date(longValue);
    }

    public static final java.sql.Date castToSqlDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return new java.sql.Date(((Calendar) value).getTimeInMillis());
        }
        if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) value).getTime());
        }
        long longValue = 0;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            longValue = Long.parseLong(strVal);
        }
        if (longValue <= 0) {
            throw new TypeException("can not cast to Date, value : " + value);
        }
        return new java.sql.Date(longValue);
    }

    public static java.sql.Time castToSqlTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.sql.Time) {
            return (java.sql.Time) value;
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Time(((java.util.Date) value).getTime());
        }
        if (value instanceof Calendar) {
            return new java.sql.Time(((Calendar) value).getTimeInMillis());
        }
        long longValue = 0;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 || "null".equalsIgnoreCase(strVal)) {
                return null;
            }
            longValue = Long.parseLong(strVal);
        }
        if (longValue <= 0) {
            throw new TypeException("can not cast to Date, value : " + value); // TODO 忽略 1970-01-01 之前的时间处理？
        }
        return new java.sql.Time(longValue);
    }

    public static final java.sql.Timestamp castToTimestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return new java.sql.Timestamp(((Calendar) value).getTimeInMillis());
        }
        if (value instanceof java.sql.Timestamp) {
            return (java.sql.Timestamp) value;
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        }
        long longValue = 0;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            longValue = Long.parseLong(strVal);
        }
        if (longValue <= 0) {
            throw new TypeException("can not cast to Date, value : " + value);
        }
        return new java.sql.Timestamp(longValue);
    }

    public static final Long castToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            try {
                return Long.parseLong(strVal);
            } catch (NumberFormatException ex) {
                //
            }
        }
        throw new TypeException("can not cast to long, value : " + value);
    }

    public static final Integer castToInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            return Integer.parseInt(strVal);
        }
        throw new TypeException("can not cast to int, value : " + value);
    }

    public static final Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        if (value instanceof String) {
            String strVal = (String) value;

            if (strVal.length() == 0) {
                return null;
            }
            if ("true".equalsIgnoreCase(strVal)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(strVal)) {
                return Boolean.FALSE;
            }
            if ("1".equals(strVal)) {
                return Boolean.TRUE;
            }
            if ("0".equals(strVal)) {
                return Boolean.FALSE;
            }
            if ("null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
        }
        throw new TypeException("can not cast to boolean, value : " + value);
    }

    public static final Object cast(Object value, ClassCastException ex) throws ClassNotFoundException {
        String exInfo = ex.getLocalizedMessage();
        int idx = exInfo.lastIndexOf(" ");
        String targetClassStr = exInfo.substring(idx + 1);
        Class targetClass = Class.forName(targetClassStr);
        return TypeUtils.cast(value, targetClass);
    }

    public static final <T> T cast(Object value, Class<T> target) {
        if (value == null) {
            return (T) null;
        }
        if (target == byte.class) {
            return (T) TypeUtils.castToByte(value);
        } else if (target == char.class) {
            return (T) TypeUtils.castToChar(value);
        } else if (target == short.class) {
            return (T) TypeUtils.castToShort(value);
        } else if (target == int.class) {
            return (T) TypeUtils.castToInt(value);
        } else if (target == long.class) {
            return (T) TypeUtils.castToLong(value);
        } else if (target == float.class) {
            return (T) TypeUtils.castToFloat(value);
        } else if (target == double.class) {
            return (T) TypeUtils.castToDouble(value);
        } else if (target == boolean.class) {
            return (T) TypeUtils.castToBoolean(value);
        } else if (target == Character.class) {
            return (T) TypeUtils.castToChar(value);
        } else if (target == Short.class) {
            return (T) TypeUtils.castToShort(value);
        } else if (target == Integer.class) {
            return (T) TypeUtils.castToInt(value);
        } else if (target == Long.class) {
            return (T) TypeUtils.castToLong(value);
        } else if (target == Float.class) {
            return (T) TypeUtils.castToFloat(value);
        } else if (target == Double.class) {
            return (T) TypeUtils.castToDouble(value);
        } else if (target == Byte.class) {
            return (T) TypeUtils.castToByte(value);
        } else if (target == Boolean.class) {
            return (T) TypeUtils.castToBoolean(value);
        } else if (target == Date.class) {
            return (T) TypeUtils.castToDate(value);
        } else if (target == BigInteger.class) {
            return (T) TypeUtils.castToBigInteger(value);
        } else if (target == BigDecimal.class) {
            return (T) TypeUtils.castToBigDecimal(value);
        } else if (target == Date.class) {
            return (T) TypeUtils.castToDate(value);
        } else if (target == java.sql.Date.class) {
            return (T) TypeUtils.castToSqlDate(value);
        } else if (target == java.sql.Time.class) {
            return (T) TypeUtils.castToSqlTime(value);
        } else if (target == java.sql.Timestamp.class) {
            return (T) TypeUtils.castToTimestamp(value);
        } else if (target == String.class) {
            return (T) TypeUtils.castToString(value);
        } else if (target.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (target == value.getClass()) {
            return (T) target.cast(value);
        }
        return (T) value;
    }

    private static final ConcurrentMap<String, Class<?>> mappings = new ConcurrentHashMap<>();

    static {
        addBaseClassMappings();
    }

    public static void addClassMapping(String className, Class<?> clazz) {
        if (className == null) {
            className = clazz.getName();
        }
        mappings.put(className, clazz);
    }

    public static void addBaseClassMappings() {
        mappings.put("byte", byte.class);
        mappings.put("short", short.class);
        mappings.put("int", int.class);
        mappings.put("long", long.class);
        mappings.put("float", float.class);
        mappings.put("double", double.class);
        mappings.put("boolean", boolean.class);
        mappings.put("char", char.class);

        mappings.put("[byte", byte[].class);
        mappings.put("[short", short[].class);
        mappings.put("[int", int[].class);
        mappings.put("[long", long[].class);
        mappings.put("[float", float[].class);
        mappings.put("[double", double[].class);
        mappings.put("[boolean", boolean[].class);
        mappings.put("[char", char[].class);

        mappings.put(HashMap.class.getName(), HashMap.class);
    }

    public static void clearClassMapping() {
        mappings.clear();
        addBaseClassMappings();
    }

    public static Class<?> loadClass(String className) {
        if (className == null || className.length() == 0) {
            return null;
        }

        Class<?> clazz = mappings.get(className);

        if (clazz != null) {
            return clazz;
        }

        if (className.charAt(0) == '[') {
            Class<?> componentType = loadClass(className.substring(1));
            return Array.newInstance(componentType, 0).getClass();
        }

        if (className.startsWith("L") && className.endsWith(";")) {
            String newClassName = className.substring(1, className.length() - 1);
            return loadClass(newClassName);
        }

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            if (classLoader != null) {
                clazz = classLoader.loadClass(className);

                addClassMapping(className, clazz);

                return clazz;
            }
        } catch (Throwable e) {
            // skip
        }

        try {
            clazz = Class.forName(className);

            addClassMapping(className, clazz);

            return clazz;
        } catch (Throwable e) {
            // skip
        }

        return clazz;
    }

    public static boolean isGenericParamType(java.lang.reflect.Type type) {
        if (type instanceof ParameterizedType) {
            return true;
        }

        if (type instanceof Class) {
            return isGenericParamType(((Class<?>) type).getGenericSuperclass());
        }

        return false;
    }

    public static java.lang.reflect.Type getGenericParamType(java.lang.reflect.Type type) {
        if (type instanceof ParameterizedType) {
            return type;
        }

        if (type instanceof Class) {
            return getGenericParamType(((Class<?>) type).getGenericSuperclass());
        }

        return type;
    }

    public static java.lang.reflect.Type unwrap(java.lang.reflect.Type type) {
        if (type instanceof GenericArrayType) {
            java.lang.reflect.Type componentType = ((GenericArrayType) type).getGenericComponentType();
            if (componentType == byte.class) {
                return byte[].class;
            }
            if (componentType == char.class) {
                return char[].class;
            }
        }

        return type;
    }

    public static Class<?> getClass(java.lang.reflect.Type type) {
        if (type.getClass() == Class.class) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        }

        return Object.class;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            return getField(superClass, fieldName);
        }

        return null;
    }

    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))
                && Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

}
