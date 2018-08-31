package com.ramz.igar.amu.model;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.ramz.igar.amu.R;

public class IconTextView extends android.support.v7.widget.AppCompatTextView {

    private Context context;

    public IconTextView(Context context) {
        super(context);
        this.context = context;
        createView();
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        createView();
    }

    private void createView(){
        setGravity(Gravity.CENTER);
        Typeface font = Typeface.createFromAsset(context.getAssets(),"font_awesome.ttf");
        setTypeface(font);

    }
}
