package com.cgmoffice.core.datasource_utils.mybatis.paginator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig.PagingConfigOrder;
import com.cgmoffice.core.exception.CmmnBizException;

/**
 *  페이징 쿼리 개체
 */
public class PageBounds extends RowBounds implements Serializable {
    private static final long serialVersionUID = -6414350656252331011L;

    public final static int DEFAULT_ROW_LIMIT = 50;

    public final static int NO_PAGE = 1;
    /** 페이지 번호 */
    protected int page = NO_PAGE;
    /** 페이징 크기 */
    protected int limit;
    /** 페이지 정렬 정보 */
    protected List<Order> orders = new ArrayList<Order>();
    /** 결과 집합에 TotalCount가 포함되는지 여부 */
    protected boolean containsTotalCount;

    protected Boolean asyncTotalCount;

    private static final int DEFAULT_SLIDERS_COUNT = 10;

    protected int sliderCount = -1;

    public PageBounds(){
        containsTotalCount = false;
    }

    public PageBounds(RowBounds rowBounds) {
        if(rowBounds instanceof PageBounds){
            PageBounds pageBounds = (PageBounds)rowBounds;
            this.page = pageBounds.page;
            this.limit = pageBounds.limit;
            this.orders = pageBounds.orders;
            this.containsTotalCount = pageBounds.containsTotalCount;
            this.asyncTotalCount = pageBounds.asyncTotalCount;
            this.sliderCount = pageBounds.sliderCount;
        }else{
            this.page = (rowBounds.getOffset()/rowBounds.getLimit())+1;
            this.limit = rowBounds.getLimit();
        }
    }

    /**
     * Query TOP N, default containsTotalCount = false
     * @param limit
     */
    public PageBounds(int page) {
        this(page, DEFAULT_ROW_LIMIT);
    }

    public PageBounds(int page, int limit) {
        this(page, limit, new ArrayList<Order>(), true);
    }

    public PageBounds(int page, String limit) {
        this(page, Integer.parseInt(limit), new ArrayList<Order>(), true);
    }

    public PageBounds(int page, int limit, boolean containsTotalCount) {
        this(page, limit, new ArrayList<Order>(), containsTotalCount);
    }

    /**
     * Just sorting, default containsTotalCount = false
     * @param orders
     */
    public PageBounds(List<Order> orders) {
        this(NO_PAGE, NO_ROW_LIMIT,orders ,false);
    }

    /**
     * Just sorting, default containsTotalCount = false
     * @param order
     */
    public PageBounds(Order... order) {
        this(NO_PAGE, NO_ROW_LIMIT,order);
        this.containsTotalCount = false;
    }

    public PageBounds(int page, int limit, Order... order) {
        this(page, limit, Arrays.asList(order), true);
    }

    public PageBounds(int page, Order... order) {
        this(page, Arrays.asList(order), true);
    }

    public PageBounds(PageConfig pagingConfig) {

    	Integer pageNo = Integer.parseInt(StringUtils.defaultIfEmpty(pagingConfig.getPage(), "-1"));
		if(pageNo == -1) {
			throw new CmmnBizException("페이지번호가 존재하지 않습니다.");
		}

		List<PagingConfigOrder> orders = pagingConfig.getOrders();
		if(orders == null) {
			throw new CmmnBizException("페이지 정렬기준이 존재하지 않습니다.");
		}

		List<Order> orderlist = new ArrayList<Order>();
		for(PagingConfigOrder order : orders) {
			String target = order.getTarget();
			if(StringUtils.isNotEmpty(target.trim())) {
				orderlist.add(Order.create(order.getTarget(), order.getIsAsc() ? "ASC" : "DESC"));
			}
		}

		Integer limit = Integer.parseInt(StringUtils.defaultIfEmpty(pagingConfig.getLimit(), "-1"));
		if(limit == -1) {
			limit = DEFAULT_ROW_LIMIT;
		}

		int tmpSliderCount = pagingConfig.getSliderCount();
		if(tmpSliderCount != -1) {
			this.sliderCount = tmpSliderCount;
		}

        this.page = pageNo;
        this.orders = orderlist;
        this.containsTotalCount = true;
        this.limit = limit;
    }

    public PageBounds(int page, int limit, List<Order> orders) {
        this(page, limit, orders, true);
    }

    public PageBounds(int page, List<Order> orders, boolean containsTotalCount) {
        this.page = page;
        this.orders = orders;
        this.containsTotalCount = containsTotalCount;
        this.limit = DEFAULT_ROW_LIMIT;
    }

    public PageBounds(int page, int limit, List<Order> orders, boolean containsTotalCount) {
        this.page = page;
        this.limit = limit;
        this.orders = orders;
        this.containsTotalCount = containsTotalCount;
    }

    public void setSliderCount(int sliderCount) {
    	this.sliderCount = sliderCount;
    }

    public int getSliderCount() {
    	if(this.sliderCount == -1) {
    		return DEFAULT_SLIDERS_COUNT;
    	} else {
    		return this.sliderCount;
    	}
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isContainsTotalCount() {
        return containsTotalCount;
    }

    public void setContainsTotalCount(boolean containsTotalCount) {
        this.containsTotalCount = containsTotalCount;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Boolean getAsyncTotalCount() {
        return asyncTotalCount;
    }

    public void setAsyncTotalCount(Boolean asyncTotalCount) {
        this.asyncTotalCount = asyncTotalCount;
    }

    @Override
    public int getOffset() {
        if(page >= 1){
            return (page-1) * limit;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PageBounds{");
        sb.append("page=").append(page);
        sb.append(", limit=").append(limit);
        sb.append(", orders=").append(orders);
        sb.append(", containsTotalCount=").append(containsTotalCount);
        sb.append(", asyncTotalCount=").append(asyncTotalCount);
        sb.append('}');
        return sb.toString();
    }
}