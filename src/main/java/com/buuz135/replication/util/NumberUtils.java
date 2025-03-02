package com.buuz135.replication.util;

import java.text.DecimalFormat;

public class NumberUtils {

    private static DecimalFormat formatterWithUnits = new DecimalFormat("####0.#");
    private static final String[] suffixes = {"", "K", "M", "B", "T", "Q", "Qi", "Sx", "Sp", "O"};

    /*public static String getFormatedBigNumber(double number) {
        if (number >= 1000000000) { //BILLION
            float numb = (float) (number / 1000_000_000F);
            return formatterWithUnits.format(numb) + "B";
        } else if (number >= 1000000) { //MILLION
            float numb = (float) (number / 1000000F);
            if (number > 100000000) numb = Math.round(numb);
            return formatterWithUnits.format(numb) + "M";
        } else if (number >= 1000) { //THOUSANDS
            float numb = (float) (number / 1000F);
            if (number > 100000) numb = Math.round(numb);
            return formatterWithUnits.format(numb) + "K";
        }
        return String.valueOf(number);
    }*/

    public static String getFormatedBigNumber(double value) {
        if (value < 1000) {
            return String.valueOf((int) Math.ceil(value));
        }

        int exp = (int) (Math.log(value) / Math.log(1000));
        if (exp >= suffixes.length) {
            return "Err";
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(value / Math.pow(1000, exp)) + suffixes[exp];
    }

    public static double customCeil(double value) {
        if (value == (long) value) {
            return value; // Already an integer
        }
        return (value > 0) ? (long) value + 1 : (long) value;
    }
}

