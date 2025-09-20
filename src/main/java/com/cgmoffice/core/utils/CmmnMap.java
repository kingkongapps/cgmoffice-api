package com.cgmoffice.core.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CmmnMap extends HashMap<Object, Object>{

	private static final long serialVersionUID = -4485149791269586840L;

	@Override
	public CmmnMap put(Object key, Object value) {
		super.put(key, value);
		return this;
	}

	@Override
	public Object get(Object key) {
		return super.get(key);
	}

	public String getString(Object key) {
		Object val = get(key);
		if("Integer".equals(whatIs(val))) {
			return Integer.toString((int)val);
		} else {
			return (String)val;
		}
	}

	public String getString(Object key, String deflt) {
		Object val = get(key);
		if("Integer".equals(whatIs(val))) {
			return Integer.toString((int)val);
		} else if("String".equals(whatIs(val))) {
			return (String)val;
		}
		return val == null ? deflt : (String)val;
	}

	public Boolean getBoolean(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return Boolean.parseBoolean(value.toString().trim());
		}
	}

	public Boolean getBoolean(Object key, boolean deflt) {
		Object value = get(key);
		if (value == null) {
			return deflt;
		} else if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return Boolean.parseBoolean(value.toString().trim());
		}
	}

	public Integer getInt(Object key) {
		return getInteger(key);
	}

	public Integer getInt(Object key, int deflt) {
		return getInteger(key, deflt);
	}

	public Integer getInteger(Object key, int deflt) {
		Object value = get(key);
		if (value == null) {
			return deflt;
		} else if (value instanceof Integer) {
			return (Integer) value;
		} else if (value.toString().trim().equals("")) {
			return deflt;
		} else {
			return Integer.valueOf(value.toString().trim());
		}
	}

	public Integer getInteger(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Integer) {
			return (Integer) value;
		} else {
			return Integer.valueOf(value.toString().trim());
		}
	}

	public Long getLong(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Long) {
			return (Long) value;
		} else {
			return Long.valueOf(value.toString().trim());
		}
	}

	public Float getFloat(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Float) {
			return (Float) value;
		} else {
			return Float.valueOf(value.toString().trim());
		}
	}

	public Double getDouble(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Double) {
			return (Double) value;
		} else {
			return Double.valueOf(value.toString().trim());
		}
	}

	public BigDecimal getBigDecimal(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		} else {
			return new BigDecimal(value.toString().trim());
		}
	}

	public CmmnMap getCmmnMap(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return JsonUtils.fromJsonStr(CmmnMap.class, (String) value);
		} else if (value instanceof Map) {
			return CoreUtils.cast(CmmnMap.class, value);
		} else {
			return null;
		}
	}

	public CmmnMap getCmmnMap(Object key, CmmnMap deflt) {
		Object value = get(key);
		if (value == null) {
			return deflt;
		} else if (value instanceof String) {
			return JsonUtils.fromJsonStr(CmmnMap.class, (String) value);
		} else if (value instanceof Map) {
			return CoreUtils.cast(CmmnMap.class, value);
		} else {
			return deflt;
		}
	}

	public List<CmmnMap> getCmmnMapList(Object key) {
		Object value = get(key);
		if (value == null) {
			return new ArrayList<>();
		} else if(value instanceof String) {
			List<CmmnMap> rslt = new ArrayList<>();
			JsonUtils.fromJsonStr(List.class, value.toString()).forEach(a -> rslt.add(CoreUtils.cast(CmmnMap.class, a)));
			return rslt;
		} else if (value instanceof List) {
			List<CmmnMap> rslt = new ArrayList<>();
			((List) value).forEach(a -> rslt.add(CoreUtils.cast(CmmnMap.class, a)));
			return rslt;
		} else {
			return null;
		}
	}

	public List<CmmnMap> getCmmnMapList(Object key, ArrayList<CmmnMap> deflt) {
		Object value = get(key);
		if (value == null) {
			return deflt;
		} else if(value instanceof String) {
			List<CmmnMap> rslt = new ArrayList<>();
			JsonUtils.fromJsonStr(List.class, value.toString()).forEach(a -> rslt.add(CoreUtils.cast(CmmnMap.class, a)));
			return rslt;
		} else if (value instanceof List) {
			List<CmmnMap> rslt = new ArrayList<>();
			((List) value).forEach(a -> rslt.add(CoreUtils.cast(CmmnMap.class, a)));
			return rslt;
		} else {
			return deflt;
		}
	}

	public Date getDate(Object key) {
		return getDateFormat(key, "yyyy-MM-dd");
	}

	public Date getDateTime(Object key) {
		return getDateFormat(key, "yyyy-MM-dd HH:mm:ss");
	}

	public Date getDateFormat(Object key, String format) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof java.sql.Date) {
			java.sql.Date sqlDate = (java.sql.Date) value;
			return new Date(sqlDate.getTime());
		} else if (value instanceof Date) {
			return (Date) value;
		} else {
			String str = value.toString().trim();
			SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);
			sdf.setLenient(false);
			try {
				return sdf.parse(str);
			} catch (ParseException e) {
				return null;
			}
		}
	}

	public Timestamp getTimestamp(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof java.sql.Date) {
			java.sql.Date sqlDate = (java.sql.Date) value;
			return new Timestamp(sqlDate.getTime());
		} else if (value instanceof Timestamp) {
			return (Timestamp) value;
		} else if (value instanceof Date) {
			Date date = (Date) value;
			return new Timestamp(date.getTime());
		} else {
			return Timestamp.valueOf(value.toString());
		}
	}

	@Override
	public String toString() {
//		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		return JsonUtils.toJsonStr(this);
	}

	private String whatIs(Object obj) {
	    if (obj instanceof Integer) {
	        return "Integer";
	    } else if(obj instanceof String) {
	    	return "String";
	    } else if(obj instanceof Float) {
	    	return "Float";
	    }
	    return "non";
	}


	public <T> List<T> getList(Object key) {

		Object value = get(key);
		if (value instanceof List) {
			return (List<T>) value;
		} else if (value instanceof String) {
			return JsonUtils.fromJsonStr(List.class, (String)value);
		} else {
			return null;
		}
	}
}
