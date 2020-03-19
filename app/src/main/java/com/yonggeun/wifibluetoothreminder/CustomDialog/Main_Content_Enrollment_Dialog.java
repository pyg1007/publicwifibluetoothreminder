package com.yonggeun.wifibluetoothreminder.CustomDialog;

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

import com.yonggeun.wifibluetoothreminder.R;

import java.util.List;

public class Main_Content_Enrollment_Dialog extends Dialog implements View.OnClickListener {

    private Context context;

    private EditText Schedule_Edit;
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
        TextView nickName_Text = findViewById(R.id.NickName_Text);
        TextView title = findViewById(R.id.Title);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setText("일정 등록");

        TextView schedule = findViewById(R.id.Schedule);

        Button enrollment = findViewById(R.id.Enrollment);
        Button cancle = findViewById(R.id.Cancle);

        enrollment.setOnClickListener(this);
        cancle.setOnClickListener(this);

        Schedule_Edit = findViewById(R.id.Schedule_Edit);
        Schedule_Edit.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        Spinner nickName_Spinner = findViewById(R.id.NickName_Spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, NickName);
        nickName_Spinner.setAdapter(adapter);
        nickName_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
