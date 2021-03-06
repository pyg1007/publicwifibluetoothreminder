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

public class DetailContent extends Dialog implements View.OnClickListener {

    private String ContentsData;

    public CustomDialogListener customDialogListener;

    public interface CustomDialogListener {
        void PositiveClick();

        void NegativeClick();
    }

    public DetailContent(@NonNull Context context, String Data) {
        super(context);
        this.ContentsData = Data;
    }

    public void setListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detailcontentdialog);
        UI();
    }

    public void UI() {
        TextView title = findViewById(R.id.Title);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setText("일정 상세 보기");
        TextView content = findViewById(R.id.Content);
        content.setText(ContentsData);
        content.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        Button edit = findViewById(R.id.Edit);
        Button confirm = findViewById(R.id.Confirm);
        edit.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Edit:
                customDialogListener.PositiveClick();
                dismiss();
                break;
            case R.id.Confirm:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }
}
