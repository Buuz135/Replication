package com.buuz135.replication.util;

import java.text.DecimalFormat;

public class NumberUtils {

    private static final ThreadLocal<DecimalFormat> BIG_NUMBER_FORMATTER =
            ThreadLocal.withInitial(() -> new DecimalFormat("#.#"));
    private static final String[] suffixes = {"", "K", "M", "B", "T", "Q", "Qi", "Sx", "Sp", "O"};

    public static String getFormatedBigNumber(double value) {
        if (value < 1000) {
            return String.valueOf((int) Math.ceil(value));
        }

        int exp = (int) (Math.log(value) / Math.log(1000));
        if (exp >= suffixes.length) {
            return "Err";
        }

        return BIG_NUMBER_FORMATTER.get().format(value / Math.pow(1000, exp)) + suffixes[exp];
    }

    public static double customCeil(double value) {
        if (value == (long) value) {
            return value; // Already an integer
        }
        return (value > 0) ? (long) value + 1 : (long) value;
    }
}
