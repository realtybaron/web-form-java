package com.socotech.wf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ReflectTest extends TestCase {

    public void testPrimitiveProperty() throws Exception {
        Container container = new Container();
        Field field = Reflect.getDeclaredField(container, "primitive");
        assertNotNull(field);
        assertTrue(field.getType().isPrimitive());
    }

    public void testArrayProperty() throws Exception {
        Container container = new Container();
        Field field = Reflect.getDeclaredField(container, "array");
        assertNotNull(field);
        assertTrue(field.getType().isArray());
        assertEquals(String.class, field.getType().getComponentType());
    }

    public void testNestedProperty() throws Exception {
        Container container = new Container();
        Field field = Reflect.getDeclaredField(container, "component.simple");
        assertEquals("simple", field.getName());
    }

    public void testSimpleIndexedProperty() throws Exception {
        Container container = new Container();
        Field field = Reflect.getDeclaredField(container, "list[0]");
        assertEquals("list", field.getName());
    }

    public void testNestedIndexedProperty() throws Exception {
        Container container = new Container();
        Field field = Reflect.getDeclaredField(container, "list[0].indexed");
        assertEquals("indexed", field.getName());
    }

    public void testMappedProperty() throws Exception {
        Container container = new Container();
        Field field = Reflect.getDeclaredField(container, "map(component).mapped");
        assertEquals("mapped", field.getName());
    }

    public void testPrimitiveSubProperty() throws Exception {
        SubContainer container = new SubContainer();
        Field field = Reflect.getDeclaredField(container, "primitive");
        assertNotNull(field);
        assertTrue(field.getType().isPrimitive());
    }

    public class Container {
        protected int primitive = 0;
        protected String[] array = new String[0];
        protected Component component = new Component();
        protected List<Component> list = new ArrayList<Component>();
        protected Map<String, Component> map = new HashMap<String, Component>();

        public Container() {
            this.list.add(new Component());
            this.map.put("component", new Component());
        }

        public int getPrimitive() {
            return primitive;
        }

        public void setPrimitive(int primitive) {
            this.primitive = primitive;
        }

        public String[] getArray() {
            return array;
        }

        public void setArray(String[] array) {
            this.array = array;
        }

        public Component getComponent() {
            return component;
        }

        public void setComponent(Component component) {
            this.component = component;
        }

        public List<Component> getList() {
            return list;
        }

        public void setList(List<Component> list) {
            this.list = list;
        }

        public Map<String, Component> getMap() {
            return map;
        }

        public void setMap(Map<String, Component> map) {
            this.map = map;
        }
    }

    public class Component {
        private Object simple = new Object();
        private Object nested = new Object();
        private Object indexed = new Object();
        private Object mapped = new Object();


        public Object getSimple() {
            return simple;
        }

        public void setSimple(Object o) {
            this.simple = o;
        }

        public Object getNested() {
            return nested;
        }

        public void setNested(Object o) {
            this.nested = o;
        }

        public Object getIndexed() {
            return indexed;
        }

        public void setIndexed(Object o) {
            this.indexed = o;
        }

        public Object getMapped() {
            return mapped;
        }

        public void setMapped(Object o) {
            this.mapped = o;
        }
    }

    public class SubContainer extends Container {
        // noop
    }

    /**
     * Run these from the command line
     *
     * @param args common-line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ReflectTest.class);
    }
}
