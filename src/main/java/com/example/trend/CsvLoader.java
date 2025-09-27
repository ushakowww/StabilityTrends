package com.example.trend;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CsvLoader {
    public static class DataSet {
        public final List<String> times;
        public final List<Double> trend1;
        public final List<Double> trend2;
        public final String trend1Name;
        public final String trend2Name;

        public DataSet(List<String> times, List<Double> trend1, List<Double> trend2,
                       String trend1Name, String trend2Name) {
            this.times = times; this.trend1 = trend1; this.trend2 = trend2;
            this.trend1Name = trend1Name; this.trend2Name = trend2Name;
        }
    }

    public static DataSet load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.isEmpty()) throw new IOException("Пустой файл");

        String header = stripBOM(lines.get(0));

        String sample = null;
        for (int i = 1; i < lines.size(); i++) {
            String ln = lines.get(i).trim();
            if (!ln.isEmpty()) { sample = ln; break; }
        }

        String delimiter = detectDelimiter(header, sample);

        String[] h = header.split(Pattern.quote(delimiter), -1);
        String t1Name = (h.length > 1 && !h[1].isBlank()) ? h[1].trim() : "Trend 1";
        String t2Name = (h.length > 2 && !h[2].isBlank()) ? h[2].trim() : "Trend 2";

        List<String> times = new ArrayList<>();
        List<Double> t1 = new ArrayList<>();
        List<Double> t2 = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String ln = lines.get(i).trim();
            if (ln.isEmpty()) continue;

            String[] p = ln.split(Pattern.quote(delimiter), -1);
            if (p.length < 3) continue;

            times.add(p[0].trim());
            Double v1 = parseNum(p[1]);
            Double v2 = parseNum(p[2]);
            if (v1 != null && v2 != null) {
                t1.add(v1);
                t2.add(v2);
            }
        }

        return new DataSet(times, t1, t2, t1Name, t2Name);
    }

    private static String stripBOM(String s) {
        if (s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF') return s.substring(1);
        return s;
    }

    private static String detectDelimiter(String header, String sample) {
        if (sample != null) {
            if (looksValid(sample, ";")) return ";";
            if (looksValid(sample, ",")) return ",";
        }
        int sc = header.split(";", -1).length;
        int cm = header.split(",", -1).length;
        if (sc >= 3 && cm < 3) return ";";
        if (cm >= 3 && sc < 3) return ",";
        if (sc >= 3 && cm >= 3) return (sc >= cm) ? ";" : ",";
        return ";";
    }

    private static boolean looksValid(String line, String delim) {
        String[] p = line.split(Pattern.quote(delim), -1);
        if (p.length < 3) return false;
        return parseNum(p[1]) != null && parseNum(p[2]) != null;
    }

    private static Double parseNum(String s) {
        try {
            return Double.parseDouble(s.trim().replace(" ", "").replace(',', '.'));
        } catch (Exception e) {
            return null;
        }
    }
}
