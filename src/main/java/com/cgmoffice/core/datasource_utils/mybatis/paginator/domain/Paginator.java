package com.cgmoffice.core.datasource_utils.mybatis.paginator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * page, limit, totalCount에 따라 호출기는 페이지에 여러 항목을 표시하고 페이지 호출을 용이하게하기 위해 페이지 번호와 현재 페이지 사이의 오프셋을 계산하는 데 사용됩니다.
 *
 */
public class Paginator implements Serializable {
    private static final long serialVersionUID = -2429864663690465105L;

    private int sliderCount;

    /**
     * 페이징 크기
     */
    private int limit;
    /**
     * 페이지 수
     */
    private int page;
    /**
     * 총 레코드 수
     */
    private int totalCount;    

    public Paginator(int page, int limit, int totalCount, int sliderCount) {
        super();
        this.limit = limit;
        this.totalCount = totalCount;
        this.page = computePageNo(page);
        this.sliderCount = sliderCount;
    }

    /**
     * 현재 페이지를 가져옵니다.
     */
    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    /**
     * 총 항목 수를 가져옵니다.
     *
     * @return 총 항목 수
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 첫번째 페이지여부
     *
     * @return 첫번째 페이지 식별자
     */
    public boolean isFirstPage() {
        return page <= 1;
    }

    /**
     * 마지막 페이지여부
     *
     * @return 마지막 페이지 식별자
     */
    public boolean isLastPage() {
        return page >= getTotalPages();
    }

    public int getPrePage() {
        if (isHasPrePage()) {
            return page - 1;
        } else {
            return page;
        }
    }

    public int getNextPage() {
        if (isHasNextPage()) {
            return page + 1;
        } else {
            return page;
        }
    }

    /**
     * 지정된 페이지 번호가 금지되어 있는지, 즉 지정된 페이지 번호가 범위를 벗어나거나 현재 페이지 번호와 같은지 여부를 결정
     *
     * @param page 페이지 번호
     * @return boolean  금지 된 페이지 번호여부
     */
    public boolean isDisabledPage(int page) {
        return ((page < 1) || (page > getTotalPages()) || (page == this.page));
    }

    /**
     * 이전 페이지 존재 여부
     *
     * @return 이전 페이지 존재 여부
     */
    public boolean isHasPrePage() {
        return (page - 1 >= 1);
    }

    /**
     * 다음 페이지가 존재여부
     *
     * @return 다음 페이지 식별자
     */
    public boolean isHasNextPage() {
        return (page + 1 <= getTotalPages());
    }
    
    /**
     * 이전 페이지그룹이 존재하는지 여부
     * @return
     */
    public boolean isHasPrePageGroup() {
    	return (((page - 1) / this.sliderCount) - 1 >= 0);
    }
    
    /**
     * 다음 페이지그룹이 존재하는지 여부
     * @return
     */
    public boolean isHasNextPageGroup() {
    	return (((page - 1) / this.sliderCount) < ((getTotalPages()-1) / this.sliderCount));
    }

    /**
     * 시작 라인, 오라클 페이징 (1 기반)에 사용할 수 있습니다.。
     */
    public int getStartRow() {
        if (getLimit() <= 0 || totalCount <= 0) {
        	return 0;
        }
        return page > 0 ? (page - 1) * getLimit() + 1 : 0;
    }

    /**
     * 엔드 라인은 오라클 페이징 (1 기반)에 사용될 수 있습니다.
     */
    public int getEndRow() {
        return page > 0 ? Math.min(limit * page, getTotalCount()) : 0;
    }

    /**
     * offset，계산은 0에서 시작하여 mysql 페이징 (0 기반)
     */
    public int getOffset() {
        return page > 0 ? (page - 1) * getLimit() : 0;
    }



    /**
     * 총 페이지 수 가져 오기
     *
     * @return
     */
    public int getTotalPages() {
        if (totalCount <= 0) {
            return 0;
        }
        if (limit <= 0) {
            return 0;
        }

        int count = totalCount / limit;
        if (totalCount % limit > 0) {
            count++;
        }
        return count;
    }

    protected int computePageNo(int page) {
        return computePageNumber(page, limit, totalCount);
    }

    public Integer[] getPageSlider() {
    	List<Integer> result = new ArrayList<Integer>();
    	int lastPageNumber = getTotalPages();
    	int pageNum = ((getBlockNo() - 1) * this.sliderCount);
    	for(int i = 0; i < this.sliderCount; i++) {
    		++pageNum;
    		if(pageNum > lastPageNumber) {
    			break;
    		}
    		result.add(pageNum);
    	}
    	return result.toArray(new Integer[result.size()]);
    }
    
    private static int computeLastPageNumber(int totalItems, int pageSize) {
        if (pageSize <= 0) {
        	return 1;
        }
        int result = (int) (totalItems % pageSize == 0 ?
                totalItems / pageSize
                : totalItems / pageSize + 1);
        if (result <= 1) {
        	result = 1;
        }
        return result;
    }

    private int computePageNumber(int page, int pageSize, int totalItems) {
        if (page <= 1) {
            return 1;
        }
        if (Integer.MAX_VALUE == page
                || page > computeLastPageNumber(totalItems, pageSize)) { //last page
            return computeLastPageNumber(totalItems, pageSize);
        }
        return page;
    }

    public int getBlockNo() {
		return ((getPage()-1) / this.sliderCount) + 1;
	}
    
    public int getPreBlockPageNo() {
    	int blockNo = getBlockNo();
    	if(blockNo <= 1) {
    		return -1;
    	} else {
    		return (blockNo - 1) * this.sliderCount;
    	}
    }
    
    public int getNextBlockPageNo() {
    	int blockNo = getBlockNo();
    	int lastBlockNo = ((getTotalPages()-1) / this.sliderCount) + 1;
    	if(blockNo >= lastBlockNo) {
    		return -1;
    	} else {
    		return blockNo * this.sliderCount + 1;
    	}
    }

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Paginator");
        sb.append("{page=").append(page);
        sb.append(", limit=").append(limit);
        sb.append(", totalCount=").append(totalCount);
        sb.append('}');
        return sb.toString();
    }
}
