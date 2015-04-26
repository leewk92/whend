package com.whend.android;


import java.util.ArrayList;
import java.util.Calendar;
 






import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
 

public class CalendarView extends CTCalendarView{
	

    private Oneday basisDay;
    private int during;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
    	
    	
    	
       /* final String CONTENT_URI = "content://com.android.calendar";
        Uri uri = Uri.parse(CONTENT_URI + "/calendars");
        Cursor c = getContentResolver().query(
                     uri, new String[] { "_id" }, 
                     "selected=?", new String[] { "1" }, null);
        if(c == null || !c.moveToFirst()) {
            // 시스템에 캘린더가 존재하지 않음(계정이 등록되어 있지 않음) 오류 처리 필요
        }
        final int id = c.getInt(c.getColumnIndex("_id"));
        c.close();
        
        uri = Uri.parse(CONTENT_URI + "/events");
        // 이전 파트에서 집어 넣었던 일정을 검색한다.
        c = getContentResolver().query(
                     uri, new String[] { "_id", "dtstart" }, // 가져올 필드
                     "calendar_id=? AND title=?",
                     new String[] { String.valueOf(id), "birthday" }, null);
        if(c == null || !c.moveToFirst()) {
            // 실패했거나 검색 결과가 없음. 오류처리 필요
        }
        // 일정의 시작 시간을 Date타입인 dtstart에 저장한다.
        final Date dtstart = new Date(c.getLong(c.getColumnIndex("dtstart")));
        c.close();
        */
        
        setTitle("원하는 날짜를 선택해 주세요.");
        try {
			initialize();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
        basisDay = new Oneday(this);
        
        Intent intent = getIntent();
        int[] b = intent.getIntArrayExtra("basisDay");
        during = intent.getIntExtra("during", 0);
        if(b != null){
            basisDay.setYear(b[0]);
            basisDay.setMonth(b[1]);
            basisDay.setDay(b[2]);
        } else {
            Calendar cal = Calendar.getInstance();
            basisDay.setYear(cal.get(Calendar.YEAR));
            basisDay.setMonth(cal.get(Calendar.MONTH));
            basisDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
        }
    }
     
    @Override
    protected void onTouched(Oneday touchedDay){
         
        if(isInside(touchedDay, basisDay, during)){
            Calendar cal = Calendar.getInstance();
            cal.set(basisDay.getYear(), basisDay.getMonth(), basisDay.getDay());
            cal.add(Calendar.DAY_OF_MONTH,during);
            Toast.makeText(this, (cal.get(Calendar.MONTH) + 1)+"월"+
                    cal.get(Calendar.DAY_OF_MONTH) + "일 이후 선택 가능", Toast.LENGTH_SHORT).show();
            return;
        }
         
        final String year = String.valueOf(touchedDay.getYear());
        final String month = doubleString(touchedDay.getMonth() + 1);
        final String date = doubleString(touchedDay.getDay());
        
        AlertDialog.Builder builder =  new AlertDialog.Builder(CalendarView.this);
        builder.setTitle("다음 날짜로 설정하시겠습니까?");
        builder.setMessage(year + "." + month + "." + date + "(" + touchedDay.getDayOfWeekKorean()+")");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent();
                intent.putExtra("date", year + "." + month + "." + date);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setNegativeButton("아니오", null);
        builder.show();
    }
     
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.calendar_menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { 
        //오늘
        case R.id.menuitem_calendar_0:
            gotoToday();
            return true;
        }
        return false;
    }
}
