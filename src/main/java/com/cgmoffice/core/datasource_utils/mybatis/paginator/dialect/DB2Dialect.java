package com.cgmoffice.core.datasource_utils.mybatis.paginator.dialect;

import java.util.Locale;

import org.apache.ibatis.mapping.MappedStatement;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageBounds;

public class DB2Dialect extends Dialect{

    public DB2Dialect(MappedStatement mappedStatement, Object parameterObject, PageBounds pageBounds) {
        super(mappedStatement, parameterObject, pageBounds);
    }
	
	private static String getRowNumber(String sql) {
		StringBuffer rownumber = new StringBuffer(50);
		rownumber.append("rownumber() over(");

		int orderByIndex = sql.toLowerCase(Locale.KOREA).indexOf("order by");

		if ( orderByIndex>0 && !hasDistinct(sql) ) {
			rownumber.append( sql.substring(orderByIndex) );
		}

		rownumber.append(") as rownumber_,");

		return rownumber.toString();
	}
	
	private static boolean hasDistinct(String sql) {
		return sql.toLowerCase(Locale.KOREA).indexOf("select distinct")>=0;
	}

    protected String getLimitString(String sql, String offsetName,int offset, String limitName, int limit) {
		int startOfSelect = sql.toLowerCase(Locale.KOREA).indexOf("select");

		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
		pagingSelect.append( sql.substring(0, startOfSelect) ); //add the comment
		pagingSelect.append("select * from ( select "); //nest the main query in an outer select
		pagingSelect.append( getRowNumber(sql) ); //add the rownnumber bit into the outer query select list

		if ( hasDistinct(sql) ) {
			pagingSelect.append(" row_.* from ( "); //add another (inner) nested select
			pagingSelect.append( sql.substring(startOfSelect) ); //add the main query
			pagingSelect.append(" ) as row_"); //close off the inner nested select
		}
		else {
			pagingSelect.append( sql.substring( startOfSelect + 6 ) ); //add the main query
		}

		pagingSelect.append(" ) as temp_ where rownumber_ ");

		//add the restriction to the outer select
		if (offset > 0) {
			pagingSelect.append("between ?+1 and ?");
            setPageParameter(offsetName,offset,Integer.class);
            setPageParameter("__offsetEnd",offset+limit,Integer.class);
		}
		else {
			pagingSelect.append("<= ?");
            setPageParameter(limitName,limit,Integer.class);
		}

		return pagingSelect.toString();
	}
}
