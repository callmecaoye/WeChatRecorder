package com.caoye.wechatrecorder.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.caoye.wechatrecorder.R;
import com.caoye.wechatrecorder.model.Recorder;

import java.util.List;

/**
 * Created by admin on 10/14/16.
 */
public class RecorderAdapter extends ArrayAdapter {
    private List<Recorder> mData;
    private Context mContext;

    private int mMinItemWidth;
    private int mMaxItemWidth;

    private LayoutInflater mLayoutInflater;

    public RecorderAdapter(Context context, List<Recorder> data) {
        super(context, -1, data);
        this.mData = data;
        mContext = context;

        mLayoutInflater = LayoutInflater.from(context);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
    }


    @Override
    public Recorder getItem(int position) {
        return mData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_recorder, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.seconds = (TextView) convertView.findViewById(R.id.id_recorder_time);
            viewHolder.length = convertView.findViewById(R.id.id_recorder_length);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.seconds.setText(Math.round(getItem(position).getTime()) + "\"");
        ViewGroup.LayoutParams layoutParams = viewHolder.length.getLayoutParams();
        layoutParams.width = (int)(mMinItemWidth + (mMaxItemWidth / 60f * getItem(position).getTime()));
        return convertView;
    }

    static class ViewHolder
    {
        TextView seconds;
        View length;
    }
}


