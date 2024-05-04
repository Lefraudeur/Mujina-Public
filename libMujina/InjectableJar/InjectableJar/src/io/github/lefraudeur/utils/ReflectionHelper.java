package io.github.lefraudeur.utils;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;

public class ReflectionHelper {

    public static Object getFieldValue(final Object target, final String obfName, final String deobfName)
    {
        if(target == null) return null;

        final Class<?> cls = target.getClass();
        Field field = null;

        for(Class<?> cls1 = cls; cls1 != null; cls1 = cls1.getSuperclass())
        {
            try
            {
                field = cls1.getDeclaredField(obfName);
            }
            catch (final Exception e)
            {
                try
                {
                    field = cls1.getDeclaredField(deobfName);
                }
                catch (final Exception e1)
                {
                    continue;
                }
            }

            if(!field.isAccessible()) field.setAccessible(true);

            try
            {
                return field.get(target);
            }
            catch (final Exception e)
            {
                throw new RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @" + target.getClass().getSimpleName());
            }
        }

        final int size = ClassUtils.getAllInterfaces(cls).size() - 1;
        int i;

        for(i = size; i >= 0; --i)
        {
            final Class<?> class1 = ClassUtils.getAllInterfaces(cls).get(i);

            try
            {
                field = class1.getField(obfName);
            }
            catch (final Exception e)
            {
                try
                {
                    field = class1.getField(deobfName);
                }
                catch (final Exception e1)
                {
                    continue;
                }
            }
            try
            {
                return field.get(target);
            }
            catch (final Exception e)
            {
                throw new RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @" + target.getClass().getSimpleName());
            }
        }

        throw new RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @" + target.getClass().getSimpleName());
    }

}
