

package com.huilan.core.enums;

import com.huilan.core.exception.SourceException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DataTypeEnum {

    MYSQL("mysql", "mysql", "com.mysql.jdbc.Driver", "`", "`", "'", "'"),

    ORACLE("oracle", "oracle", "oracle.jdbc.driver.OracleDriver", "\"", "\"", "\"", "\""),

    SQLSERVER("sqlserver", "sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "\"", "\"", "\"", "\""),

    H2("h2", "h2", "org.h2.Driver", "`", "`", "\"", "\""),

    PHOENIX("phoenix", "hbase phoenix", "org.apache.phoenix.jdbc.PhoenixDriver", "", "", "\"", "\""),

    MONGODB("mongo", "mongodb", "mongodb.jdbc.MongoDriver", "`", "`", "\"", "\""),

    ELASTICSEARCH("sql4es", "elasticsearch", "nl.anchormen.sql4es.jdbc.ESDriver", "", "", "'", "'"),

    PRESTO("presto", "presto", "com.facebook.presto.jdbc.PrestoDriver", "", "", "\"", "\""),

    MOONBOX("moonbox", "moonbox", "moonbox.jdbc.MbDriver", "`", "`", "`", "`"),

    CASSANDRA("cassandra", "cassandra", "com.github.adejanovski.cassandra.jdbc.CassandraDriver", "", "", "'", "'"),

    CLICKHOUSE("clickhouse", "clickhouse", "ru.yandex.clickhouse.ClickHouseDriver", "", "", "\"", "\""),

    KYLIN("kylin", "kylin", "org.apache.kylin.jdbc.Driver", "\"", "\"", "\"", "\""),

    VERTICA("vertica", "vertica", "com.vertica.jdbc.Driver", "", "", "'", "'"),

    HANA("sap", "sap hana", "com.sap.db.jdbc.Driver", "", "", "'", "'"),

    IMPALA("impala", "impala", "com.cloudera.impala.jdbc41.Driver", "", "", "'", "'");


    private String feature;
    private String desc;
    private String driver;
    private String keywordPrefix;
    private String keywordSuffix;
    private String aliasPrefix;
    private String aliasSuffix;

    private static final String jdbcUrlPrefix = "jdbc:";

    DataTypeEnum(String feature, String desc, String driver, String keywordPrefix, String keywordSuffix, String aliasPrefix, String aliasSuffix) {
        this.feature = feature;
        this.desc = desc;
        this.driver = driver;
        this.keywordPrefix = keywordPrefix;
        this.keywordSuffix = keywordSuffix;
        this.aliasPrefix = aliasPrefix;
        this.aliasSuffix = aliasSuffix;
    }

    public static DataTypeEnum urlOf(String jdbcUrl) throws SourceException {
        String url = jdbcUrl.toLowerCase().trim();
        for (DataTypeEnum dataTypeEnum : values()) {
            if (url.startsWith(jdbcUrlPrefix + dataTypeEnum.feature)) {
                try {
                    Class<?> aClass = Class.forName(dataTypeEnum.getDriver());
                    if (null == aClass) {
                        throw new SourceException("Unable to get driver instance for jdbcUrl: " + jdbcUrl);
                    }
                } catch (ClassNotFoundException e) {
                    throw new SourceException("Unable to get driver instance: " + jdbcUrl);
                }
                return dataTypeEnum;
            }
        }
        return null;
    }

    public String getFeature() {
        return feature;
    }

    public String getDesc() {
        return desc;
    }

    public String getDriver() {
        return driver;
    }

    public String getKeywordPrefix() {
        return keywordPrefix;
    }

    public String getKeywordSuffix() {
        return keywordSuffix;
    }

    public String getAliasPrefix() {
        return aliasPrefix;
    }

    public String getAliasSuffix() {
        return aliasSuffix;
    }
}
