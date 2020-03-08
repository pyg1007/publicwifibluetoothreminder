package com.yonggeun.wifibluetoothreminder.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yonggeun.wifibluetoothreminder.R;

public class ContentDeleteDialog extends Dialog implements View.OnClickListener {

    private TextView Message;
    private int num;
    private Button Delete, Cancle;
    private CustomDialogListener customDialogListener;

    public interface CustomDialogListener {
        void PositiveClick();

        void NegativeClick();
    }

    public void setCustomDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    public ContentDeleteDialog(@NonNull Context context, int Num) {
        super(context);
        this.num = Num;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contentdeletedialog);

        UI();
    }

    public void UI() {
        Message = findViewById(R.id.Message);
        Message.setText(num + "개의 일정이 선택되었습니다.\n삭제하시겠습니까?");
        Message.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        Delete = findViewById(R.id.Delete);
        Cancle = findViewById(R.id.Cancle);

        Delete.setOnClickListener(this);
        Cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Delete:
                customDialogListener.PositiveClick();
                dismiss();
                break;
            case R.id.Cancle:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }
}
