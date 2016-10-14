package com.caoye.wechatrecorder.manager;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caoye.wechatrecorder.R;

/**
 * Created by admin on 10/12/16.
 */
public class DialogManager {

    private Context mContext;

    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVolume;
    private TextView mLabel;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog, null);
        mDialog.setContentView(view);

        mIcon = (ImageView) mDialog.findViewById(R.id.id_recorder_dialog_icon);
        mVolume = (ImageView) mDialog.findViewById(R.id.id_recorder_dialog_volume);
        mLabel = (TextView) mDialog.findViewById(R.id.id_recorder_dialog_label);
        mDialog.show();
    }

    public void dismissDialog() {
        if (mDialog == null || !mDialog.isShowing()) return;
        mDialog.dismiss();
        mDialog = null;
    }

    public void cancel() {
        if (mDialog == null || !mDialog.isShowing()) return;

        mIcon.setVisibility(View.VISIBLE);
        mVolume.setVisibility(View.GONE);
        mLabel.setVisibility(View.VISIBLE);

        mIcon.setImageResource(R.drawable.cancel);
        mLabel.setText(R.string.str_dialog_want_cancel);
    }

    public void recording() {
        if (mDialog == null || !mDialog.isShowing()) return;

        mIcon.setVisibility(View.VISIBLE);
        mVolume.setVisibility(View.VISIBLE);
        mLabel.setVisibility(View.VISIBLE);

        mIcon.setImageResource(R.drawable.recorder);
        mLabel.setText(R.string.str_dialog_recording);
    }

    public void tooShort() {
        if (mDialog == null || !mDialog.isShowing()) return;

        mIcon.setVisibility(View.VISIBLE);
        mVolume.setVisibility(View.GONE);
        mLabel.setVisibility(View.VISIBLE);

        mIcon.setImageResource(R.drawable.voice_to_short);
        mLabel.setText(R.string.str_dialog_too_short);
    }

    /**
     * 通过level更新volume图片
     * @param level 1-7
     */
    public void updateVolume(int level) {
        if (mDialog == null || !mDialog.isShowing()) return;

        //mIcon.setVisibility(View.VISIBLE);
        //mVolume.setVisibility(View.VISIBLE);
        //mLabel.setVisibility(View.VISIBLE);

        int resId = mContext.getResources().getIdentifier("v"+level, "drawable", mContext.getPackageName());
        mVolume.setImageResource(resId);
    }
}
