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

    private TextView Title, Content;
    private Button Edit, Confirm;
    private String ContentsData;

    public CustomDialogListener customDialogListener;

    public interface CustomDialogListener{
        void PositiveClick();
        void NegativeClick();
    }

    public DetailContent(@NonNull Context context, String Data) {
        super(context);
        this.ContentsData = Data;
    }

    public void setListener(CustomDialogListener customDialogListener){
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

    public void UI(){
        Title = findViewById(R.id.Title);
        Title.setTextColor(Color.BLACK);
        Title.setGravity(Gravity.CENTER);
        Title.setText("일정 상세 보기");
        Content = findViewById(R.id.Content);
        Content.setText(ContentsData);
        Content.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

        Edit = findViewById(R.id.Edit);
        Confirm = findViewById(R.id.Confirm);
        Edit.setOnClickListener(this);
        Confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
