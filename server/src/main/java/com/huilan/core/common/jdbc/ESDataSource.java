

package com.huilan.core.common.jdbc;

import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
import com.huilan.core.exception.SourceException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_CONNECTIONPROPERTIES;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_URL;

@Slf4j
public class ESDataSource {

    private ESDataSource() {
    }

    private static volatile DataSource dataSource = null;

    private static volatile Map<String, DataSource> map = new HashMap<>();

    public static synchronized DataSource getDataSource(String jdbcUrl) throws SourceException {
        if (!map.containsKey(jdbcUrl.trim()) || null == map.get(jdbcUrl.trim())) {
            Properties properties = new Properties();
            properties.setProperty(PROP_URL, jdbcUrl.trim());
            properties.put(PROP_CONNECTIONPROPERTIES, "client.transport.ignore_cluster_name=true");
            try {
                dataSource = ElasticSearchDruidDataSourceFactory.createDataSource(properties);
                map.put(jdbcUrl.trim(), dataSource);
            } catch (Exception e) {
                log.error("Exception during pool initialization, ", e);
                throw new SourceException(e.getMessage());
            }
        }
        return map.get(jdbcUrl.trim());
    }

    public static void removeDataSource(String jdbcUrl) {
        if (map.containsKey(map.containsKey(jdbcUrl.trim()))) {
            map.remove(jdbcUrl.trim());
        }
    }
}
