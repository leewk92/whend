package com.whend.android;

import java.util.Calendar;

public class Recipe {
	
	private String recipe_name;
	private String item_main;
	private String item_sub;
		
	Calendar calendar = Calendar.getInstance();

	public Recipe(String recipe_name, String item_main, String item_sub){
		this.recipe_name = recipe_name;
		this.item_main = item_main;
		this.item_sub = item_sub;
	
	}
		
	
	public String getRecipe_name(){
		return recipe_name;
	}
	
	public String getItem_main(){
		return item_main;
	}
	public String getItem_sub(){
		return item_sub;
	}
}
