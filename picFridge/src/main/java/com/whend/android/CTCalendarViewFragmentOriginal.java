package com.whend.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TableRow.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
 
// 원본 : http://funpython.com/blog/59?category=2
// 수정 : 한수댁
public class CTCalendarViewFragmentOriginal extends Fragment implements OnClickListener {
     
    private Calendar rightNow;
    private GregorianCalendar gCal;
    private int iYear = 0;
    private int iMonth = 0;  
 
    private int startDayOfweek = 0;
    private int maxDay = 0;
    private int oneday_width =0;
    private int oneday_height =0;
 
    ArrayList<String> daylist; //일자 목록을 가지고 있는다. 1,2,3,4,.... 28?30?31? 
    ArrayList<String> actlist; //일자에 해당하는 활동내용을 가지고 있는다.
 
    TextView aDateTxt;
    View rootview;
    private int dayCnt;
    private int mSelect = -1;
     
    public static CTCalendarViewFragment newInstance(){
    	CTCalendarViewFragment fragment = new CTCalendarViewFragment();
    	return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState){
    	rootview= inflater.inflate(R.layout.calendarview, parent,false);
    	initialize();
    	
    	
    	return rootview;
    }
    
    
   /* @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        FragmentManager mFragmentManager = getFragmentManager();
        CTCalendarViewFragment ct = new CTCalendarViewFragment();
        mFragmentManager
                .beginTransaction()
                .replace(R.id.fl_activity_main,
                        ct, "myProjectListFragment")
                .commit();
         
        initialize();
    }*/
    /*
        
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		   //inflater.inflate(R.menu.pf03__itemlog, menu);
		    ((PF01_Main)getActivity()).setActionBarTitle(" Calendar");
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.refresh :
	    		
                //refresh_list(rootView, db);
                
                Toast toast = Toast.makeText(getActivity(), "Item Refreshed", Toast.LENGTH_SHORT); 
                toast.show(); 
                
                break;
                
	    }
	    return true;
    }
    
    
    protected void initialize(){
    	//getActivity().setContentView(R.layout.calendarview);
        syncDeviceCalendar(); 
        rightNow = Calendar.getInstance();
        gCal = new GregorianCalendar();
        iYear = rightNow.get(Calendar.YEAR);
        iMonth = rightNow.get(Calendar.MONTH);
         
        Button btnMPrev = (Button)rootview.findViewById(R.id.btn_calendar_prevmonth);
        btnMPrev.setOnClickListener(this);
        Button btnMNext = (Button)rootview.findViewById(R.id.btn_calendar_nextmonth);
        btnMNext.setOnClickListener(this);
 
        btnMPrev.setText("이전");
        btnMNext.setText("다음");
 
        aDateTxt = (TextView)rootview.findViewById(R.id.CalendarMonthTxt);
 
        makeCalendardata(iYear, iMonth);
    }
 
 
  //달력의 일자를 표시한다. 
    private void printDate(String thisYear, String thisMonth)
    {
 
        if(thisMonth.length() == 1) {
            aDateTxt.setText(String.valueOf(thisYear) + "." + "0"+ thisMonth);
        }
        else{
            aDateTxt.setText(String.valueOf(thisYear) + "." + thisMonth);
        }
    }
 
  //달력에 표시할 일자를 배열에 넣어 구성한다. 
    private void makeCalendardata(int thisYear, int thisMonth)
    {
        printDate(String.valueOf(thisYear),String.valueOf(thisMonth+1));
         
        rightNow.set(thisYear, thisMonth, 1);
        gCal.set(thisYear, thisMonth, 1);
        startDayOfweek = rightNow.get(Calendar.DAY_OF_WEEK);
 
 
        maxDay = gCal.getActualMaximum ((Calendar.DAY_OF_MONTH));
        if(daylist==null)daylist = new ArrayList<String>();
        daylist.clear();
 
        if(actlist==null)actlist = new ArrayList<String>();
        actlist.clear();
 
        daylist.add("일");actlist.add("");
        daylist.add("월");actlist.add("");
        daylist.add("화");actlist.add("");
        daylist.add("수");actlist.add("");
        daylist.add("목");actlist.add("");
        daylist.add("금");actlist.add("");
        daylist.add("토");actlist.add("");
 
        if(startDayOfweek != 1) {
            gCal.set(thisYear, thisMonth-1, 1);
            int prevMonthMaximumDay = (gCal.getActualMaximum((Calendar.DAY_OF_MONTH))+2);
            for(int i=startDayOfweek;i>1;i--){
                daylist.add(Integer.toString(prevMonthMaximumDay-i));
                actlist.add("p");
            }
        }
 
        for(int i=1;i<=maxDay;i++) //일자를 넣는다.
        {
            daylist.add(Integer.toString(i));
            actlist.add("");
        }
 
 
        int dayDummy = (startDayOfweek-1)+maxDay;
        if(dayDummy >35)
        {
            dayDummy = 42 - dayDummy;
        }else{
            dayDummy = 35 - dayDummy;
        }
        
      //자투리..그러니까 빈칸을 넣어 달력 모양을 이쁘게 만들어 준다.
        if(dayDummy != 0)
        {
            for(int i=1;i<=dayDummy;i++) 
            {
                daylist.add(Integer.toString(i));
                actlist.add("n");
            }
        }
 
        makeCalendar();
    }
 
 
 
    private void makeCalendar()
    {
        final Oneday[] oneday = new Oneday[daylist.size()];
        final Calendar today = Calendar.getInstance();
        TableLayout tl =(TableLayout)rootview.findViewById(R.id.tl_calendar_monthly);
        tl.removeAllViews();
 
        dayCnt = 0;
        int maxRow = ((daylist.size() > 42)? 7:6);
        int maxColumn = 7;
        
 
        oneday_width = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth();
        oneday_height =getActivity().getWindow().getWindowManager().getDefaultDisplay().getHeight();
 
        oneday_height = ((((oneday_height >= oneday_width)?oneday_height:oneday_width) - tl.getTop()) / (maxRow+1))-15;	//revised, original : -10이 뒤에 붙어있었음.
        oneday_width = (oneday_width / maxColumn)+1;
 
 
 
        int daylistsize =daylist.size()-1;
//        syncDeviceCalendar();
        for(int i=1;i<=maxRow;i++ )
        {
            TableRow tr = new TableRow(getActivity());
            tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            for(int j=1;j<=maxColumn;j++)
            {
                //calender_oneday를 생성해 내용을 넣는다. 
                oneday[dayCnt] = new Oneday(getActivity().getApplicationContext());
 
                //요일별 색상 정하기 
                if((dayCnt % 7) == 0){
                    oneday[dayCnt].setTextDayColor(Color.RED);
                } else if((dayCnt % 7) == 6){
                    oneday[dayCnt].setTextDayColor(Color.GRAY);
                } else {
                    oneday[dayCnt].setTextDayColor(Color.BLACK);
                }
                 
                //요일 표시줄 설정
                if(dayCnt >= 0 && dayCnt < 7)
                {
                    oneday[dayCnt].setBgDayPaint(Color.WHITE); //배경색상 
                    oneday[dayCnt].setTextDayTopPadding(1); //일자표시 할때 top padding: original 8
                    oneday[dayCnt].setTextDayColor(Color.DKGRAY); //일자의 글씨 색상 
                    oneday[dayCnt].setTextDaySize(20); //일자의 글씨크기 
                    oneday[dayCnt].setLayoutParams(new LayoutParams(oneday_width,35)); //일자 컨트롤 크기 
                    oneday[dayCnt].isToday = false;
                     
                }else{
                     
                    oneday[dayCnt].isToday = false;
                    oneday[dayCnt].setDayOfWeek(dayCnt%7 + 1);
                    oneday[dayCnt].setDay(Integer.valueOf(daylist.get(dayCnt)).intValue());
                    oneday[dayCnt].setTextActcntSize(14);		//original : 14
                    oneday[dayCnt].setTextActcntColor(Color.BLACK);
                    oneday[dayCnt].setTextActcntTopPadding(18);			//original : 18
                    oneday[dayCnt].setBgSelectedDayPaint(Color.rgb(0, 162, 232));
                    oneday[dayCnt].setBgTodayPaint(Color.LTGRAY);
                    oneday[dayCnt].setBgActcntPaint(Color.rgb(251, 247, 176));
                    oneday[dayCnt].setLayoutParams(new LayoutParams(oneday_width,oneday_height));
                     
                    //이전 달 블럭 표시
                    if(actlist.get(dayCnt).equals("p")){
                         oneday[dayCnt].setTextDaySize(18);
                         actlist.set(dayCnt, "");
                         oneday[dayCnt].setTextDayTopPadding(-4);
                          
                         if(iMonth - 1 < Calendar.JANUARY){
                             oneday[dayCnt].setMonth(Calendar.DECEMBER);
                             oneday[dayCnt].setYear(iYear - 1);
                         }  else {
                             oneday[dayCnt].setMonth(iMonth - 1);
                             oneday[dayCnt].setYear(iYear);
                         }
                     
                    // 다음 달 블럭 표시
                    } else if(actlist.get(dayCnt).equals("n")){
                        oneday[dayCnt].setTextDaySize(18);
                        actlist.set(dayCnt, "");
                        oneday[dayCnt].setTextDayTopPadding(-4);
                        if(iMonth + 1 > Calendar.DECEMBER){
                            oneday[dayCnt].setMonth(Calendar.JANUARY);
                            oneday[dayCnt].setYear(iYear + 1);
                        }  else {
                            oneday[dayCnt].setMonth(iMonth + 1);
                            oneday[dayCnt].setYear(iYear);
                        }
                    // 현재 달 블력 표시
                    }else{
                        oneday[dayCnt].setTextDaySize(24);
                        oneday[dayCnt].setYear(iYear);
                        oneday[dayCnt].setMonth(iMonth);
                        
                        //oneday[dayCnt].setBgActcntPaint(Color.YELLOW);
                        //오늘 표시
                        if(oneday[dayCnt].getDay() == today.get(Calendar.DAY_OF_MONTH)
                                && oneday[dayCnt].getMonth() == today.get(Calendar.MONTH)
                                && oneday[dayCnt].getYear() == today.get(Calendar.YEAR)){
                             
                            oneday[dayCnt].isToday = true;
                            actlist.set(dayCnt,"오늘");
                            oneday[dayCnt].invalidate();
                            mSelect = dayCnt;
                        }
                        
              
	                    for(int key=0; key<mDay.size(); key++){
                        	if(oneday[dayCnt].getDay() == mDay.get(key)
	                        		&&oneday[dayCnt].getMonth() == mMonth.get(key)
	                        		&&oneday[dayCnt].getYear() == mYear.get(key)){
	                        	
	                        	oneday[dayCnt].textActCnts.add(title.get(key).toString());
	                        	oneday[dayCnt].bgActcntPaints.add(new Paint(Color.YELLOW));
	                        	Log.d("include",title.get(key).toString());  
	                        	
	                        }
	                    }
                    }
                     
 
                    oneday[dayCnt].setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //Toast.makeText(context, iYear+"-"+iMonth+"-"+oneday[v.getId()].getTextDay(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
 
                    oneday[dayCnt].setOnTouchListener(new OnTouchListener() {
 
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                             
                            if(oneday[v.getId()].getTextDay() != "" && event.getAction() == MotionEvent.ACTION_UP)
                            {
                                if(mSelect != -1){
                                    oneday[mSelect].setSelected(false);
                                    oneday[mSelect].invalidate();
                                }
                                oneday[v.getId()].setSelected(true);
                                oneday[v.getId()].invalidate();
                                mSelect = v.getId();
                                 
                                //Log.d("hahaha", oneday[mSelect].getMonth()+"-"+ oneday[mSelect].getDay());
                                 
                                onTouched(oneday[mSelect]);
                            }
                            return false;
                        }                        
                    });
                }
                 
                oneday[dayCnt].setTextDayTopPadding(-20); 		//revised Original: 없었음
                oneday[dayCnt].setTextDayLeftPadding(-19);		//revised Original: 없었음
                oneday[dayCnt].setTextDay(daylist.get(dayCnt).toString()); //요일,일자 넣기 
                oneday[dayCnt].setTextActCnt(actlist.get(dayCnt).toString());//활동내용 넣기 
                oneday[dayCnt].setId(dayCnt); //생성한 객체를 구별할수 있는 id넣기 
                oneday[dayCnt].invalidate();
                tr.addView(oneday[dayCnt]);
 
                if(daylistsize != dayCnt)
                {
                    dayCnt++;
                }else{
                    break;
                }
            }
            tl.addView(tr,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
 
        }
    }
     
    
    
    
    public static final String[] CALENDAR_PROJECTION = new String[] {
	    Calendars._ID,                           // 0
	    Calendars.ACCOUNT_NAME,                  // 1
	    Calendars.CALENDAR_DISPLAY_NAME,         // 2
	    Calendars.OWNER_ACCOUNT                  // 3
	};
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
	public static final String[] EVENT_PROJECTION =  new String[]{ 
		Events._ID,  
        Events.DTSTART,  
        Events.DTEND,  
        Events.RRULE,  
        Events.TITLE
    }; 
	//for calendars
	List<Long> calID = new ArrayList<Long>();
	List<Object> displayName = new ArrayList<Object>();
	List<Object> accountName = new ArrayList<Object>();
	List<Object> ownerName = new ArrayList<Object>();
	//for events
	List<Object> eventID = new ArrayList<Object>();
	List<Object> dtstart = new ArrayList<Object>();
	List<Object> dtend = new ArrayList<Object>();
	List<Object> rrule = new ArrayList<Object>();
	List<Object> title = new ArrayList<Object>();
	List<Integer> mYear = new ArrayList<Integer>();
	List<Integer> mMonth = new ArrayList<Integer>();
	List<Integer> mDay = new ArrayList<Integer>();
	
	
    private void syncDeviceCalendar(){
    	
    	calID.clear();
    	displayName.clear();
    	accountName.clear();
    	ownerName.clear();
    	eventID.clear();
    	dtstart.clear();
    	dtend.clear();
    	rrule.clear();
    	title.clear();
    	mYear.clear();
    	mMonth.clear();
    	mDay.clear();
    	
    	
    	
    	Cursor cur = null;
    	ContentResolver cr = getActivity().getContentResolver();
    	Uri uri = Calendars.CONTENT_URI;   
    //	String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
    //	                        + Calendars.ACCOUNT_TYPE + " = ?))";
    //	String[] selectionArgs = new String[]{"sampleuser@gmail.com", "com.google"}; 
    	// Submit the query and get a Cursor object back. 
    	cur = cr.query(uri, CALENDAR_PROJECTION, Calendars.VISIBLE + " = 1", null, null);
    	// Use the cursor to step through the returned records
    	
    	
    	while (cur.moveToNext()) {
    	        		    	      
    	    // Get the field values
    	    calID.add(cur.getLong(PROJECTION_ID_INDEX));
    	    displayName.add(cur.getString(PROJECTION_DISPLAY_NAME_INDEX));
    	    accountName.add(cur.getString(PROJECTION_ACCOUNT_NAME_INDEX));
    	    ownerName.add(cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX));
    	    Uri uri2 = Events.CONTENT_URI;
    	    ContentResolver cr2 = getActivity().getContentResolver();
    	    Cursor cur2 = cr2.query(uri2, EVENT_PROJECTION, Events.CALENDAR_ID+" ="+cur.getLong(PROJECTION_ID_INDEX),null, null);
        	
        	Calendar calendar = null;
        	while (cur2.moveToNext()) {
      	      
        	    // Get the field values
        		eventID.add(cur2.getLong(0));
        		dtstart.add(cur2.getLong(1));
        		dtend.add(cur2.getLong(2));
        		rrule.add(cur2.getString(3));
        		title.add(cur2.getString(4));
        	   	Log.d("title",cur2.getString(4));        	   
        		calendar = Calendar.getInstance();
        	    calendar.setTimeInMillis(cur2.getLong(1));
        	    
        	    mYear.add(calendar.get(Calendar.YEAR));
        	    mMonth.add(calendar.get(Calendar.MONTH));		
        	    mDay.add(calendar.get(Calendar.DAY_OF_MONTH));
        	    Log.d("mYear",String.valueOf(calendar.get(Calendar.YEAR)));	
        	    Log.d("mMonth",String.valueOf(calendar.get(Calendar.MONTH)));	
        	    Log.d("mDay",String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));	
        	}
    	}
  
    }
     
    
    /**
     * 숫자를 2자리 문자로 변환, 2 -> 02
     * @param value
     * @return
     */
    protected String doubleString(int value)
    {
        String temp;
 
        if(value < 10){
            temp = "0"+ String.valueOf(value);
             
        }else {
            temp = String.valueOf(value);
        }
        return temp;
    }
 
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
        case R.id.btn_calendar_nextmonth:
            if(iMonth == 11)
            {
                iYear = iYear + 1;
                iMonth = 0;
            }
            else
            {
                iMonth = iMonth + 1;
            }
            makeCalendardata(iYear,iMonth);
            break;
        case R.id.btn_calendar_prevmonth:
            if(iMonth == 0)
            {
                iYear = iYear - 1;
                iMonth = 11;
            }else{
                iMonth = iMonth - 1;
            }
            makeCalendardata(iYear,iMonth);
            break;
        }
    }
     
    /**
     * 서브 클래스에서 오버라이드 해서 터치한 날짜 입력 받기
     * @param oneday
     */
    protected void onTouched(Oneday oneday){
         
    }
     
    /**
     * 해당 일이 기준일 범위 안에 있는지 검사
     * @param test 검사할 날짜
     * @param basis 기준 날짜
     * @param during 기간(일)
     * @return
     */
    protected boolean isInside(Oneday test, Oneday basis, int during){
        Calendar calbasis = Calendar.getInstance();
        calbasis.set(basis.getYear(), basis.getMonth(), basis.getDay());
        calbasis.add(Calendar.DAY_OF_MONTH, during);
         
        Calendar caltest = Calendar.getInstance();
        caltest.set(test.getYear(), test.getMonth(), test.getDay());
         
        if(caltest.getTimeInMillis() < calbasis.getTimeInMillis()){
            return true;
        }
        return false;
    }
     
    /**
     *오늘 달력으로 이동 
     */
    public void gotoToday(){
        final Calendar today = Calendar.getInstance();
        iYear = today.get(Calendar.YEAR);
        iMonth = today.get(Calendar.MONTH);
        makeCalendardata(today.get(Calendar.YEAR),today.get(Calendar.MONTH));
    }
}
