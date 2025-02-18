

package com.huilan.zhihui.core.enums;

import com.sun.tools.javac.util.ListBuffer;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.relational.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SqlOperatorEnum {
    IN("IN"),
    NoTIN("NOT IN"),
    EQUALSTO("="),
    BETWEEN("BETWEEN"),
    GREATERTHAN(">"),
    GREATERTHANEQUALS(">="),
    ISNULL("IS NULL"),
    LIKE("LIKE"),
    MINORTHAN("<"),
    MINORTHANEQUALS("<="),
    NOTEQUALSTO("!="),
    EXISTS("EXISTS");

    private String value;

    SqlOperatorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SqlOperatorEnum getSqlOperator(String src) {
        for (SqlOperatorEnum operatorEnum : SqlOperatorEnum.values()) {
            if (src.toUpperCase().indexOf(operatorEnum.value) > -1) {
                return operatorEnum;
            }
        }
        return null;
    }

    public static ExpressionVisitorAdapter getVisitor(ListBuffer<Map<SqlOperatorEnum, List<String>>> listBuffer) {
        Map<SqlOperatorEnum, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        return new ExpressionVisitorAdapter() {

            @Override
            public void visit(InExpression expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getRightItemsList().toString());

                if (expr.isNot()) {
                    map.put(SqlOperatorEnum.NoTIN, list);
                } else {
                    map.put(SqlOperatorEnum.IN, list);
                }

                listBuffer.append(map);
            }

            @Override
            public void visit(EqualsTo expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getRightExpression().toString());

                map.put(SqlOperatorEnum.EQUALSTO, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(NotEqualsTo expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getRightExpression().toString());

                map.put(SqlOperatorEnum.NOTEQUALSTO, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(Between expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getBetweenExpressionStart().toString() + " AND " + expr.getBetweenExpressionEnd().toString());

                map.put(SqlOperatorEnum.BETWEEN, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(GreaterThan expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getRightExpression().toString());

                map.put(SqlOperatorEnum.GREATERTHAN, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(GreaterThanEquals expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getRightExpression().toString());

                map.put(SqlOperatorEnum.GREATERTHANEQUALS, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(MinorThan expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getRightExpression().toString());

                map.put(SqlOperatorEnum.MINORTHAN, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(MinorThanEquals expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getRightExpression().toString());

                map.put(SqlOperatorEnum.MINORTHANEQUALS, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(IsNullExpression expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());

                map.put(SqlOperatorEnum.ISNULL, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(LikeExpression expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getLeftExpression().toString());
                list.add(expr.getRightExpression().toString());

                map.put(SqlOperatorEnum.LIKE, list);

                listBuffer.append(map);
            }

            @Override
            public void visit(ExistsExpression expr) {
                super.visit(expr);
                list.clear();
                map.clear();

                list.add(expr.getRightExpression().toString());

                map.put(SqlOperatorEnum.EXISTS, list);

                listBuffer.append(map);
            }
        };
    }
}
