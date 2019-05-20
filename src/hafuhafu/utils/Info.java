package hafuhafu.utils;

/**
 * @Auther: Tang
 * @Date: 2019/5/20 10:07
 * @Description:
 */
public class Info {
    private static final String INFO = "INFO";
    private static final String ERROR = "ERROR";
    private static final String WARM = "WARM";
    private static final String TIPS = "TIPS";

    private static String build(String msg, String type) {
        return "[" + type + "]" + msg;
    }

    public static String separator() {
        return System.getProperty("line.separator");
    }

    public static String info(String msg) {
        return build(msg, INFO);
    }

    public static String error(String msg) {
        return build(msg, ERROR);
    }

    public static String warm(String msg) {
        return build(msg, WARM);
    }

    public static String tips(String msg) {
        return build(msg, TIPS);
    }
}
