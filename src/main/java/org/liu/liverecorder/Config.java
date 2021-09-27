package org.liu.liverecorder;

import org.liu.liverecorder.annotations.Option;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    @Option(name = "splitScriptTags", defaultValue = "false")
    public static boolean splitScriptTagsIfCheck;

    @Option(name = "splitAVHeaderTags", followField = "splitScriptTagsIfCheck", defaultValue = "")
    public static Boolean splitAVHeaderTagsIfCheck;

    @Option(name = "delete", defaultValue = "true")
    public static boolean deleteOnchecked;

    @Option(name = "check", defaultValue = "true")
    public static boolean autoCheck;

    @Option(name = "liver", defaultValue = "bili")
    public static String liver;

    @Option(name = "id", defaultValue = "")
    public static String shortId;

    @Option(name = "qn", defaultValue = "")
    public static String qn;

    @Option(name = "qnPri", defaultValue = "")
    public static String[] qnPriority;

    @Option(name = "zip", defaultValue = "false")
    public static boolean flagZip;

    @Option(name = "retry", defaultValue = "5")
    public static int maxFailCnt;
    public static int failCnt = 0;

    @Option(name = "retryIfLiveOff", defaultValue = "false")
    public static boolean retryIfLiveOff;

    @Option(name = "maxRetryIfLiveOff", defaultValue = "0")
    public static int maxRetryIfLiveOff;

    @Option(name = "retryAfterMinutes", defaultValue = "5")
    public static double retryAfterMinutes;

    @Option(name = "failRetryAfterMinutes", defaultValue = "1")
    public static double failRetryAfterMinutes;

    @Option(name = "fileSize", defaultValue = "0")
    public static long splitFileSize = 1024 * 1024;

    @Option(name = "filePeriod", defaultValue = "0")
    public static long splitRecordPeriod = 60 * 1000;

    @Option(name = "fileName", defaultValue = "{name}-{shortId} 的{liver}直播{startTime}-{seq}")
    public static String fileName;

    @Option(name = "timeFormat", defaultValue = "yyyy-MM-dd HH.mm")
    public static String timeFormat;

    @Option(name = "saveFolder", defaultValue = "")
    public static String saveFolder;

    @Option(name = "saveFolderAfterCheck", defaultValue = "")
    public static String saveFolderAfterCheck;

    public static void init(String[] args) {
        if (args != null && args.length >= 1) {
            for (Field field : Config.class.getDeclaredFields()) {
                Option option = field.getAnnotation(Option.class);
                if (option != null) {
                    String value = getValue(args[0], option.name());
                    if (value == null) value = option.defaultValue();
                    if (!value.isEmpty()) {
                        setValue(field, value);
                    }
                }
            }
        }
    }

    /**
     * 根据字段的类型进行类型转换
     *
     * @param field 字段
     * @param value 字段值
     */
    private static void setValue(Field field, String value) {
        try {
            if (field.getType().equals(String.class)) {
                field.set(null, value);
            } else if (field.getType().equals(int.class)) {
                field.set(null, Integer.parseInt(value));
            } else if (field.getType().equals(long.class)) {
                Long obj = (Long) field.get(null);
                field.set(null, obj * Long.parseLong(value));
            } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                field.set(null, "true".equals(value));
            } else if (field.getType().equals(double.class)) {
                field.set(null, Double.parseDouble(value));
            } else if (field.getType().equals(String[].class)) {
                try {
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                field.set(null, value.split(">"));
            } else {
                System.err.println("未知类型：" + field.getType());
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从命令行中获取参数
     *
     * @param param
     * @param key
     * @return
     */
    public static String getValue(String param, String key) {
        Pattern pattern = Pattern.compile(key + "=([^&]*)");
        Matcher matcher = pattern.matcher(param);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
