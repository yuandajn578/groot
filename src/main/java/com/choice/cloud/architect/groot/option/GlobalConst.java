package com.choice.cloud.architect.groot.option;

/**
 * @classDesc:
 * @author: yuxiaopeng
 * @createTime: 2018/12/20
 * @version: v1.0.0
 * @copyright: 北京辰森
 * @email: yuxiaopeng@choicesoft.com.cn
 */
public class GlobalConst {

    /**
     * 已删除({@value})
     */
    public static final Integer DELETE = 1;
    /**
     * 未删除({@value})
     */
    public static final Integer NOT_DELETE = 0;
    /**
     * 启用({@value})
     */
    public static final Integer ENABLE = 1;
    /**
     * 禁用({@value})
     */
    public static final Integer DISABLE = 0;
    /**
     * 模糊查询字符
     */
    public static final String SQL_LIKE_SYMBOL = "%";

    public static final String ENV_CODE_STABLE = "stable";

    public static final String JVM_OPT = "-server -Xms2048m -Xmx2048m -Xmn1024m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=768m -XX:+PrintGCDateStamps -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+CMSScavengeBeforeRemark -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=/tmp";

    /**
     * 待发布({@value})
     */
    public static final Integer PUBLISHING = 0;
    /**
     * 已发布({@value})
     */
    public static final Integer PUBLISHED = 1;

    /**
     * 是
     */
    public static final String TRUE = "TRUE";

    /**
     * 否
     */
    public static final String FALSE = "FALSE";
}
