package com.whend.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
//import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBar;

public class PF00_Loading extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pf00__loading);
		
//		ActionBar atb = getActionBar();
//		atb.hide();
//		
		loading();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pf00__loading, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void loading(){
		
		final Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				// Main Sleeping
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					change_status_handler.sendEmptyMessage(0);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						Intent i = new Intent(PF00_Loading.this, PF01_Main.class);
						startActivity(i);
						finish();
					}
					
				}
			}
		});
		
		thread.start();
		
		
	}
	
	private Handler change_status_handler = new Handler(){
		public void handleMessage(Message msg){
			TextView status = (TextView) findViewById(R.id.pf00_status);
			status.setText("PicFridge Turned on");
		}
	};
}
