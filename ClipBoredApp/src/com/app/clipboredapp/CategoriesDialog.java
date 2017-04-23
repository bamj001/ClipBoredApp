/********************************************************************************
 * Copyright (c) 2013, Bashar Jarrar, Alexander Meijer, All Rights Reserved
 * Filename: CategoriesDialog.java
 * Author: Bashar Jarrar
 * Date: Dec 1, 2013
 * 
 * Located in package: com.app.clipbored
 * Project: ClipBored
 */

package com.app.clipboredapp;

import java.util.ArrayList;

import com.example.clipboredapp.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CategoriesDialog extends DialogFragment {

	// Category integers
	public static final int SPORTS_CATEGORY = 0;
	public static final int ARTS_CATEGORY = 1;
	public static final int HEALTH_AND_BEAUTY_CATEGORY = 2;
	public static final int SCIENCES_CATEGORY = 3;

	public static final int NUM_CATEGORIES = 4;

	public CategoriesDialog() {
		// Empty constructor required
	}

	// Get the list of categories from an ArrayList of integers
	public ArrayList<String> getCategoryStrings(ArrayList<Integer> arrayList) {
		ArrayList<String> categoryStrings = new ArrayList<String>();

		for (int i = 0; i < arrayList.size(); i++) {
			int categoryInt = arrayList.get(i);
			// Switch on category integers and give each integer a string value
			switch (categoryInt) {
			case SPORTS_CATEGORY:
				categoryStrings.add("Sports");
				break;
			case ARTS_CATEGORY:
				categoryStrings.add("Arts");
				break;
			case HEALTH_AND_BEAUTY_CATEGORY:
				categoryStrings.add("Health & Beauty");
				break;
			case SCIENCES_CATEGORY:
				categoryStrings.add("Sciences");
				break;
			}
		}

		return categoryStrings;
	}

	// onCreate function to create dialog
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Get LayoutInflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog

		// Pass null as parent because its going to the dialog layout
		View v = inflater.inflate(R.layout.choosecategories, null);

		// Create an empty list of integer for all selected categories
		final ArrayList<Integer> selectedCategories = new ArrayList<Integer>();

		// Get all check box objects in the dialog used later to save check box
		// state
		CheckBox sportsView = (CheckBox) v.findViewById(R.id.checkbox_sports);
		CheckBox artsView = (CheckBox) v.findViewById(R.id.checkbox_arts);
		CheckBox healthBeautyView = (CheckBox) v.findViewById(R.id.checkbox_healthBeauty);
		CheckBox sciencesView = (CheckBox) v.findViewById(R.id.checkbox_sciences);

		// If the check box was checked then add 'sports' to list of selected
		// categories
		sportsView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkboxView, boolean isChecked) {
				if (isChecked)
					selectedCategories.add(SPORTS_CATEGORY);
			}

		});

		// If the check box was checked then add 'arts' to list of selected
		// categories
		artsView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkboxView, boolean isChecked) {
				if (isChecked)
					selectedCategories.add(ARTS_CATEGORY);
			}

		});

		// If the check box was checked then add 'healthBeauty' to list of
		// selected categories
		healthBeautyView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkboxView, boolean isChecked) {
				if (isChecked)
					selectedCategories.add(HEALTH_AND_BEAUTY_CATEGORY);
			}

		});

		// If the check box was checked then add 'sciences' to list of selected
		// categories
		sciencesView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkboxView, boolean isChecked) {
				if (isChecked)
					selectedCategories.add(SCIENCES_CATEGORY);
			}

		});

		// Make an array of check boxes to pass their states to the clips
		// activity in the doPositiveClick method

		final CheckBox[] checkboxes = new CheckBox[NUM_CATEGORIES];
		checkboxes[SPORTS_CATEGORY] = sportsView;
		checkboxes[ARTS_CATEGORY] = artsView;
		checkboxes[HEALTH_AND_BEAUTY_CATEGORY] = healthBeautyView;
		checkboxes[SCIENCES_CATEGORY] = sciencesView;

		// Set the view to the choosecategories.xml view created
		builder.setView(v)

		// Set opening message
		.setMessage(R.string.title_categories_select)
		// Set what happens when the ok button is clicked
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Get all the category strings from the ints
				ArrayList<String> categoryStrings = getCategoryStrings(selectedCategories);

				// Call the doPositiveClick method in the clips activity
				// class to make the api calls and get the right videos
				((ClipsActivity) getActivity()).doPositiveClick(categoryStrings, checkboxes);
			}
		})

		// Set what happens when the cancel buttons is selected
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				// User does not want the categories they chose anymore
			}
		});

		return builder.create();
	}

}
