package com.cedarsoftware.util;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author John DeRegnaucourt (jdereg@gmail.com)
 *         <br/>
 *         Copyright (c) Cedar Software LLC
 *         <br/><br/>
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br/><br/>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <br/><br/>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public class TestReflectionUtils
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface ControllerClass
    {
    }

    @ControllerClass
    static class Foo
    {
    }

    static class Bar extends Foo
    {
    }

    @ControllerClass
    static interface Baz
    {
    }

    static interface Qux extends Baz
    {
    }

    static class Beta implements Qux
    {
    }

    static class Alpha extends Beta
    {
    }

    static interface Blart
    {
    }

    static class Bogus implements Blart
    {
    }

    @Test
    public void testClassAnnotation()
    {
        Annotation a = ReflectionUtils.getClassAnnotation(Bar.class, ControllerClass.class);
        assertNotNull(a);
        assertTrue(a instanceof ControllerClass);

        a = ReflectionUtils.getClassAnnotation(Alpha.class, ControllerClass.class);
        assertNotNull(a);
        assertTrue(a instanceof ControllerClass);

        a = ReflectionUtils.getClassAnnotation(Bogus.class, ControllerClass.class);
        assertNull(a);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ControllerMethod
    {
        String allow();
    }

    static class Foo1
    {
        @ControllerMethod(allow = "false")
        public void yelp()
        {
        }
    }

    static class Bar1 extends Foo1
    {
    }

    static interface Baz1
    {
        @ControllerMethod(allow = "false")
        void yelp();
    }

    static interface Qux1 extends Baz1
    {
    }

    static class Beta1 implements Qux1
    {
        public void yelp()
        {
        }
    }

    static class Alpha1 extends Beta1
    {
    }

    static interface Blart1
    {
        void yelp();
    }

    static class Bogus1 implements Blart1
    {
        public void yelp()
        {
        }
    }

    @Test
    public void testMethodAnnotation() throws Exception
    {
        Method m = ReflectionUtils.getMethod(Bar1.class, "yelp");
        Annotation a = ReflectionUtils.getMethodAnnotation(m, ControllerMethod.class);
        assertNotNull(a);
        assertTrue(a instanceof ControllerMethod);
        assertEquals("false", ((ControllerMethod) a).allow());

        m = ReflectionUtils.getMethod(Alpha1.class, "yelp");
        a = ReflectionUtils.getMethodAnnotation(m, ControllerMethod.class);
        assertNotNull(a);
        assertTrue(a instanceof ControllerMethod);

        m = ReflectionUtils.getMethod(Bogus1.class, "yelp");
        a = ReflectionUtils.getMethodAnnotation(m, ControllerMethod.class);
        assertNull(a);
    }

    @Test
    public void testDeepDeclaredFields() throws Exception
    {
        Calendar c = Calendar.getInstance();
        Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(c.getClass());
        assertTrue(fields.size() > 0);

        boolean miss = true;
        boolean found = false;
        for (Field field : fields)
        {
            if ("firstDayOfWeek".equals(field.getName()))
            {
                found = true;
                break;
            }

            if ("blart".equals(field.getName()))
            {
                miss = false;
            }
        }

        assertTrue(found);
        assertTrue(miss);
    }

    @Test
    public void testDeepDeclaredFieldMap() throws Exception
    {
        Calendar c = Calendar.getInstance();
        Map<String, Field> fields = ReflectionUtils.getDeepDeclaredFieldMap(c.getClass());
        assertTrue(fields.size() > 0);
        assertTrue(fields.containsKey("firstDayOfWeek"));
        assertFalse(fields.containsKey("blart"));
    }
}
