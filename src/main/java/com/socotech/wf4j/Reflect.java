package com.socotech.wf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.lang.Validate;

/**
 * Helper methods for reflection activities
 */
public class Reflect {
    /**
     * Resolves the property name to a Java Field reference.  Simple, indexed, and mapped properties are handled.
     *
     * @param o    bean object
     * @param name simple, indexed, or mapped property name
     * @return field reference
     * @throws InvocationTargetException unreadable field
     * @throws NoSuchMethodException     no getter for private field?
     * @throws IllegalAccessException    private field
     */
    public static Field getDeclaredField(Object o, String name) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Validate.notNull(o, "Object cannot be null");
        Validate.notEmpty(name, "Name cannot be empty");
        Resolver resolver = new DefaultResolver();
        // Iterate through a nested property expression
        while (resolver.hasNested(name)) {
            // isolate a single property from a nested expression
            String next = resolver.next(name);
            // Process...
            String property = resolver.getProperty(next);
            if (resolver.isIndexed(next)) {
                int index = resolver.getIndex(next);
                o = PropertyUtils.getIndexedProperty(o, property, index);
            } else if (resolver.isMapped(next)) {
                String key = resolver.getKey(next);
                o = PropertyUtils.getMappedProperty(o, property, key);
            } else if (PropertyUtils.isWriteable(o, property)) {
                o = PropertyUtils.getSimpleProperty(o, property);
            } else {
                return null; // field is not writable
            }
            // remove the processed property from the expression
            name = resolver.remove(name);
        }
        Class<?> clazz = o.getClass();
        String canonicalName = name.replaceAll(Patterns.INDEX_REFERENCE.pattern(), "");
        do {
            try {
                return clazz.getDeclaredField(canonicalName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        } while (clazz != null);
        // no field in target class or any ancestor
        return null;
    }
}
