package com.mysema.query.sql.dml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.mysema.query.QueryException;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.types.Path;

/**
 * Creates the mapping by inspecting the RelationalPath and Object via reflection. 
 * Given bean doesn't need to have @Column metadata, but the fields need to have the same 
 * name as in the given relational path. 
 * 
 * @author tiwe
 *
 */
public class DefaultMapper extends AbstractMapper<Object> {

    public static final DefaultMapper DEFAULT = new DefaultMapper();
    
    @Override
    public Map<Path<?>, Object> createMap(RelationalPath<?> entity, Object bean) {
        try {
            Map<Path<?>, Object> values = new HashMap<Path<?>, Object>();
            Class<?> beanClass = bean.getClass();
            Map<String, Field> fields = getPathFields(entity.getClass());
            for (Field beanField : beanClass.getDeclaredFields()) {
                if (!Modifier.isStatic(beanField.getModifiers())) {
                    Field field = fields.get(beanField.getName());                    
                    @SuppressWarnings("rawtypes")
                    Path path = (Path<?>) field.get(entity);
                    beanField.setAccessible(true);
                    Object propertyValue = beanField.get(bean);
                    if (propertyValue != null) {
                        values.put(path, propertyValue);
                    }     
                }
            }
            return values;    
        } catch (IllegalAccessException e) {
            throw new QueryException(e);
        }
    }
    


}
