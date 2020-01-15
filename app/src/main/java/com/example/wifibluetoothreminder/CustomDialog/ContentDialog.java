package com.example.wifibluetoothreminder.CustomDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wifibluetoothreminder.R;

public class ContentDialog extends Dialog implements View.OnClickListener{

    private Context context;

    private EditText Content;
    private Button PositiveButton;
    private Button NegativeButton;

    public ContentDialog.CustomDialogListener customDialogListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_dialog);
        UI();
    }

    public void UI(){
        Content = findViewById(R.id.Content);
        PositiveButton = findViewById(R.id.Confirm);
        NegativeButton = findViewById(R.id.Cancle);

        PositiveButton.setOnClickListener(this);
        NegativeButton.setOnClickListener(this);
    }


    public interface CustomDialogListener{
        void PositiveClick(String Contents);
        void NegativeClick();
    }

    public void setDialogListener(ContentDialog.CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    public ContentDialog(@NonNull Context context) {
        super(context);
        this.context= context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Confirm:
                customDialogListener.PositiveClick(Content.getText().toString());
                dismiss();
                break;
            case R.id.Cancle:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }
}
