package com.cgmoffice.core.datasource_utils.mybatis.paginator.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Properties 조작을위한 툴 클래스는 Properties에 대한 프록시를 제공하여 getRequiredString (), getInt (), getBoolean () 등과 같은 관련 도구 메소드를 추가합니다.
 * systemPropertiesMode 속성을 사용하여 System.getProperty () 및 System.getenv ()를 검색하여 값을 찾을 지 여부를 지정할 수 있습니다.
 * 기본값은 시스템 속성을 검색하지 않습니다.
 * 
 * <pre>
 * 사용1:
 * public class ConnectionUtils {
 *     static Properties properties = new Properties(); 
 *     // ... do load properties 
 *     
 *     // delegate to properties
 * 	   static PropertiesHelper props = new PropertiesHelper(properties);
 *     public static Connection getConnection() {
 *     		// use getRequiredProperty() 
 *     		DriverManager.getConnection(props.getRequiredString("jdbc.url"));
 *     }
 * }
 * 시스템 속성 검색 여부 지정:
 * new PropertiesHelper(properties,PropertiesHelper.SYSTEM_PROPERTIES_MODE_OVERRIDE)
 * </pre>
 */
public class PropertiesHelper {
	/** Never check system properties. */
	public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;

	/**
	 * Check system properties if not resolvable in the specified properties.
	 * This is the default.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;

	/**
	 * Check system properties first, before trying the specified properties.
	 * This allows system properties to override any other property source.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
	
	private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_NEVER;
	private Properties p;

	public PropertiesHelper(Properties p) {
		setProperties(p);
	}
	
	public PropertiesHelper(Properties p, int systemPropertiesMode) {
		setProperties(p);
		if(systemPropertiesMode != SYSTEM_PROPERTIES_MODE_NEVER && systemPropertiesMode != SYSTEM_PROPERTIES_MODE_FALLBACK && systemPropertiesMode != SYSTEM_PROPERTIES_MODE_OVERRIDE) {
			throw new IllegalArgumentException("error systemPropertiesMode mode:"+systemPropertiesMode);
		}
		this.systemPropertiesMode = systemPropertiesMode;
	}
	
	public Properties getProperties() {
		return p;
	}
	
	public void setProperties(Properties props) {
		if(props == null) {
			throw new IllegalArgumentException("properties must be not null");
		}
		this.p = props;
	}
	
	public String getRequiredString(String key) {
		String value = getProperty(key);
		if(isBlankString(value)) {
			throw new IllegalStateException("required property is blank by key="+key);
		}
		return value;
	}
	
	public String getNullIfBlank(String key) {
		String value = getProperty(key);
		if(isBlankString(value)) {
			return null;
		}
		return value;
	}
	
	public String getNullIfEmpty(String key) {
		String value = getProperty(key);
		if(value == null || "".equals(value)) {
			return null;
		}
		return value;
	}
	
	/**
	 * System.getProperty (key) 및 System.getenv (key)에서 값을 가져 오려고합니다.
	 * @return
	 */
	public String getAndTryFromSystem(String key) {
		String value = getProperty(key);
		if(isBlankString(value)) {
			value = getSystemProperty(key);
		}
		return value;
	}

	private String getSystemProperty(String key) {
		String value;
		value = System.getProperty(key);
		if(isBlankString(value)) {
			value = System.getenv(key);
		}
		return value;
	}
	
	public Integer getInteger(String key) {
		String v = getProperty(key);
		if(v == null){
			return null;
		}
		return Integer.parseInt(v);
	}
	
	public int getInt(String key,int defaultValue) {
		if(getProperty(key) == null) {
			return defaultValue;
		}
		return Integer.parseInt(getRequiredString(key));
	}
	
	public int getRequiredInt(String key) {
		return Integer.parseInt(getRequiredString(key));
	}
	
	public Long getLong(String key) {
		if(getProperty(key) == null) {
			return null;
		}
		return Long.parseLong(getRequiredString(key));
	}
	
	public long getLong(String key,long defaultValue) {
		if(getProperty(key) == null) {
			return defaultValue;
		}
		return Long.parseLong(getRequiredString(key));
	}
	
	public Long getRequiredLong(String key) {
		return Long.parseLong(getRequiredString(key));
	}
	
	public Boolean getBoolean(String key) {
		if(getProperty(key) == null) {
			return null;
		}
		return Boolean.parseBoolean(getRequiredString(key));
	}
	
