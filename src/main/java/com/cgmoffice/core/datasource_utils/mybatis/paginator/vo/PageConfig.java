package com.cgmoffice.core.datasource_utils.mybatis.paginator.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageConfig {

	@Builder.Default
	String page = "1";

	@Builder.Default
	String limit = null;

	@Builder.Default
	int sliderCount = -1;

	List<PagingConfigOrder> orders;

	@Builder
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PagingConfigOrder {

		String target;

		@Builder.Default
		boolean isAsc = true;

		public boolean getIsAsc() {
			return isAsc;
		}

		public void setIsAsc(boolean isAsc) {
			this.isAsc = isAsc;
		}

	}
}
