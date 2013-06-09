package ca.q0r.kfreqs.app.prop;

import java.util.*;

public class LinkedProperties extends Properties {
    private LinkedHashMap<Object, Object> entries = new LinkedHashMap<Object, Object>();

    @Override
    public Enumeration<Object> keys() { return Collections.enumeration(entries.keySet()); }

    @Override
    public Enumeration<Object> elements() { return Collections.enumeration(entries.values()); }

    @Override
    public boolean contains(Object value) { return entries.containsValue(value); }

    @Override
    public void putAll(Map<?, ?> map) {
        entries.putAll(map);
    }

    @Override
    public int size() { return entries.size(); }

    @Override
    public boolean isEmpty() { return entries.isEmpty(); }

    @Override
    public boolean containsKey(Object key) { return entries.containsKey(key); }

    @Override
    public boolean containsValue(Object value) { return entries.containsValue(value); }

    @Override
    public Object get(Object key) { return entries.get(key); }

    @Override
    public Object put(Object key, Object value) { return entries.put(key, value); }

    @Override
    public Object remove(Object key) { return entries.remove(key); }

    @Override
    public void clear() { entries.clear(); }

    @Override
    public Set<Object> keySet() { return entries.keySet(); }

    @Override
    public Collection<Object> values() { return entries.values(); }

    @Override
    public Set<Entry<Object, Object>> entrySet() { return entries.entrySet(); }

    @Override
    public boolean equals(Object o) {
        return o instanceof Entry && entries.equals(o);
    }

    @Override
    public int hashCode() { return entries.hashCode(); }
}
