package com.example.wifibluetoothreminder.CustomDialog;

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

import com.example.wifibluetoothreminder.R;

public class ContentDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private TextView textView;
    private EditText Content;
    private Button PositiveButton;
    private Button NegativeButton;

    public CustomDialogListener customDialogListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content_dialog);
        UI();
    }

    public void UI() {
        Content = findViewById(R.id.Content);
        textView = findViewById(R.id.Content_Dialog_Title);
        textView.setText("일정등록");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);

        PositiveButton = findViewById(R.id.Enrollment);
        NegativeButton = findViewById(R.id.Cancle);

        PositiveButton.setOnClickListener(this);
        NegativeButton.setOnClickListener(this);
    }

    public interface CustomDialogListener {
        void PositiveClick(String Contents);

        void NegativeClick();
    }

    public void setDialogListener(ContentDialog.CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    public ContentDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Enrollment:
                if (Content.getText().toString().length() > 0) {
                    customDialogListener.PositiveClick(Content.getText().toString());
                    dismiss();
                } else {
                    Toast.makeText(context, "일정을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Cancle:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }
}
