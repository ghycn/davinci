

package com.huilan.zhihui.core.common;

import com.huilan.core.consts.Consts;

/**
 * 常量
 */
public class Constants extends Consts {


    /**
     * api基本路径
     */
    public static final String BASE_API_PATH = "/api/v3";


    /**
     * auth基本路径
     */
    public static final String AUTH_API_PATH = "/auth/v3";

    /**
     * 用户激活 / 重发激活邮件模板
     */
    public static final String USER_ACTIVATE_EMAIL_TEMPLATE = "mail/userActivateEmailTemplate";

    /**
     * 用户激活 / 重发激活邮件主题
     */
    public static final String USER_ACTIVATE_EMAIL_SUBJECT = "[智汇BI] 用户激活";

    /**
     * 用户默认Organization描述
     */
    public static final String DEFAULT_ORGANIZATION_DES = "my default organization";

    /**
     * 用户头像上传地址
     */
    public static final String USER_AVATAR_PATH = "/image/user/";

    /**
     * 组织头像上传地址
     */
    public static final String ORG_AVATAR_PATH = "/image/organization/";


    /**
     * display封面图地址
     */
    public static final String DISPLAY_AVATAR_PATH = "/image/display/";


    /**
     * CSV地址
     */
    public static final String SOURCE_CSV_PATH = "/source/csv/";

    /**
     * 邀请组织成员邮件主题
     * inviter username
     * organization
     */
    public static final String INVITE_ORG_MEMBER_MAIL_SUBJECT = "[智汇BI] %s has invited you to join the %s organization";

    /**
     * 邀请组织成员邮件模板
     */
    public static final String INVITE_ORG_MEMBER_MAIL_TEMPLATE = "mail/inviteOrgMemberTemplate";

    /**
     * 分割符号
     */
    public static final String SPLIT_CHAR_STRING = ":-:";

    /**
     * sql ST模板
     */
    public static final String SQL_TEMPLATE = "templates/sql/sqlTemplate.stg";


    /**
     * excel 表头，数据格式化js
     */
    public static final String TABLE_FORMAT_JS = "templates/js/formatCellValue.js";


    /**
     * 格式化全局参数js
     */
    public static final String EXECUTE_PARAM_FORMAT_JS = "templates/js/executeParam.js";


    /**
     * 定时任务发送邮件模板
     */
    public static final String SCHEDULE_MAIL_TEMPLATE = "mail/scheduleEmaiTemplate";

    /**
     * select 表达式
     */
    public static final String SELECT_EXEPRESSION = "SELECT * FROM TABLE WHERE %s";

    /**
     * 点赞project
     */
    public static final String STAR_TARGET_PROJECT = "project";


    public static final String REG_USER_PASSWORD = ".{6,20}";

    public static final String EXCEL_FORMAT_KEY = "format";

    public static final String EXCEL_FORMAT_TYPE_KEY = "formatType";

    public static final String REG_SQL_PLACEHOLDER = "%s.+%s";

    public static final String REG_AUTHVAR = "\\([a-zA-Z0-9_.-[\\u4e00-\\u9fa5]*]+\\s*[\\w<>!=]*\\s*[a-zA-Z0-9_.-]*((\\(%s[a-zA-Z0-9_]+%s\\))|(%s[a-zA-Z0-9_]+%s))+\\s*\\)";

    public static final String LDAP_USER_PASSWORD = "LDAP";

    public static final String N0_AUTH_PERMISSION = "@DAVINCI_DATA_ACCESS_DENIED@";


    public static char getSqlTempDelimiter(String sqlTempDelimiter) {
        return sqlTempDelimiter.charAt(sqlTempDelimiter.length() - 1);
    }

    public static String getReg(String express, char delimiter, boolean isAuthPress) {
        String arg = String.valueOf(delimiter);
        if (delimiter == DOLLAR_DELIMITER) {
            arg = "\\" + arg;
        }
        if (isAuthPress) {
            return String.format(express, arg, arg, arg, arg);
        } else {
            return String.format(express, arg, arg);
        }
    }
}
