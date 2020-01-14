package com.example.wifibluetoothreminder.CustomDialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.wifibluetoothreminder.R;

public class MainListDialog extends Dialog implements View.OnClickListener{

    private Context context;

    private EditText editNickName;
    private Button PositiveButton;
    private Button NegativeButton;

    public CustomDialogListener customDialogListener;

    public interface CustomDialogListener{
        void PositiveClick(String NickName);
        void NegativeClick();
    }

    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    public MainListDialog(@NonNull Context context) {
        super(context);
        this.context= context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_dialog);
        UI();
    }

    public void UI(){
        editNickName = findViewById(R.id.NickName);

        PositiveButton = findViewById(R.id.Change);
        NegativeButton = findViewById(R.id.Cancle);

        PositiveButton.setOnClickListener(this);
        NegativeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Change:
                if(!editNickName.getText().equals("")) {
                    customDialogListener.PositiveClick(editNickName.getText().toString());
                    dismiss();
                }else{
                    Toast.makeText(context, "별명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Cancle:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }
}
