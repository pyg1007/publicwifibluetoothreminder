package com.example.wifibluetoothreminder.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.wifibluetoothreminder.R;

import java.util.List;

public class Main_Content_Enrollment_Dialog extends Dialog implements View.OnClickListener {

    private Context context;

    private TextView NickName_Text, Title, Schedule;
    private EditText Schedule_Edit;
    private Button Enrollment, Cancle;
    private Spinner NickName_Spinner;
    private List<String> NickName;

    private int Position;

    public CustomDialogListener customDialogListener;

    public interface CustomDialogListener {
        void PositiveClick(String Content, int position);

        void NegativeClick();
    }

    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    public Main_Content_Enrollment_Dialog(@NonNull Context context, List<String> item) {
        super(context);
        this.context = context;
        this.NickName = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menudialog);
        UI();
    }

    public void UI() {
        NickName_Text = findViewById(R.id.NickName_Text);
        Title = findViewById(R.id.Title);
        Title.setTextColor(Color.BLACK);
        Title.setGravity(Gravity.CENTER);
        Title.setText("일정 등록");

        Schedule = findViewById(R.id.Schedule);

        Enrollment = findViewById(R.id.Enrollment);
        Cancle = findViewById(R.id.Cancle);

        Enrollment.setOnClickListener(this);
        Cancle.setOnClickListener(this);

        Schedule_Edit = findViewById(R.id.Schedule_Edit);
        Schedule_Edit.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

        NickName_Spinner = findViewById(R.id.NickName_Spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, NickName);
        NickName_Spinner.setAdapter(adapter);
        NickName_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Position = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Enrollment:
                if (Schedule_Edit.getText().length() > 0) {
                    customDialogListener.PositiveClick(Schedule_Edit.getText().toString(), Position);
                    dismiss();
                } else
                    Toast.makeText(context, "일정을 입력해주세요.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Cancle:
                customDialogListener.NegativeClick();
                cancel();
                break;
        }
    }
}
