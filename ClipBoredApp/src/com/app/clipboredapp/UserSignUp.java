/********************************************************************************
 * Copyright (c) 2013, Bashar Jarrar, Alexander Meijer, All Rights Reserved
 * Filename: UserSignUp.java
 * Author: Bashar Jarrar
 * Date: Dec 1, 2013
 * 
 * Located in package: com.app.clipbored
 * Project: ClipBored
 */

package com.app.clipboredapp;

import java.util.ArrayList;
import java.util.Scanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class UserSignUp extends DialogFragment {

	public UserSignUp() {
		// empty constructor
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get LayoutInflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		// Inflate and set the layout for the dialog
		// Pass null as parent because its going to the dialog layout
		View v = inflater.inflate(R.layout.usersignup, null);
		final EditText usernameSignUpView = (EditText) v.findViewById(R.id.usernameSignUp);
		final EditText passwordSignUpView = (EditText) v.findViewById(R.id.passwordSignUp);
		final EditText emailView = (EditText) v.findViewById(R.id.email);
		final EditText categoriesView = (EditText) v.findViewById(R.id.categoriesSignUp);

		// Set the view to the usersignup.xml view created
		builder.setView(v)
				// Set what happens when the ok button is clicked
				.setPositiveButton(R.string.signup, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Get username, password, email and categories list
						// from their respective views
						String username = usernameSignUpView.getText().toString();
						String password = passwordSignUpView.getText().toString();
						String email = emailView.getText().toString();
						String concatCategories = categoriesView.getText().toString();
						
						// Make an array list of categories
						ArrayList<String> categoriesOfInterest = new ArrayList<String>();
						
						// Create a scanner to parse the string of concatenated
						// categories
						Scanner scanner = new Scanner(concatCategories);
						
						// If the scanner has another word, add it to the array
						// list of strings
						while (scanner.hasNext()) {
							categoriesOfInterest.add(scanner.next());
						}
						// Close the scanner
						scanner.close();
						
						// Pass those values to doPositiveUserSignUpClick method
						// in clips activity class to
						// make the api calls, signup the user and get back user
						// information
						((ClipsActivity) getActivity()).doPositiveUserSignUpClick(username, password, email,
								categoriesOfInterest);
					}
				})
				// Set what happens when the cancel buttons is selected
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						// User does not want to signup anymore
					}
				});

		return builder.create();

	}

}
