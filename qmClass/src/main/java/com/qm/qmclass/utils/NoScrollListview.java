package com.qm.qmclass.utils;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
* activity管理
*/
public class NoScrollListview extends ListView {
    public NoScrollListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
