package com.whend.android;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Following_Feed {
	
	private int page_primary;
	private String pagekey;
	private String company;
	private String group;
	private String icon_company;
	private int follow_num;
	private boolean isWallFollow;
	private boolean isCalFollow;
	private boolean isMaster;

	public Following_Feed(String company, String group, String icon, String pagekey, int page_primary, int follow_num){
		this.pagekey = pagekey;
		this.page_primary = page_primary;
		this.company = company;
		this.group = group;
		this.icon_company = icon;
		this.follow_num = follow_num;
		isWallFollow = false;
		isCalFollow = false;
		isMaster = false;
	}
		
	public void setIsWallFollow(boolean isfollow){
		this.isWallFollow = isfollow;
	}
	
	public void setIsMaster(boolean isMaster){
		this.isMaster = isMaster;
	}
	
	public void setFollowNum(int follownum){
		this.follow_num = follownum;
	}
	public int getPagePrimary(){
		return page_primary;
	}
	
	public String getPagekey(){
		return pagekey;
	}
		
	public String getCompany(){
		return company;
	}
	
	public String getIcon(){
		return icon_company;
	}
	
	public String getGroup(){
		return group;
	}
	
	public void changeWallFollowingstate(){
		isWallFollow = !isWallFollow;
	}

	
	public boolean isWallFollow(){
		return isWallFollow;
	}
	
	public boolean isMaster(){
		return isMaster;
	}
	
	public boolean isCalFollow(){
		return isCalFollow;
	}
	
	public void setIsCalFollow(boolean isfollow){
		this.isCalFollow = isfollow;
	}
	
	public void changeCalFollowingstate(){
		isCalFollow = !isCalFollow;
	}
	public int getFollowNum(){
		return follow_num;
	}
	
}
