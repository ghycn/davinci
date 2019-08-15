

package com.huilan.core.utils;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huilan.core.model.CustomDataSource;
import com.huilan.core.consts.Consts;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomDataSourceUtils {

    private static volatile Map<String, CustomDataSource> map = new HashMap<>();

    public static CustomDataSource getInstance(String url) {
        String dataSourceName = getDataSourceName(url);
        if (map.containsKey(dataSourceName) && null != map.get(dataSourceName)) {
            CustomDataSource customDataSource = map.get(dataSourceName);
            if (null != customDataSource) {
                return customDataSource;
            }
        }
        return null;
    }

    public static CustomDataSource getCustomDataSource(String url) throws Exception {
        CustomDataSource customDataSource = getInstance(url);
        if (null != customDataSource) {
            try {
                Class<?> aClass = Class.forName(customDataSource.getDriver());
                if (null == aClass) {
                    throw new Exception("Unable to get driver instance for jdbcUrl: " + url);
                }
            } catch (ClassNotFoundException e) {
                throw new Exception("Unable to get driver instance: " + url);
            }
            return customDataSource;
        }
        return null;
    }


    public static void loadAllFromYaml(String yamlPath) throws Exception {
        if (StringUtils.isEmpty(yamlPath)) {
            return;
        }
        File yamlFile = new File(yamlPath);
        if (!yamlFile.exists()) {
            return;
        }
        if (!yamlFile.isFile()) {
            return;
        }
        if (!yamlFile.canRead()) {
            return;
        }

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(yamlFile);
        } catch (FileNotFoundException e) {
            return;
        }
        if (null == fileReader) {
            return;
        }

        Yaml yaml = new Yaml();
        HashMap<String, Object> loads = yaml.loadAs(new BufferedReader(fileReader), HashMap.class);
        if (!CollectionUtils.isEmpty(loads)) {
            ObjectMapper mapper = new ObjectMapper();
            for (String key : loads.keySet()) {
                CustomDataSource customDataSource = mapper.convertValue(loads.get(key), CustomDataSource.class);
                if (StringUtils.isEmpty(customDataSource.getName()) || StringUtils.isEmpty(customDataSource.getDriver())) {
                    throw new Exception("Load custom datasource error: name or driver cannot be EMPTY");
                }
                if ("null".equals(customDataSource.getName().trim().toLowerCase())) {
                    throw new Exception("Load custom datasource error: invalid name");
                }
                if ("null".equals(customDataSource.getDriver().trim().toLowerCase())) {
                    throw new Exception("Load custom datasource error: invalid driver");
                }

                if (StringUtils.isEmpty(customDataSource.getDesc())) {
                    customDataSource.setDesc(customDataSource.getName());
                }
                if ("null".equals(customDataSource.getDesc().trim().toLowerCase())) {
                    customDataSource.setDesc(customDataSource.getName());
                }

                if (!StringUtils.isEmpty(customDataSource.getKeyword_prefix()) || !StringUtils.isEmpty(customDataSource.getKeyword_suffix())) {
                    if (StringUtils.isEmpty(customDataSource.getKeyword_prefix()) || StringUtils.isEmpty(customDataSource.getKeyword_suffix())) {
                        throw new Exception("Load custom datasource error: keyword prefixes and suffixes must be configured in pairs.");
                    }
                }

                if (!StringUtils.isEmpty(customDataSource.getAlias_prefix()) || !StringUtils.isEmpty(customDataSource.getAlias_suffix())) {
                    if (StringUtils.isEmpty(customDataSource.getAlias_prefix()) || StringUtils.isEmpty(customDataSource.getAlias_suffix())) {
                        throw new Exception("Load custom datasource error: alias prefixes and suffixes must be configured in pairs.");
                    }
                }

                map.put(key.toLowerCase(), customDataSource);
            }
        }
    }

    private static String getDataSourceName(String jdbcUrl) {
        String dataSourceName = null;
        jdbcUrl = jdbcUrl.replaceAll(Consts.NEW_LINE_CHAR, Consts.EMPTY).replaceAll(Consts.SPACE, Consts.EMPTY).trim().toLowerCase();
        String reg = "jdbc:\\w+";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(jdbcUrl);
        if (matcher.find()) {
            dataSourceName = matcher.group().split(Consts.COLON)[1];
        }
        return dataSourceName;
    }
}
