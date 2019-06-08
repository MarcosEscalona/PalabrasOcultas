package edu.uclm.esi.MarcosEscalona.PalabrasOcultas.JSONer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONer {

	public static JSONObject toJSON(Object object) throws Exception {
		Class<?> clase=object.getClass();
		Vector<Field> primitiveFields=new Vector<>();
		Vector<Field> collectionFields=new Vector<>();
		Vector<Field> complexFields=new Vector<>();
		Vector<Method> jsonableMethods=new Vector<>();
		
		loadJSONables(primitiveFields, collectionFields, complexFields, jsonableMethods, clase);
		
		if (primitiveFields.size() + collectionFields.size() + complexFields.size() + jsonableMethods.size()==0)
			return null;
		
		Hashtable<Integer, Object> hashes=new Hashtable<>();
		return toJSON(object, primitiveFields, collectionFields, complexFields, jsonableMethods, hashes);
	}
	
	private static JSONObject toJSON(Object object, Vector<Field> primitiveFields, Vector<Field> collectionFields,
			Vector<Field> complexFields, Vector<Method> jsonableMethods, Hashtable<Integer, Object> hashes) throws Exception {
		JSONObject jso=new JSONObject();
		Field field;
		String fieldName;
		Object value;
		for (int i=0; i<primitiveFields.size(); i++) {
			field=primitiveFields.get(i);
			fieldName=field.getName();
			value=field.get(object);
			jso.put(fieldName, getPrimitiveValue(value));
		}
		for (int i=0; i<collectionFields.size(); i++) {
			field=collectionFields.get(i);
			fieldName=field.getName();
			value=field.get(object);
			if (value==null)
				continue;
			if (hashes.containsKey(value.hashCode()))
				continue;
			hashes.put(value.hashCode(), value);
			jso.put(fieldName, getCollectionValue(value, hashes));
		}
		for (int i=0; i<complexFields.size(); i++) {
			field=complexFields.get(i);
			fieldName=field.getName();
			value=field.get(object);
			if (value==null)
				continue;
			if (hashes.containsKey(value.hashCode()))
				continue;
			hashes.put(value.hashCode(), value);
			jso.put(fieldName, getComplexValue(value, hashes));
		}
		Method method;
		String methodName;
		Character first;
		for (int i=0; i<jsonableMethods.size(); i++) {
			method=jsonableMethods.get(i);
			methodName=method.getName();
			first=methodName.charAt(3);
			fieldName=Character.toLowerCase(first) + methodName.substring(4);
			jso.put(fieldName, method.invoke(object));
		}
		return jso;
	}
	
	private static Object getPrimitiveValue(Object value) throws Exception {
		if (value==null)
			return null;
		Class<?> type = value.getClass();
		if (type==char.class || type==Character.class) 
			return "" + value;
		if (type==int.class || type==Integer.class)
			return new Integer((int) value);
		if (type==long.class || type==Long.class) 
			return new Long((long) value);
		if (type==float.class || type==Float.class) 
			return new Float((float) value);
		if (type==double.class || type==Double.class) 
			return new Double((double) value);
		if (type==byte.class || type==Byte.class) 
			return new Byte((byte) value);
		if (type==boolean.class || type==Boolean.class) 
			return new Boolean((boolean) value);
		if (type==String.class) 
			return value.toString();
		return value.toString();
	}	
	
	private static Object getCollectionValue(Object value, Hashtable<Integer, Object> hashes) throws Exception {
		if (value==null)
			return null;
		Class<?> type = value.getClass();
		if (type.isArray()) {
			Object[] values=(Object[]) value;
			JSONArray jsa=getValuesFromArray(hashes, values);
			return jsa;
		}
		if (Collection.class.isAssignableFrom(type)) {
			Collection<?> values=(Collection<?>) value;
			JSONArray jsa=getValuesFromCollection(hashes, values);
			return jsa;
		}
		if (Dictionary.class.isAssignableFrom(type)) {
			Dictionary<?, ?> values=(Dictionary<?, ?>) value;
			JSONArray jsa=getValuesFromEnumeration(hashes, values);
			return jsa;
		}
		if (AbstractMap.class.isAssignableFrom(type)) {
			Collection<?> values=((AbstractMap<?, ?>) value).values();
			JSONArray jsa=getValuesFromCollection(hashes, values);
			return jsa;
		}
		return null;	
	}
	
	private static Object getComplexValue(Object value, Hashtable<Integer, Object> hashes) throws Exception {
		Class<?> clase=value.getClass();
		Vector<Field> primitiveFields=new Vector<>();
		Vector<Field> collectionFields=new Vector<>();
		Vector<Field> complexFields=new Vector<>();
		Vector<Method> jsonableMethods=new Vector<>();
		
		loadJSONables(primitiveFields, collectionFields, complexFields, jsonableMethods, clase);
		
		if (primitiveFields.size() + collectionFields.size() + complexFields.size() + jsonableMethods.size()==0)
			return null;
		return toJSON(value, primitiveFields, collectionFields, complexFields, jsonableMethods, hashes);
	}
	
	private static Object getValue(Object value, Hashtable<Integer, Object> hashes) throws Exception {
		if (value==null)
			return null;
		Class<?> clase=value.getClass();
		if (isPrimitive(clase))
			return getPrimitiveValue(value);
		if (isCollection(clase))
			return getCollectionValue(value, hashes);
		return getComplexValue(value, hashes);
	}
	
	private static JSONArray getValuesFromArray(Hashtable<Integer, Object> hashes, Object[] values) throws Exception {
		JSONArray jsa=new JSONArray();
		for (int i=0; i<values.length; i++) {
			Object thisValue=getValue(values[i],  hashes);
			jsa.put(thisValue);
		}
		return jsa;
	}

	private static JSONArray getValuesFromEnumeration(Hashtable<Integer, Object> hashes, Dictionary<?, ?> values) throws Exception {
		JSONArray jsa=new JSONArray();
		Enumeration<?> eValues = values.elements();
		while (eValues.hasMoreElements()) {
			Object eValue = eValues.nextElement();
			Object thisValue=getValue(eValue, hashes);
			jsa.put(thisValue);
		}
		return jsa;
	}

	private static JSONArray getValuesFromCollection(Hashtable<Integer, Object> hashes, Collection<?> values) throws Exception {
		JSONArray jsa=new JSONArray();
		Iterator<?> iValues = values.iterator();
		while (iValues.hasNext()) {
			Object iValue = iValues.next();
			Object thisValue=getValue(iValue, hashes);
			jsa.put(thisValue);
		}
		return jsa;
	}
	
	private static boolean isPrimitive(Class<?> type) {
		return (type.isPrimitive() || Number.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type) || 
				Boolean.class.isAssignableFrom(type) || String.class.isAssignableFrom(type) ||
				UUID.class.isAssignableFrom(type) || ObjectId.class.isAssignableFrom(type));
	}
	
	private static boolean isCollection(Class<?> type) {
		if (type.isArray())
			return true;
		Class<?>[] collections = { Collection.class, Dictionary.class, AbstractMap.class };
		for (int i=0; i<collections.length; i++)
			if (collections[i].isAssignableFrom(type))
				return true;
		return false;
	}
	
	private static void loadJSONables(Vector<Field> primitiveFields, Vector<Field> collectionFields, Vector<Field> complexFields, Vector<Method> jsonableMethods, Class<?> clase) {
		Field[] ff=clase.getDeclaredFields();
		for (int i=0; i<ff.length; i++) {
			ff[i].setAccessible(true);
			if (Modifier.isStatic(ff[i].getModifiers()))
				continue;
			if (ff[i].isAnnotationPresent(JSONExclude.class))
				continue;
			if (isPrimitive(ff[i].getType()))
				primitiveFields.add(ff[i]);
			else if (isCollection(ff[i].getType()))
				collectionFields.add(ff[i]);
			else
				complexFields.addElement(ff[i]);
		}
		Method[] mm=clase.getDeclaredMethods();
		for (int i=0; i<mm.length; i++) {
			mm[i].setAccessible(true);
			if (!mm[i].isAnnotationPresent(ToJSON.class))
				continue;
			if (mm[i].getParameterTypes().length>0)
				continue;
			if (!mm[i].getName().startsWith("get"))
				continue;
			if (mm[i].getName().length()<=3)
				continue;
			jsonableMethods.add(mm[i]);
		}
		if (clase.getSuperclass()!=Object.class)
			loadJSONables(primitiveFields, collectionFields, complexFields, jsonableMethods, clase.getSuperclass());
	}
}
