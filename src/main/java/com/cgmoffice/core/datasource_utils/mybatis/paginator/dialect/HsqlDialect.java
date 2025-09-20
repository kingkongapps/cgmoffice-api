package com.cgmoffice.core.datasource_utils.mybatis.paginator.dialect;

import org.apache.ibatis.mapping.MappedStatement;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageBounds;

public class HsqlDialect extends Dialect {

	public HsqlDialect(MappedStatement mappedStatement, Object parameterObject, PageBounds pageBounds) {
		super(mappedStatement, parameterObject, pageBounds);
		// TODO Auto-generated constructor stub
	}
    
	protected String getLimitString(String sql, String offsetName,int offset, String limitName, int limit) {
        StringBuffer buffer = new StringBuffer( sql.length()+20 );
        buffer.append(sql);
        if (offset > 0) {
            buffer.append(" limit ?, ?");
            setPageParameter(offsetName, offset, Integer.class);
            setPageParameter(limitName, limit, Integer.class);
        } else {
            buffer.append(" limit ?");
            setPageParameter(limitName, limit, Integer.class);
        }
        return buffer.toString();
	}   
}
