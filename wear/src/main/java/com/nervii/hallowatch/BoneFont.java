package com.nervii.hallowatch;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.io.Console;

public class BoneFont {

    private static com.nervii.hallowatch.BoneFont instance;
    private static Typeface typeface;

    public static com.nervii.hallowatch.BoneFont getInstance(Context context) {
        synchronized (com.nervii.hallowatch.BoneFont.class) {
            if (instance == null) {
                instance = new com.nervii.hallowatch.BoneFont();
                AssetManager am = context.getResources().getAssets();
                typeface = Typeface.createFromAsset(am, "bonesregular.ttf");
            }
            return instance;
        }
    }

    public Typeface getTypeFace() {
        return typeface;
    }
}