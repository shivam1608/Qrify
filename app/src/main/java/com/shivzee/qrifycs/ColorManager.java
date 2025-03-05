package com.shivzee.qrifycs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ColorManager {
    private static final String PREF_NAME = "ColorPrefs";
    private static final String KEY_HIGHLIGHT_COLOR = "highlight_color";
    private static final String DEFAULT_COLOR = "#22EB5D";

    private static ColorManager instance;
    private final SharedPreferences prefs;

    private ColorManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static ColorManager getInstance(Context context) {
        if (instance == null) {
            instance = new ColorManager(context.getApplicationContext());
        }
        return instance;
    }

    public String getHighlightColor() {
        return prefs.getString(KEY_HIGHLIGHT_COLOR, DEFAULT_COLOR);
    }

    public void setHighlightColor(String colorHex) {
        prefs.edit().putString(KEY_HIGHLIGHT_COLOR, colorHex).apply();
    }

    public int getHighlightColorInt() {
        return Color.parseColor(getHighlightColor());
    }
} 