	public boolean getBoolean(String key,boolean defaultValue) {
		if(getProperty(key) == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(getRequiredString(key));
	}
	
	public boolean getRequiredBoolean(String key) {
		return Boolean.parseBoolean(getRequiredString(key));
	}
	
	public Float getFloat(String key) {
		if(getProperty(key) == null) {
			return null;
		}
		return Float.parseFloat(getRequiredString(key));
	}
	
	public float getFloat(String key,float defaultValue) {
		if(getProperty(key) == null) {
			return defaultValue;
		}
		return Float.parseFloat(getRequiredString(key));
	}
	
	public Float getRequiredFloat(String key) {
		return Float.parseFloat(getRequiredString(key));
	}
	
	public Double getDouble(String key) {
		if(getProperty(key) == null) {
			return null;
		}
		return Double.parseDouble(getRequiredString(key));
	}
	
	public double getDouble(String key,double defaultValue) {
		if(getProperty(key) == null) {
			return defaultValue;
		}
		return Double.parseDouble(getRequiredString(key));
	}
	
	public Double getRequiredDouble(String key) {
		return Double.parseDouble(getRequiredString(key));
	}
	
	/** setProperty(String key,int value) ... start */
	
	public Object setProperty(String key,int value) {
		return setProperty(key, String.valueOf(value));
	}
	
	public Object setProperty(String key,long value) {
		return setProperty(key, String.valueOf(value));
	}
	
	public Object setProperty(String key,float value) {
		return setProperty(key, String.valueOf(value));
	}
	
	public Object setProperty(String key,double value) {
		return setProperty(key, String.valueOf(value));
	}
	
	public Object setProperty(String key,boolean value) {
		return setProperty(key, String.valueOf(value));
	}

	public String[] getStringArray(String key) {
		String v = getProperty(key);
		if(v == null) {
			return new String[0];
		}else {
		    return tokenizeToStringArray(v, ", \t\n\r\f");
		}
	}

	public int[] getIntArray(String key) {
		return toIntArray(getStringArray(key));
	}

	public Properties getStartsWithProperties(String prefix) {
		if(prefix == null) {
			throw new IllegalArgumentException("'prefix' must be not null");
		}
		
		Properties props = getProperties();
		Properties result = new Properties();
		for(Entry<Object, Object> entry : props.entrySet()) {
			String key = (String)entry.getKey();
			if(key != null && key.startsWith(prefix)) {
				result.put(key.substring(prefix.length()), entry.getValue());
			}
		}
		return result;
	}
	
	/** delegate method start */
	
	public String getProperty(String key, String defaultValue) {
	    String value = getProperty(key);
	    if(isBlankString(value)) {
	        return defaultValue;
	    }
		return value;
	}

	public String getProperty(String key) {
		String propVal = null;
		if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_OVERRIDE) {
			propVal = getSystemProperty(key);
		}
		if (propVal == null) {
			propVal = p.getProperty(key);
		}
		if (propVal == null && systemPropertiesMode == SYSTEM_PROPERTIES_MODE_FALLBACK) {
			propVal = getSystemProperty(key);
		}
		return propVal;
	}

	public Object setProperty(String key,String value) {
		return p.setProperty(key, value);
	}

	public void clear() {
		p.clear();
	}

	public Set<Entry<Object, Object>> entrySet() {
		return p.entrySet();
	}

	public Enumeration<?> propertyNames() {
		return p.propertyNames();
	}

	public boolean contains(Object value) {
		return p.contains(value);
	}

	public boolean containsKey(Object key) {
		return p.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return p.containsValue(value);
	}

	public Enumeration<Object> elements() {
		return p.elements();
	}

	public Object get(Object key) {
		return p.get(key);
	}

	public boolean isEmpty() {
		return p.isEmpty();
	}

	public Enumeration<Object> keys() {
		return p.keys();
	}

	public Set<Object> keySet() {
		return p.keySet();
	}

	public void list(PrintStream out) {
		p.list(out);
	}

	public void list(PrintWriter out) {
		p.list(out);
	}

	public void load(InputStream inStream) throws IOException {
		p.load(inStream);
	}

	public void loadFromXML(InputStream in) throws IOException,
			InvalidPropertiesFormatException {
		p.loadFromXML(in);
	}

	public Object put(Object key, Object value) {
		return p.put(key, value);
	}

	public void putAll(Map<? extends Object, ? extends Object> t) {
		p.putAll(t);
	}

	public Object remove(Object key) {
		return p.remove(key);
	}

	/** @deprecated */
	public void save(OutputStream out, String comments) {
		p.save(out, comments);
	}

	public int size() {
		return p.size();
	}

	public void store(OutputStream out, String comments) throws IOException {
		p.store(out, comments);
	}

	public void storeToXML(OutputStream os, String comment, String encoding)
			throws IOException {
		p.storeToXML(os, comment, encoding);
	}

	public void storeToXML(OutputStream os, String comment) throws IOException {
		p.storeToXML(os, comment);
	}

	public Collection<Object> values() {
		return p.values();
	}
	
	public String toString() {
		return p.toString();
	}

	private static boolean isBlankString(String value) {
		return value == null || "".equals(value.trim());
	}
	
	private static String[] tokenizeToStringArray(String str,String seperators) {
		StringTokenizer tokenlizer = new StringTokenizer(str,seperators);
		List<Object> result = new ArrayList<Object>();
		
		while(tokenlizer.hasMoreElements()) {
			Object s = tokenlizer.nextElement();
			result.add(s);
		}
		return (String[])result.toArray(new String[result.size()]);
	}
	
	private static int[] toIntArray(String[] array) {
		int[] result = new int[array.length];
		for(int i = 0; i < array.length; i++) {
			result[i] = Integer.parseInt(array[i]);
		}
		return result;
	}
}
