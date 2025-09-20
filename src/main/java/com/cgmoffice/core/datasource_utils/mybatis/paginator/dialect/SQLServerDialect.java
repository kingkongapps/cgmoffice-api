package com.cgmoffice.core.datasource_utils.mybatis.paginator.dialect;

import java.util.Locale;

import org.apache.ibatis.mapping.MappedStatement;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageBounds;

public class SQLServerDialect extends Dialect{

    public SQLServerDialect(MappedStatement mappedStatement, Object parameterObject, PageBounds pageBounds) {
        super(mappedStatement, parameterObject, pageBounds);
    }

	
	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase(Locale.KOREA).indexOf( "select" );
		int selectDistinctIndex = sql.toLowerCase(Locale.KOREA).indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}


    protected String getLimitString(String sql, String offsetName,int offset, String limitName, int limit) {
		if ( offset > 0 ) {
			throw new UnsupportedOperationException( "sql server has no offset" );
		}
//		if(limitPlaceholder != null) {
//			throw new UnsupportedOperationException(" sql server not support variable limit");
//		}
        setPageParameter(limitName, limit, Integer.class);
        StringBuffer sb = new StringBuffer( sql.length() + 8 );
        sb.append( sql );
        sb.insert( getAfterSelectInsertPoint( sql ), " top " + limit );
		return sb.toString();
	}
	
	// TODO add Dialect.supportsVariableLimit() for sqlserver 
//	public boolean supportsVariableLimit() {
//		return false;
//	}

}
