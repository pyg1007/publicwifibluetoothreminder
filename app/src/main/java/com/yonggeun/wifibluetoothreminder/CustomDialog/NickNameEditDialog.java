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

public class NickNameEditDialog extends Dialog implements View.OnClickListener {

    private TextView Title;
    private Button Change, Cancle;
    private EditText NickName_editText;

    public CustomDialogListener customDialogListener;

    public interface CustomDialogListener {
        void PositiveClick(String NickName);
        void NegativeClick();
    }

    public NickNameEditDialog(@NonNull Context context) {
        super(context);
    }

    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.nick_name_edit_dialog);
        UI();
    }

    public void UI(){
        NickName_editText = findViewById(R.id.NickName);
        NickName_editText.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        Title = findViewById(R.id.Title);
        Title.setTextColor(Color.BLACK);
        Title.setText("별명 변경");
        Title.setGravity(Gravity.CENTER);

        Change = findViewById(R.id.Change);
        Cancle = findViewById(R.id.Cancle);

        Change.setOnClickListener(this);
        Cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Change:
                customDialogListener.PositiveClick(NickName_editText.getText().toString());
                dismiss();
                break;
            case R.id.Cancle:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }


}
