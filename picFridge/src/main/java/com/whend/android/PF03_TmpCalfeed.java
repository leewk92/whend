package com.whend.android;

import java.util.ArrayList;




import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
public class PF03_TmpCalfeed extends Fragment {
	 
	//public Following_Feed menu_setting = new Following_Feed("Ingredient","00000000");
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    public int no_items;
    public SQLiteDatabase db;
	public static ArrayList<Following_Feed> Item_list = new ArrayList<Following_Feed>();
	public View rootView = null;
	PF03_Calfeed frag = PF03_Calfeed.newInstance();
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	    
	    
	}
/*
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		   //inflater.inflate(R.menu.pf02__items, menu);
		    ((PF01_Main)getActivity()).setActionBarTitle(" Calfeed");
		}
	*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        rootView = inflater.inflate(R.layout.frag_pf03__calfeed, container, false);
     //   Intent intent = new Intent(getActivity(), CalendarView.class);
	 //   startActivity(intent);
        //refresh_list(rootView, db);
       
      //  getFragmentManager().beginTransaction().add(frag, "CTcalFrag").commit();
	    if(frag.isAdded()){
	    	Log.d("already","added");
	    
	    }else getFragmentManager().beginTransaction().replace(R.id.realtabcontent, frag,"feedtag").addToBackStack(null).commit();	//이게 돌아가야하는데..
	   // getFragmentManager().beginTransaction().add(R.id.fl_activity_main, frag,"feedtag").addToBackStack(null).commit();	//이게 돌아가야하는데..
        
        
     //   CalendarView a = new CalendarView();
	    
	   
        return rootView;
    }
    
  /* @Override
   public void onPause(){
	  
	    getFragmentManager().beginTransaction().remove(frag).commit();
   }*/
    
    
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
    
    /*private void startDownload() {
	    String url = "http://10.10.10.31/data.db";
	    new Data_DownloadFileAsync(getActivity()).execute(url, "1", "1");
	}*/
    
    
    public void refresh_list(View rootView, SQLiteDatabase db){
    	
    	//startDownload();

    	//Item_list = new ArrayList<Following_Feed>();
    	//Item_list.add(menu_setting);
    	
    	no_items = 0;
    	
    	//db = SQLiteDatabase.openDatabase( "/sdcard/data.db" , null, SQLiteDatabase.OPEN_READONLY);

    	String tableName = "fruits";
        String sql = "select * from " + tableName + ";";
        //Cursor results = db.rawQuery(sql, null);
  
        //results.moveToFirst();
        
       /* while(!results.isAfterLast()){
            no_items++;
            results.moveToNext();
            
        }

        results.moveToFirst();

        if (no_items != 0){
            
        	for (int i=0; i<no_items; i++){
        		String name = results.getString(0);
                String date = results.getString(1);
                if (name.equals("Vita"))
                	name = name + "" +"500";
                Item_list.add(new Item(name,date));
                results.moveToNext();

        	}        	
        }*/
        
        ListView listview = (ListView)rootView.findViewById(R.id.listView1);
 		listview.setAdapter(new Following_Feed_Adapter(getActivity(),R.layout.list_followers_row,Item_list));
 		
 		//results.close();
    }

}