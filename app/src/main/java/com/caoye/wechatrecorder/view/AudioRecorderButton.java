package com.caoye.wechatrecorder.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.caoye.wechatrecorder.R;

/**
 * Created by admin on 10/12/16.
 */
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_RECORDING = 1;
    private static final int STATE_WANT_TO_CANCEL = -1;
    private static final int DISTANCE_Y_CANCEL = 50;

    private int mCurState = STATE_NORMAL;

    private boolean isRecording = false;

    private DialogManager mDialogManager;
    private AudioManager mAudioManager;

    private float mTime;
    //是否出发longClick
    private boolean mReady;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDialogManager = new DialogManager(context);
        String dir = Environment.getExternalStorageDirectory() + "/Audios";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音完成后回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filepath);
    }
    private AudioFinishRecorderListener mListener;
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        this.mListener = listener;
    }

    /**
     * 获取音量大小的Runnable
     */
    private Runnable mGetVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            while(isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGE = 0X111;
    private static final int MSG_DIALOG_DISMISS = 0X112;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVolumeRunnable).start();
                    break;

                case MSG_VOICE_CHANGE:
                    mDialogManager.updateVolume(mAudioManager.getVolume(7));
                    break;

                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
            }
        }
    };

    // Callback method
    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                // OnLongClick未触发
                if (!mReady) {}

                // Audio.prepare()未完成 || 录音时间过短
                if (!isRecording || mTime < 0.6f) {
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1300);
                } else if (mCurState == STATE_RECORDING) { //正常录制结束
                    mDialogManager.dismissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime, mAudioManager.getCurFilePath());
                    }
                } else if (mCurState == STATE_WANT_TO_CANCEL) {
                    mDialogManager.dismissDialog();
                    mAudioManager.cancel();
                }

                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * reset to Default state
     */
    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        mTime = 0;
        mReady = false;
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }

        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }

    private void changeState(int state) {
        if (mCurState == state) return;

        mCurState = state;

        switch (state) {

            case STATE_NORMAL:
                setBackgroundResource(R.drawable.btn_recorder_normal);
                setText(R.string.str_btn_normal);
                break;

            case STATE_RECORDING:
                setBackgroundResource(R.drawable.btn_recording);
                setText(R.string.str_btn_recording);

                if (isRecording) {
                    mDialogManager.recording();
                }
                break;

            case STATE_WANT_TO_CANCEL:
                setBackgroundResource(R.drawable.btn_recording);
                setText(R.string.str_btn_want_cancel);

                mDialogManager.cancel();
                break;
        }
    }
}
