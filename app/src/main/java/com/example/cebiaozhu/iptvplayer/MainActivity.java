package com.example.cebiaozhu.iptvplayer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<Map<String, String>> list;
    List<String> addressText;

    String finalAddress;
    String date;
    String date2;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        readCSV();
        listView = findViewById(R.id.list);

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.item, new String[]{"name", "address"}, new int[]{R.id.name, R.id.address});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                finalAddress = addressText.get(i);
                playback();
                //startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(addressText.get(i))));
            }
        });

    }

    void readCSV() {
        list = new ArrayList<>();
        addressText = new ArrayList<>();
        InputStream is = getResources().openRawResource(R.raw.address);
        Scanner in = new Scanner(is, "utf-8");
        while (in.hasNextLine()) {
            Map<String, String> addressArr = new HashMap<>();
            String[] lines = in.nextLine().split(",");
            addressArr.put("name", lines[0]);
            addressArr.put("address", lines[1]);
            addressText.add(lines[1]);
            list.add(addressArr);
        }
        return;
    }

    private void playback() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("IPTV")
                .setMessage("是否需要回看")
                //设置对话框的按钮
                .setNegativeButton("直播", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalAddress)));
                    }
                })
                .setPositiveButton("回看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showDate();
                    }
                })
                .setNeutralButton("取消播放", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void showDate() {
        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = calendar.get(Calendar.MONTH);//当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日
        //new一个日期选择对话框的对象,并设置默认显示时间为当前的年月日时间.
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                date = String.format("%04d%02d%02d", i, i1 + 1, i2);
                date2 = String.format("%04d%02d%02d", i, i1 + 1, i2 + 1);
                showTime();
            }
        }, year, month, day);
        dialog.show();
    }

    private void showTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time = String.format("%02d%02d", i, i1);
                finalAddress = finalAddress + String.format("?playseek=%s%s00-%s%s00", date, time, date2, time);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalAddress)));
            }
        }, hour, minute, true);
        dialog.show();
    }
}
