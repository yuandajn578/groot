package com.choice.cloud.architect.groot.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 文字转拼音工具类
 */
@Slf4j
public class HanyupinyinUtil {

    /**
     * 将文字转为汉语拼音,全拼(复兴路=>fuxinglu)
     *
     * @param ChineseLanguage 要转换的文字
     * @return String
     */
    public static String getPinyinString(String ChineseLanguage) {
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出拼音全部小写
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 不带声调
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        try {
            for (int i = 0; i < cl_chars.length; i++) {
                // 如果字符是中文,则将中文转为汉语拼音
                if (String.valueOf(cl_chars[i]).matches("[\u4e00-\u9fa5]+")) {
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_chars[i], defaultFormat)[0];
                    // 如果字符不是中文,则不转换
                } else {
                    hanyupinyin += cl_chars[i];
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error("字符不能转成汉语拼音");
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    /**
     * 取每个汉字的第一个字符,大写(复兴路=>FXL)
     *
     * @param ChineseLanguage 要转换的文字
     * @return String
     */
    public static String getFirstLettersUp(String ChineseLanguage) {
        return getFirstLetters(ChineseLanguage, HanyuPinyinCaseType.UPPERCASE);
    }

    /**
     * 取每个汉字的第一个字符,小写(复兴路=>fxl)
     *
     * @param ChineseLanguage 要转换的文字
     * @return String
     */
    public static String getFirstLettersLo(String ChineseLanguage) {
        return getFirstLetters(ChineseLanguage, HanyuPinyinCaseType.LOWERCASE);
    }

    /**
     * 将文字转为汉语拼音首字母,大写或小写需要自己指定(用上面的就行了,这个就别用了)
     *
     * @param ChineseLanguage 要转换的文字
     * @param caseType        UPPERCASE->大写,LOWERCASE->小写
     * @return String
     */
    public static String getFirstLetters(String ChineseLanguage, HanyuPinyinCaseType caseType) {
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出拼音全部大写
        defaultFormat.setCaseType(caseType);
        // 不带声调
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        hanyupinyin = getHanYuPinYinString(cl_chars, hanyupinyin, defaultFormat);
        return hanyupinyin;
    }

    /**
     * 取第一个汉字的第一个字符(复兴路=>F)
     *
     * @param ChineseLanguage 要转换的文字
     * @return String
     */
    public static String getFirstLetter(String ChineseLanguage) {
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出拼音全部大写
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        // 不带声调
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        try {
            String str = String.valueOf(cl_chars[0]);
            // 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
            if (str.matches("[\u4e00-\u9fa5]+")) {
                hanyupinyin = PinyinHelper.toHanyuPinyinStringArray(
                    cl_chars[0], defaultFormat)[0].substring(0, 1);
                // 如果字符是数字,取数字
            } else if (str.matches("[0-9]+")) {
                hanyupinyin += cl_chars[0];
                // 如果字符是字母,取字母
            } else if (str.matches("[a-zA-Z]+")) {

                hanyupinyin += cl_chars[0];
                // 否则不转换
            } else {

            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error("字符不能转成汉语拼音");
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    /**
     * 获取汉语拼音
     * @param cl_chars
     * @param hanyupinyin
     * @param defaultFormat
     * @return
     */
    private static String getHanYuPinYinString(char[] cl_chars, String hanyupinyin, HanyuPinyinOutputFormat defaultFormat) {
        try {
            for (int i = 0; i < cl_chars.length; i++) {
                String str = String.valueOf(cl_chars[i]);
                // 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                if (str.matches("[\u4e00-\u9fa5]+")) {
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_chars[i], defaultFormat)[0].substring(0, 1);
                    // 如果字符是数字,取数字
                } else if (str.matches("[0-9]+")) {
                    hanyupinyin += cl_chars[i];
                    // 如果字符是字母,取字母
                } else if (str.matches("[a-zA-Z]+")) {
                    hanyupinyin += cl_chars[i];
                    // 否则不转换
                } else {
                    // 如果是标点符号的话，带着
                    hanyupinyin += cl_chars[i];
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error("字符不能转成汉语拼音");
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    public static void main(String[] args) {
        System.out.println("getPinyinString===============" + getPinyinString("复兴路"));
        System.out.println("getFirstLettersUp===============" + getFirstLettersUp("复兴路"));
        System.out.println("getFirstLettersLo===============" + getFirstLettersLo("复兴路"));
        System.out.println("getFirstLetter===============" + getFirstLetter("复兴路"));
    }
}