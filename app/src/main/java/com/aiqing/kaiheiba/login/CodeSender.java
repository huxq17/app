package com.aiqing.kaiheiba.login;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

public class CodeSender extends CountDownTimer {
    TextView textView;
    FinishedListener listener;
    private int textColor = Color.parseColor("#70e9eb");
    private int hintColor = Color.parseColor("#1d2e43");

    public void setFinishedListener(FinishedListener listener) {
        this.listener = listener;
    }

    public interface FinishedListener {
        void onFinished();
    }

    public CodeSender(TextView textView) {
        super(60000, 1000);
        this.textView = textView;
    }

    public void startWait() {
        setHasSentCode(true);
        start();
    }

    public void sendFailed() {
        onFinish();
    }

    private CharSequence originalString;

    private void setHasSentCode(boolean sent) {
        if (sent) {
//            textView.setText(originalString);
        } else {
            originalString = textView.getText();
            textView.setText("获取中...");
        }
//        textView.setTextColor(hintColor);
        textView.setEnabled(false);
    }

    public void codeSent() {
        setHasSentCode(false);
    }

    @Override
    public void onFinish() {
        textView.setEnabled(true);
        textView.setText(originalString);
        if (listener != null) {
            listener.onFinished();
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        textView.setText("重新发送(" + millisUntilFinished / 1000 + ")");
    }

}
