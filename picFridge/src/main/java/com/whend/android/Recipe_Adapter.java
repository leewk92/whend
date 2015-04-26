package com.whend.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Recipe_Adapter extends ArrayAdapter<Recipe>{
	private ArrayList<Recipe> recipe_list;
	private Activity activity;
	
	public Recipe_Adapter(Activity a, int textViewResourceId, ArrayList<Recipe> lists){
		super(a, textViewResourceId, lists);
		this.recipe_list = lists;
		this.activity = a;
	}
	
	public static class ViewIds{
		public TextView recipe_name;
		public TextView item_main;
		public TextView item_sub;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		ViewIds Ids;
		
		if(v==null){
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_recipe_row, null);
			Ids = new ViewIds();
			Ids.recipe_name = (TextView) v.findViewById(R.id.recipe_name);
			Ids.item_main = (TextView) v.findViewById(R.id.recipe_main);
			Ids.item_sub = (TextView) v.findViewById(R.id.recipe_sub);
			v.setTag(Ids);
		}else
			Ids = (ViewIds)v.getTag();
		
		final Recipe r = recipe_list.get(position);
		
		if(position == 0){
			Ids.recipe_name.setText("Recipe");
			Ids.item_main.setText("Main Ingredient");
			Ids.item_sub.setText("Sub Ingredient");
			
		}else if(r != null){
			Ids.recipe_name.setText(""+ r.getRecipe_name());
			Ids.item_main.setText(r.getItem_main());
			Ids.item_sub.setText(r.getItem_sub());
			
		}	
		return v;
	}
}
