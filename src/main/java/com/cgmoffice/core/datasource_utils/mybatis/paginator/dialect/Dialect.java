package com.cgmoffice.core.datasource_utils.mybatis.paginator.dialect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.SimpleTypeRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.Order;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageBounds;

/**
 * Hibernate의 Dialect와 비슷하지만 페이징 섹션 만 능률화
 */
public class Dialect {
    protected TypeHandlerRegistry typeHandlerRegistry;
    protected MappedStatement mappedStatement;
    protected PageBounds pageBounds;
    protected Object parameterObject;
    protected BoundSql boundSql;
    protected List<ParameterMapping> parameterMappings;
    protected Map<String, Object> pageParameters = new HashMap<String, Object>();

    private String pageSQL;
    private String countSQL;


    public Dialect(MappedStatement mappedStatement, Object parameterObject, PageBounds pageBounds){
        this.mappedStatement = mappedStatement;
        this.parameterObject = parameterObject;
        this.pageBounds = pageBounds;
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();

        init();
    }

    @SuppressWarnings("unchecked")
	protected void init(){

        boundSql = mappedStatement.getBoundSql(parameterObject);
        parameterMappings = new ArrayList<ParameterMapping>(boundSql.getParameterMappings());
        if(parameterObject instanceof Map){
            pageParameters.putAll((Map<String, Object>)parameterObject);
        }else if( parameterObject != null){
            Class<? extends Object> cls = parameterObject.getClass();
            if(cls.isPrimitive() || cls.isArray() ||
                    SimpleTypeRegistry.isSimpleType(cls) ||
                    Enum.class.isAssignableFrom(cls) ||
                    Collection.class.isAssignableFrom(cls)){
                for (ParameterMapping parameterMapping : parameterMappings) {
                    pageParameters.put(parameterMapping.getProperty(),parameterObject);
                }
            }else{
                MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);
                ObjectWrapper wrapper = metaObject.getObjectWrapper();
                for (ParameterMapping parameterMapping : parameterMappings) {
                    PropertyTokenizer prop = new PropertyTokenizer(parameterMapping.getProperty());
                    pageParameters.put(parameterMapping.getProperty(),wrapper.get(prop));
                }
            }

        }

        StringBuffer bufferSql = new StringBuffer(boundSql.getSql().trim());
        if(bufferSql.lastIndexOf(";") == bufferSql.length()-1){
            bufferSql.deleteCharAt(bufferSql.length()-1);
        }
        String sql = bufferSql.toString();
        pageSQL = sql;
        if(pageBounds.getOrders() != null && !pageBounds.getOrders().isEmpty()){
            pageSQL = getSortString(sql, pageBounds.getOrders());
        }
        if(pageBounds.getOffset() != RowBounds.NO_ROW_OFFSET
                || pageBounds.getLimit() != RowBounds.NO_ROW_LIMIT){
            pageSQL = getLimitString(pageSQL, "__offset", pageBounds.getOffset(), "__limit",pageBounds.getLimit());
        }


        countSQL = getCountString(sql);
    }


    public List<ParameterMapping> getParameterMappings(){
        return parameterMappings;
    }

    public Object getParameterObject(){
        return pageParameters;
    }


    public String getPageSQL(){
        return pageSQL;
    }

    protected void setPageParameter(String name, Object value, Class<?> type){
        ParameterMapping parameterMapping = new ParameterMapping.Builder(mappedStatement.getConfiguration(), name, type).build();
        parameterMappings.add(parameterMapping);
        pageParameters.put(name, value);
    }


    public String getCountSQL(){
        return countSQL;
    }


    /**
     * sql을 페이지 sql 문으로 변경
     */
    protected String getLimitString(String sql, String offsetName,int offset, String limitName, int limit) {
        throw new UnsupportedOperationException("paged queries not supported");
    }

    /**
     * SQL을 총 레코드 SQL로 변환
     * @param sql SQL문
     * @return 변환된 SQL문
     */
    protected String getCountString(String sql){
        return StringUtils.join("select count(1) from (", sql, ") tmp_count");
    }

    /**
     * SQL을 sort를 사용하는 SQL로 변환
     * @param sql SQL문
     * @return 변환된 SQL문
     */
    protected String getSortString(String sql, List<Order> orders){
        if(orders == null || orders.isEmpty()){
            return sql;
        }

        StringBuffer buffer = new StringBuffer("select * from (");
        buffer.append(sql);
        buffer.append(") temp_order order by ");
        for(Order order : orders){
            if(order != null){
                buffer.append(order.toString());
                buffer.append(", ");
            }

        }
        buffer.delete(buffer.length()-2, buffer.length());
        return buffer.toString();
    }
}
