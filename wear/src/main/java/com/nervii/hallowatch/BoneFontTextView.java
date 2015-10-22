package com.nervii.hallowatch;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class BoneFontTextView extends TextView {

    public BoneFontTextView(Context context) {
        super(context);
        setTypeface(BoneFont.getInstance(context).getTypeFace());
    }

    public BoneFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(BoneFont.getInstance(context).getTypeFace());
    }

    public BoneFontTextView(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(BoneFont.getInstance(context).getTypeFace());
    }
}
