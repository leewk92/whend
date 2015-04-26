package com.whend.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

public class Calfeed {
		private Long start;
		private Long end;
		private String company;
		private String date_start;
		private String date_end;
		private String items;
		private String place;
		private String index;
		private String icon_company;
		private String icon_content;
		private int follow_num;
		private int event_primary;
		private int page_primary;
		private boolean is_incalendar;
		private boolean isMaster;
		private int allday;
		
		public Calfeed(String company, String items, Long _date_start, Long _date_end, String place, String index, String icon_company, String icon_content, int follow_num, boolean is_incalendar, int event_primary, int page_primary){
			//지금은 place 가 memo로 쓰이고있음

			
			
			
			this.company = company;
			this.items = items;
			this.start = _date_start;
			this.end = _date_end;
	//		this.date_start = new Date(_date_start).toString();
	//		this.date_end =  new Date(_date_end).toString();
			this.place = place;
			this.index = index;
			this.icon_company = icon_company;
			this.icon_content = icon_content;
			this.follow_num = follow_num;
			this.is_incalendar = is_incalendar;
			this.event_primary = event_primary;
			this.page_primary = page_primary;
			
		}
		
		public String getCompany(){
			return company;
		}
		
		public String getItems(){
			return items;
		}
		
		public Long getStart(){
			return start;
		}
		
		public Long getEnd(){
			return end;
		}
		public int getAllday(){
			return allday;
		}
		public String getDateStart(){
			return date_start;
		}
		public String getDateEnd(){
			return date_end;
		}
		
		public String getPlace(){
			return place;
		}
		
		public String getIndex(){
			return index;
		}
		
		public String getIconCompany(){
			return icon_company;
		}
		
		public String getIconContent(){
			return icon_content;
		}
		
		public int getFollowNumber(){
			return follow_num;
		}
		public int getEventPrimary(){
			return event_primary;
		}
		
		public boolean isIncalendar(){
			return is_incalendar;
		}
		
		public void changeIncalendar(){
			is_incalendar = !is_incalendar;
		}
		
		public boolean isMaster(){
			return isMaster;
		}
		
		public void setIsMaster(boolean a){
			isMaster = a;
		}
		public void setAllday(int allday){
			this.allday = allday;
				
			if(this.allday == 0){
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				this.date_start = sdFormat.format(new Date(start)).toString();
				this.date_end =  sdFormat.format(new Date(end)).toString();
			
			}else if (this.allday==1){
				SimpleDateFormat sdFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				this.date_start = sdFormat2.format(new Date(start)).toString();
				this.date_end =  sdFormat2.format(new Date(end - 3600*24*1000)).toString();
						
			}
			
					
			
		}
}
