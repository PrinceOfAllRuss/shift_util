import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileManager {
    String resultPath;
    String prefix;
    boolean append;
    private final StringBuilder builderForStrings = new StringBuilder();
    private final StringBuilder builderForIntegers = new StringBuilder();
    private final StringBuilder builderForFloats = new StringBuilder();
    private boolean stringFirstTime = true;
    private double[] stringStat = {0, 0, 0}; // countString, minString, maxString
    private boolean intFirstTime = true;
    private double[] intStat = {0, 0, 0, 0}; // countInt, minInt, maxInt, sumInt
    private boolean floatFirstTime = true;
    private double[] floatStat = {0, 0, 0, 0}; // countFloat, minFloat, maxFloat, sumFloat

    public FileManager(
            String resultPath,
            String prefix,
            boolean append
    ) {
        this.resultPath = resultPath;
        this.prefix = prefix;
        this.append = append;
    }

    private void stat(String el) {
        if (stringFirstTime) {
            for (int i = 1; i < stringStat.length; i++) {
                stringStat[i] = el.length();
            }
            stringStat[0] += 1;
            stringFirstTime = false;
        } else {
            stringStat[0] += 1;
            stringStat[1] = Math.min(stringStat[1], el.length());
            stringStat[2] = Math.max(stringStat[2], el.length());
        }
    }

    private void stat(int el) {
        if (intFirstTime) {
            for (int i = 1; i < intStat.length; i++) {
                intStat[i] = el;
            }
            intStat[0] += 1;
            intFirstTime = false;
        } else {
            intStat[0] += 1;
            intStat[1] = Math.min(intStat[1], el);
            intStat[2] = Math.max(intStat[2], el);
            intStat[3] += el;
        }
    }

    private void stat(float el) {
        if (floatFirstTime) {
            for (int i = 1; i < floatStat.length; i++) {
                floatStat[i] = el;
            }
            floatStat[0] += 1;
            floatFirstTime = false;
        } else {
            floatStat[0] += 1;
            floatStat[1] = Math.min(floatStat[1], el);
            floatStat[2] = Math.max(floatStat[2], el);
            floatStat[3] += el;
        }
    }

    private void lineProcessing(String line) {
        try {
            int num = Integer.parseInt(line);
            builderForIntegers.append(line).append("\n");
            stat(num);
        }
        catch (NumberFormatException intExp) {
            try {
                float num = Float.parseFloat(line);
                builderForFloats.append(line).append("\n");
                stat(num);
            } catch (NumberFormatException floatExp) {
                builderForStrings.append(line).append("\n");
                stat(line);
            }
        }
    }

    private void writeToFile(StringBuilder builder, String fileName) {
        if (!builder.isEmpty()) {
            File directory = new File(resultPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    System.out.println("Failed to create directory: " + resultPath);
                    return;
                }
            }

            String path = resultPath + "/" + prefix + fileName;
            File stringFile = new File(path);
            try (FileOutputStream stringStream = new FileOutputStream(stringFile, append)) {
                byte[] buffer = builder.toString().getBytes();
                stringStream.write(buffer, 0, buffer.length);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void filterFile(String fileName) {
        try (Stream<String> lines = Files.lines(Path.of(fileName))) {
            lines.forEach(this::lineProcessing);
        } catch (IOException e) {
            System.out.println(e);
        }

        writeToFile(builderForStrings, "string.txt");
        writeToFile(builderForIntegers, "integers.txt");
        writeToFile(builderForFloats, "floats.txt");
    }

    public Number convertDoubleToFloatIfPossible(double d) {
        float f = (float) d;
        double d2 = (double) f;
        double epsilon = 1e-6;
        if (Math.abs(d - d2) < epsilon) {
            return f;
        } else {
            return d;
        }
    }

    public void printStat(StatType statType) {
        if (statType == StatType.NO_STAT) return;

        if (statType == StatType.SHOR) {
            System.out.println("Number of strings: " + stringStat[0]);
            System.out.println("Number of integers: " + intStat[0]);
            System.out.println("Number of floats: " + floatStat[0]);
        }

        if (statType == StatType.FULL) {
            System.out.println("Number of strings: " + stringStat[0]);
            System.out.println("Minimum strings length: " + stringStat[1]);
            System.out.println("Maximum strings length: " + stringStat[2]);
            System.out.println();

            System.out.println("Number of integers: " + intStat[0]);
            System.out.println("Minimum integers: " + intStat[1]);
            System.out.println("Maximum integers: " + intStat[2]);
            System.out.println("Sum of integers: " + intStat[3]);
            double intAverage = intStat[3] / intStat[0];
            System.out.println("Average of integers: " + intAverage);
            System.out.println();

            System.out.println("Number of floats: " + floatStat[0]);
            System.out.println("Minimum floats: " + convertDoubleToFloatIfPossible(floatStat[1]));
            System.out.println("Maximum floats: " + convertDoubleToFloatIfPossible(floatStat[2]));
            System.out.println("Sum of floats: " + convertDoubleToFloatIfPossible(floatStat[3]));
            double floatsAverage = floatStat[3] / floatStat[0];
            System.out.println("Average of floats: " + convertDoubleToFloatIfPossible(floatsAverage));
            System.out.println();
        }
    }
}
