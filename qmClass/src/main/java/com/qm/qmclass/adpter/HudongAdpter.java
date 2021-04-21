package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qm.qmclass.R;
import com.qm.qmclass.model.Hudong;

import java.util.List;

public class HudongAdpter extends RecyclerView.Adapter<HudongAdpter.MyHolder> {
    private Context mcontext;
    private List<Hudong> mlist;
    private MyClickListener mListener;
    public HudongAdpter(Context context, List<Hudong> list, MyClickListener listener) {
        mcontext=context;
        mlist=list;
        mListener = listener;
    }

    @NonNull
    @Override
    public HudongAdpter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hudong_item,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HudongAdpter.MyHolder holder, final int position) {
        holder.tvName.setText(mlist.get(position).getName());
        holder.ivHudong.setImageDrawable(mcontext.getResources().getDrawable(mlist.get(position).getIconid()));
        holder.ivHudong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.myOnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (!mlist.isEmpty()){
            return mlist.size();
        }
        return 0;
    }
    /**
     * 自定义的ViewHolder
     */
    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivHudong;

        public MyHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivHudong = itemView.findViewById(R.id.iv_hudong);
        }
    }
    /**
     * 用于回调的抽象类
     */
    public static abstract class MyClickListener{
        public abstract void myOnClick(int position);
    }
}
