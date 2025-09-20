package com.cgmoffice.core.datasource_utils.mybatis.paginator.domain;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * "페이징"정보가 포함 된 목록
 * 
 * <p>총 페이지 수를 얻으려면 다음을 사용하십시오. toPaginator().getTotalPages();</p>
 * 
 */
public class PageList<E> extends ArrayList<E> {
    private static final long serialVersionUID = 1412759446332294208L;
    
    private Paginator paginator;
    
	public PageList(Collection<? extends E> c) {
		super(c);
	}

	
	public PageList(Collection<? extends E> c,Paginator p) {
        super(c);
        this.paginator = p;
    }

    public PageList(Paginator p) {
        this.paginator = p;
    }

	public Paginator getPaginator() {
		return paginator;
	}

	
}
