package com.jxavier.almanax;

import java.util.HashMap;

public class Utils {
    public static final String CHANNEL_ID = "my_channel_id";
    public static final String PREFS_NAME = "preferences";
    public static HashMap<String, String> monthConversion = new HashMap<String,String>(){{
        put("12", "Descendre");
        put("11", "Novamaire");
        put("10", "Octolliard");
        put("09", "Septange");
        put("08", "Fraouctor");
        put("07", "Joullier");
        put("06", "Juinssidor");
        put("05", "Maisial");
        put("04", "Aperirel");
        put("03", "Martalo");
        put("02", "Flovor");
        put("01", "Javian");
    }};
    public static String URL_EN = "https://api.jsonbin.io/b/5e3079f33d75894195e09cff";
    public static String URL_FR = "https://api.jsonbin.io/b/5e2824785df640720839f2e3";
}