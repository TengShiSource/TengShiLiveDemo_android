package com.qm.qmclass.adpter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qm.qmclass.R;
import com.qm.qmclass.model.ChatContent;

import java.util.List;

public class DanmuContentAdpter extends ArrayAdapter {
    public DanmuContentAdpter(Context context, int resource,List<String> list) {
        super(context, resource, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String str =(String)getItem(position);//通过position获取当前要赋值的内容
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.danmu_content_item, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv_content);
        tv.setText(str);//设置TextView中的字符串
        tv.setTextSize(13);//设置大小
        SpannableStringBuilder builder = new SpannableStringBuilder(tv.getText().toString());
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(Color.parseColor("#28b28b"));
        builder.setSpan(greenSpan, 0, str.indexOf(":")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(builder);
        return convertView;
    }

}
