package com.cgmoffice.core.datasource_utils.mybatis.paginator;

import jakarta.servlet.ServletContextEvent;

public class CleanupMybatisPaginatorListener {


    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        OffsetLimitInterceptor.Pool.shutdownNow();
    }
}
