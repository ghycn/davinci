

package com.huilan.zhihui.core.utils;

import com.alibaba.druid.util.StringUtils;
import com.huilan.core.exception.ServerException;
import com.huilan.core.model.QueryColumn;
import com.huilan.core.utils.CollectionUtils;
import com.huilan.core.utils.SqlUtils;
import com.huilan.zhihui.core.enums.FileTypeEnum;
import com.huilan.zhihui.core.enums.SqlColumnEnum;
import com.huilan.zhihui.core.model.DataUploadEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

import static com.huilan.core.consts.Consts.EMPTY;


public class CsvUtils {


    /**
     * 解析Csv
     *
     * @param csvFile
     * @param charsetName
     * @return
     * @throws ServerException
     */
    public static DataUploadEntity parseCsvWithFirstAsHeader(MultipartFile csvFile, String charsetName) throws ServerException {

        if (null == csvFile) {
            throw new ServerException("Invalid csv file");
        }

        if (!csvFile.getOriginalFilename().toLowerCase().endsWith(FileTypeEnum.CSV.getType())) {
            throw new ServerException("Invalid csv file");
        }

        DataUploadEntity dataUploadEntity = null;
        BufferedReader reader = null;
        CSVParser csvParser = null;
        try {
            reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream(), charsetName));
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());

            Set<String> csvHeaders = csvParser.getHeaderMap().keySet();

            List<CSVRecord> records = csvParser.getRecords();
            List<Map<String, Object>> values = null;
            Set<QueryColumn> headers = null;

            if (!CollectionUtils.isEmpty(records)) {
                headers = new HashSet<>();
                for (String key : csvHeaders) {
                    headers.add(new QueryColumn(key.replace("\uFEFF", EMPTY), SqlUtils.formatSqlType(records.get(0).get(key))));
                }
                if (records.size() > 1) {
                    values = new ArrayList<>();
                    for (int i = 1; i < records.size(); i++) {
                        Map<String, Object> item = new HashMap<>();
                        for (String key : csvHeaders) {
                            item.put(key.replace("\uFEFF", EMPTY), SqlColumnEnum.formatValue(records.get(0).get(key), records.get(i).get(key)));
                        }
                        values.add(item);
                    }
                }

                dataUploadEntity = new DataUploadEntity();
                dataUploadEntity.setHeaders(headers);
                dataUploadEntity.setValues(values);
            }


            csvParser.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(e.getMessage());
        } finally {
            try {
                if (null != csvParser) {
                    csvParser.close();
                }

                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServerException(e.getMessage());
            }
        }

        return dataUploadEntity;
    }


    /**
     * 写入csv
     *
     * @param filePath
     * @param fileName
     * @param columns
     * @param dataList
     * @return
     * @throws ServerException
     */
    public static String formatCsvWithFirstAsHeader(String filePath, String fileName, List<QueryColumn> columns, List<Map<String, Object>> dataList) throws ServerException {

        String csvFullName = null;
        if (!CollectionUtils.isEmpty(columns)) {

            List<String> headers = new ArrayList<>();
            List<String> headerTypes = new ArrayList<>();
            for (QueryColumn column : columns) {
                headers.add(column.getName());
                headerTypes.add(column.getType());
            }

            if (!fileName.toLowerCase().endsWith(FileTypeEnum.CSV.getFormat())) {
                fileName = fileName.trim() + FileTypeEnum.CSV.getFormat();
            }

            if (!StringUtils.isEmpty(filePath)) {
                File dir = new File(filePath);
                if (!dir.exists() || !dir.isDirectory()) {
                    dir.mkdirs();
                }
            }

            File file = new File(filePath + File.separator + fileName);
            if (file.exists()) {
                fileName = fileName.substring(0, fileName.lastIndexOf(".") - 1) + "_" + UUID.randomUUID() + FileTypeEnum.CSV.getFormat();
            }

            csvFullName = filePath + File.separator + fileName;

            FileWriter fileWriter = null;

            CSVPrinter csvPrinter = null;

            try {
                CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();


                fileWriter = new FileWriter(csvFullName, true);

                fileWriter.write("\uFEFF"); //解决csv用excel打开乱码问题

                csvPrinter = new CSVPrinter(fileWriter, csvFormat);

                csvPrinter.printRecord(headers);
                csvPrinter.printRecord(headerTypes);

                if (!CollectionUtils.isEmpty(dataList)) {
                    for (Map<String, Object> map : dataList) {
                        List<Object> list = new ArrayList<>();
                        for (String key : headers) {
                            list.add(map.get(key));
                        }
                        csvPrinter.printRecord(list);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new ServerException(e.getMessage());
            } finally {
                try {
                    csvPrinter.flush();
                    fileWriter.flush();
                    fileWriter.close();
                    csvPrinter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ServerException(e.getMessage());
                }
            }
        }
        return csvFullName;
    }
}
