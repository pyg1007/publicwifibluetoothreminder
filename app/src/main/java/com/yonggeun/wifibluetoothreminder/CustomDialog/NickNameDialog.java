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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yonggeun.wifibluetoothreminder.R;

public class NickNameDialog extends Dialog implements View.OnClickListener {


    private TextView Title;
    private EditText editNickName;
    private Button PositiveButton;
    private Button NegativeButton;

    public CustomDialogListener customDialogListener;

    public interface CustomDialogListener {
        void PositiveClick(String NickName);

        void NegativeClick();
    }

    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    public NickNameDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_list_dialog);
        UI();
    }

    public void UI() {
        Title = findViewById(R.id.Title);
        Title.setGravity(Gravity.CENTER);
        Title.setText("기기 등록");
        Title.setTextColor(Color.BLACK);
        editNickName = findViewById(R.id.NickName);
        editNickName.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

        PositiveButton = findViewById(R.id.Enrollment);
        NegativeButton = findViewById(R.id.Cancle);

        PositiveButton.setOnClickListener(this);
        NegativeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Enrollment:
                customDialogListener.PositiveClick(editNickName.getText().toString());
                dismiss();
                break;
            case R.id.Cancle:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }
}
