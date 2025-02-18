

package com.huilan.zhihui.common.utils;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.enums.FieldFormatTypeEnum;
import com.huilan.zhihui.core.enums.NumericUnitEnum;
import com.huilan.zhihui.core.model.*;
import com.huilan.zhihui.dto.viewDto.Aggregator;
import com.huilan.zhihui.dto.viewDto.Order;
import com.huilan.zhihui.dto.viewDto.Param;
import com.huilan.zhihui.dto.viewDto.ViewExecuteParam;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.huilan.core.consts.Consts.EMPTY;
import static com.huilan.zhihui.core.common.Constants.EXCEL_FORMAT_TYPE_KEY;

@Component
public class ScriptUtiils {
    private static ClassLoader classLoader = ScriptUtiils.class.getClassLoader();


    public static ScriptEngine getCellValueScriptEngine() throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(new InputStreamReader(classLoader.getResourceAsStream(Constants.TABLE_FORMAT_JS)));
        return engine;
    }

    public static ScriptEngine getExecuptParamScriptEngine() throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(new InputStreamReader(classLoader.getResourceAsStream(Constants.EXECUTE_PARAM_FORMAT_JS)));
        return engine;
    }

    public static ViewExecuteParam getViewExecuteParam(ScriptEngine engine, String dashboardConfig, String widgetConfig, Long releationId) {
        try {
            Invocable invocable = (Invocable) engine;
            Object obj = invocable.invokeFunction("getDashboardItemExecuteParam", dashboardConfig, widgetConfig, releationId);

            if (obj instanceof ScriptObjectMirror) {
                ScriptObjectMirror vsom = (ScriptObjectMirror) obj;
                List<String> groups = new ArrayList<>();
                List<Aggregator> aggregators = new ArrayList<>();
                List<Order> orders = new ArrayList<>();
                List<String> filters = new ArrayList<>();
                Boolean cache = false;
                Boolean nativeQuery = false;

                Long expired = 0L;
                List<Param> params = new ArrayList<>();
                for (String key : vsom.keySet()) {
                    switch (key) {
                        case "groups":
                            ScriptObjectMirror groupMirror = (ScriptObjectMirror) vsom.get(key);
                            if (groupMirror.isArray()) {
                                Collection<Object> values = groupMirror.values();
                                values.forEach(v -> groups.add(String.valueOf(v)));
                            }
                            break;
                        case "aggregators":
                            ScriptObjectMirror aggregatorsMirror = (ScriptObjectMirror) vsom.get(key);
                            if (aggregatorsMirror.isArray()) {
                                Collection<Object> values = aggregatorsMirror.values();
                                values.forEach(v -> {
                                    ScriptObjectMirror agg = (ScriptObjectMirror) v;
                                    Aggregator aggregator = new Aggregator(String.valueOf(agg.get("column")), String.valueOf(agg.get("func")));
                                    aggregators.add(aggregator);
                                });
                            }
                            break;
                        case "orders":
                            ScriptObjectMirror ordersMirror = (ScriptObjectMirror) vsom.get(key);
                            if (ordersMirror.isArray()) {
                                Collection<Object> values = ordersMirror.values();
                                values.forEach(v -> {
                                    ScriptObjectMirror agg = (ScriptObjectMirror) v;
                                    Order order = new Order(String.valueOf(agg.get("column")), String.valueOf(agg.get("direction")));
                                    orders.add(order);
                                });
                            }
                            break;
                        case "filters":
                            ScriptObjectMirror filterMirror = (ScriptObjectMirror) vsom.get(key);
                            if (filterMirror.isArray()) {
                                Collection<Object> values = filterMirror.values();
                                values.forEach(v -> filters.add(String.valueOf(v)));
                            }
                            break;
                        case "cache":
                            cache = (Boolean) vsom.get(key);
                            break;
                        case "expired":
                            expired = Long.parseLong(String.valueOf(vsom.get(key)));
                            break;
                        case "params":
                            ScriptObjectMirror paramsMirror = (ScriptObjectMirror) vsom.get(key);
                            if (paramsMirror.isArray()) {
                                Collection<Object> values = paramsMirror.values();
                                values.forEach(v -> {
                                    ScriptObjectMirror agg = (ScriptObjectMirror) v;
                                    Param param = new Param(String.valueOf(agg.get("name")), String.valueOf(agg.get("value")));
                                    params.add(param);
                                });
                            }
                            break;
                        case "nativeQuery":
                            nativeQuery = (Boolean) vsom.get(key);
                            break;
                    }
                }

                return new ViewExecuteParam(groups, aggregators, orders, filters, params, cache, expired, nativeQuery);
            }

        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<ExcelHeader> formatHeader(ScriptEngine engine, String json, List<Param> params) {
        try {
            Invocable invocable = (Invocable) engine;
            Object obj = invocable.invokeFunction("getFieldsHeader", json, params);

            if (obj instanceof ScriptObjectMirror) {
                ScriptObjectMirror som = (ScriptObjectMirror) obj;
                if (som.isArray()) {
                    final List<ExcelHeader> excelHeaders = new ArrayList<>();
                    Collection<Object> values = som.values();
                    values.forEach(v -> {
                        ExcelHeader header = new ExcelHeader();
                        ScriptObjectMirror vsom = (ScriptObjectMirror) v;
                        for (String key : vsom.keySet()) {
                            if (!StringUtils.isEmpty(key)) {
                                String setter = "set" + String.valueOf(key.charAt(0)).toUpperCase() + key.substring(1);
                                Object o = vsom.get(key);
                                Class clazz = o.getClass();

                                try {
                                    if (o instanceof ScriptObjectMirror) {
                                        ScriptObjectMirror mirror = (ScriptObjectMirror) o;
                                        if ("range".equals(key)) {
                                            if (mirror.isArray()) {
                                                int[] array = new int[4];
                                                for (int i = 0; i < 4; i++) {
                                                    array[i] = Integer.parseInt(mirror.get(i + EMPTY).toString());
                                                }
                                                header.setRange(array);
                                            }
                                        } else if ("style".equals(key)) {
                                            if (mirror.isArray()) {
                                                List<String> list = new ArrayList<>();
                                                for (int i = 0; i < 4; i++) {
                                                    list.add(mirror.get(i + EMPTY).toString());
                                                }
                                                header.setStyle(list);
                                            }
                                        } else if ("format".equals(key)) {
                                            String formatType = mirror.get(EXCEL_FORMAT_TYPE_KEY).toString();
                                            ScriptObjectMirror format = (ScriptObjectMirror) mirror.get(formatType);

                                            if (null != format) {
                                                FieldFormatTypeEnum typeEnum = FieldFormatTypeEnum.typeOf(formatType);
                                                ObjectMapper mapper = new ObjectMapper();

                                                NumericUnitEnum numericUnit = null;
                                                if (format.containsKey("unit")) {
                                                    numericUnit = NumericUnitEnum.unitOf(String.valueOf(format.get("unit")));
                                                }

                                                switch (typeEnum) {
                                                    case Currency:
                                                        FieldCurrency fieldCurrency = mapper.convertValue(format, FieldCurrency.class);
                                                        if (null != fieldCurrency) {
                                                            fieldCurrency.setUnit(numericUnit);
                                                            header.setFormat(fieldCurrency);
                                                        }
                                                        break;
                                                    case Custom:
                                                        FieldCustom fieldCustom = mapper.convertValue(format, FieldCustom.class);
                                                        header.setFormat(fieldCustom);
                                                        break;
                                                    case Date:
                                                        FieldDate fieldDate = mapper.convertValue(format, FieldDate.class);
                                                        header.setFormat(fieldDate);

                                                        break;
                                                    case Numeric:
                                                        FieldNumeric fieldNumeric = mapper.convertValue(format, FieldNumeric.class);
                                                        fieldNumeric.setUnit(numericUnit);
                                                        header.setFormat(fieldNumeric);

                                                        break;
                                                    case Percentage:
                                                        FieldPercentage fieldPercentage = mapper.convertValue(format, FieldPercentage.class);
                                                        header.setFormat(fieldPercentage);

                                                        break;
                                                    case ScientificNotation:
                                                        FieldScientificNotation scientificNotation = mapper.convertValue(format, FieldScientificNotation.class);
                                                        header.setFormat(scientificNotation);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }

                                    } else {
                                        Method method = header.getClass().getMethod(setter, clazz);
                                        method.invoke(header, vsom.get(key));
                                    }
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } finally {
                                    continue;
                                }
                            }
                        }
                        excelHeaders.add(header);
                    });
                    return excelHeaders;
                }
            }

        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
