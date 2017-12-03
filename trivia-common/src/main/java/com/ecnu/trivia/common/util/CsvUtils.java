/** Created by Jack Chen at 2014/6/24 */
package com.ecnu.trivia.common.util;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.testng.collections.Lists;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * csv工具类，用于读取csv文件
 *
 * @author Jack Chen
 */
public class CsvUtils {

    private static CsvParser defaultCsvParser() {
        return new CsvParser(new CsvParserSettings());
    }

    /**
     * 读取头信息，即第一行的数据信息，按,分隔
     * <p/>
     * 以UTF8编码进行读取
     */
    public static String[] readHeader(File file) throws IOException {
        String firstLine = Files.readFirstLine(file, Charsets.UTF_8);

        return StringUtils.splitByComma(firstLine);
    }

    /** 读取指定url地址文件的起始行,按,分隔 */
    public static String[] readHeader(URL url) throws IOException {
        String firstLine = Resources.asCharSource(url, Charsets.UTF_8).readFirstLine();

        return StringUtils.splitByComma(firstLine);
    }

    private static class MatchLineProcessor implements LineProcessor<String[]> {
        private String[] matches = null;
        private int matchColumn;
        private boolean checkMatchColumn = true;
        private String matchColumnHeader;
        private CsvParser csvParser = defaultCsvParser();

        private MatchLineProcessor(int matchColumn, String matchColumnHeader) {
            this.matchColumn = matchColumn;
            this.matchColumnHeader = matchColumnHeader;
        }

        private MatchLineProcessor(String matchColumnHeader) {
            this.matchColumnHeader = matchColumnHeader;
            checkMatchColumn = false;
        }

        @Override
        public boolean processLine(@Nonnull String line) throws IOException {
            String[] strs = csvParser.parseLine(line);

            if(strs.length == 0) {
                return true;
            }

            if(checkMatchColumn && strs.length != matchColumn) {
                return true;
            }

            String columnHeader = strs[0];
            if(!Objects.equals(columnHeader, matchColumnHeader)) {
                return true;
            }

            //匹配OK,设置值，返回之
            matches = strs;

            return false;
        }

        @Override
        public String[] getResult() {
            return matches;
        }
    }

    /**
     * 读取指定的行数,并且该行的第1个串匹配指定的字符串(equals匹配)
     *
     * @param matchColumn 如果相应列不是指定列数，则忽略
     * @return 匹配的子串列表，如果未匹配，则返回null
     */
    public static String[] readLine(File file, final String matchColumnHeader, final int matchColumn) throws IOException {
        return Files.readLines(file, Charsets.UTF_8, new MatchLineProcessor(matchColumn, matchColumnHeader));
    }


    /**
     * 读取指定的行数,并且该行的第1个串匹配指定的字符串(equals匹配)
     *
     * @param matchColumn 如果相应列不是指定列数，则忽略
     * @return 匹配的子串列表，如果未匹配，则返回null
     */
    public static String[] readLine(URL url, String matchColumnHeader, int matchColumn) throws IOException {
        return Resources.asCharSource(url, Charsets.UTF_8).readLines(new MatchLineProcessor(matchColumn, matchColumnHeader));
    }

    /**
     * 读取指定的行数,并且该行的第1个串匹配指定的字符串(equals匹配)
     *
     * @return 匹配的子串列表，如果未匹配，则返回null
     */
    public static String[] readLine(URL url, String matchColumnHeader) throws IOException {
        return Resources.asCharSource(url, Charsets.UTF_8).readLines(new MatchLineProcessor(matchColumnHeader));
    }


    /**
     * 读取csv文件返回一个原始的表格
     */
    public static List<List<String>> readLines(URL url) {
        CharSource charSource = Resources.asCharSource(url, Charsets.UTF_8);
        try{
            List<String> lines = charSource.readLines();
            List<List<String>> result = Lists.newArrayList();
            for(String line : lines) {
                List<String> strings = Lists.newArrayList(defaultCsvParser().parseLine(line));
                result.add(strings);
            }
            return result;
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
}
