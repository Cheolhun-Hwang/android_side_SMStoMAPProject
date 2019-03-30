package com.hooneys.smstomapproject;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hooneys.smstomapproject.MyApplication.MyApp;

public class AddNumberActivity extends AppCompatActivity {
    private final String TAG = AddNumberActivity.class.getSimpleName();

    private int type;
    private EditText editText;
    private Button saveBtn;
    private boolean isSave;
    private String numbers; // 00000000000,00000000000, ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_number);

        type = getIntent().getIntExtra("type", -1);
        if(type == -1){
            Toast.makeText(getApplicationContext(), "잘못된 타입입니다.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        setAppBar(getSupportActionBar());
        init();
        setEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();

        numbers = getNumbers();
        editText.setText(numbers);
    }

    private void setAppBar(ActionBar bar){
        bar.setTitle("번호추가");
        bar.setDisplayHomeAsUpEnabled(true);
    }

    private void init(){
        isSave = false;

        editText = (EditText) findViewById(R.id.add_num_save_text);
        saveBtn = (Button) findViewById(R.id.add_num_save_btn);
    }

    private void setEvents(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSave){
                    isSave = true;
                    String s_num = editText.getText().toString()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll("\n", "");
                    saveNumbers(s_num);

                    if(type == 1){
                        //catch
                        MyApp.reloadCatchSMSNumber(getApplicationContext());
                    }else if (type == 2){
                        //send
                        MyApp.reloadSendSMSNumber(getApplicationContext());
                    }

                    showAlert();
                }
            }
        });
    }

    private String getNumbers(){
        String num = null;
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        if(type == 1){
            //catch number 가져와야됨
            num = pref.getString("catch_num", "");
        }else if (type == 2) {
            //send number 가져와야됨.
            num = pref.getString("send_num", "");
        }
        return num;
    }

    private void saveNumbers(String number){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (type == 1){
            //catch save
            editor.putString("catch_num", number);
        }else if (type ==2){
            //send save
            editor.putString("send_num", number);
        }
        editor.commit();

        numbers = number;
    }

    private void showAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(AddNumberActivity.this);
        alert.setTitle("번호 추가");
        alert.setMessage("정상적으로 번호가 추가되었습니다.");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSave = false;
                dialog.dismiss();
//                finish();
            }
        });
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                finish();
                break;
        }

        return true;
    }
}
