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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yonggeun.wifibluetoothreminder.R;

public class ContentEditDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private TextView Title;
    private EditText Contents;
    private Button Edit, Cancle;

    private CustomDialogListener customDialogListener;

    public interface CustomDialogListener {
        void PositiveClick(String Contents);

        void NegativeClick();
    }

    public ContentEditDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void setCustomDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contenteditdialog);
        UI();
    }

    public void UI() {
        Title = findViewById(R.id.Title);
        Title.setText("일정 편집");
        Title.setGravity(Gravity.CENTER);
        Title.setTextColor(Color.BLACK);
        Contents = findViewById(R.id.Content);
        Contents.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        Edit = findViewById(R.id.Edit);
        Cancle = findViewById(R.id.Cancle);

        Edit.setOnClickListener(this);
        Cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Edit:
                if (Contents.getText().toString().length() > 0) {
                    customDialogListener.PositiveClick(Contents.getText().toString());
                    dismiss();
                } else {
                    Toast.makeText(context, "일정을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Cancle:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }
}
