package de.elbe5.base.data;

import de.elbe5.base.log.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvFile {
    private static final char QuoteChar = '"';
    private static final char Delimiter = ';';

    protected String path;
    protected List<CsvLine> csvLines = new ArrayList<>();

    public CsvFile(String path) {
        this.path = path;
    }

    public List<CsvLine> getCsvLines() {
        return csvLines;
    }

    public void readFile() {
        try {
            csvLines.clear();
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty())
                    return;
                CsvLine csvLine = new CsvLine();
                List<String> cells = ParseCsvLine(line);
                csvLine.Key = cells.get(0);
                for (int i = 1; i < cells.size(); i++)
                    csvLine.Values.add(cells.get(i));
                csvLines.add(csvLine);
            }
        } catch (Exception e) {
            Log.error("The file could not be read:" + e.getMessage());
        }
    }

    private static List<String> ParseCsvLine(String line) {
        char[] chars = line.toCharArray();
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        char ch;
        for (int i = 0; i < chars.length; i++) {
            ch = chars[i];
            if (sb.length() == 0 && !inQuote) {
                if (ch == QuoteChar)
                    inQuote = true;
                else if (ch == Delimiter) {
                    list.add(sb.toString());
                    sb = new StringBuilder();
                } else if (!Character.isWhitespace(ch)) {
                    sb.append(ch);
                }
            } else
                switch (ch) {
                    case Delimiter:
                        if (inQuote)
                            sb.append(Delimiter);
                        else {
                            list.add(sb.toString());
                            sb = new StringBuilder();
                        }
                        break;
                    case QuoteChar:
                        if (inQuote) {
                            if (i + 1 < chars.length && chars[i + 1] == QuoteChar) {
                                i++;
                                sb.append(QuoteChar);
                            } else
                                inQuote = false;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    default:
                        sb.append(ch);
                        break;
                }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list;
    }

    public void writeFile() {
        try {
            FileOutputStream fout = new FileOutputStream(path, false);
            OutputStreamWriter writer = new OutputStreamWriter(fout, StandardCharsets.UTF_8);
            for (CsvLine csvLine : csvLines) {
                writer.write("\"");
                writer.write(csvLine.Key);
                writer.write("\"");
                for (String value : csvLine.Values) {
                    writer.write(";");
                    writer.write("\"");
                    //mask quotes
                    writer.write(value.replaceAll("\"", "\"\""));
                    writer.write("\"");
                }
                writer.write(System.lineSeparator());
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.error("The file could not be written:" + e.getMessage());
        }
    }

    public static class CsvLine {
        public String Key;
        public List<String> Values = new ArrayList<>();
    }
}
