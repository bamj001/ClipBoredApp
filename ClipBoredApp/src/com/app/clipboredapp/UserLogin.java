/********************************************************************************
 * Copyright (c) 2013, Bashar Jarrar, Alexander Meijer, All Rights Reserved
 * Filename: UserLogin.java
 * Author: Bashar Jarrar
 * Date: Dec 1, 2013
 * 
 * Located in package: com.app.clipbored
 * Project: ClipBored
 */

package com.app.clipboredapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class UserLogin extends DialogFragment {

	public UserLogin() {
		// empty constructor
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get LayoutInflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		// Inflate and set the layout for the dialog
		// Pass null as parent because its going to the dialog layout
		View v = inflater.inflate(R.layout.userlogin, null);
		
		// Get both userNameView and passwordView to get the texts from the
		// EditTexts later on
		final EditText usernameView = (EditText) v.findViewById(R.id.username);
		final EditText passwordView = (EditText) v.findViewById(R.id.password);

		// Set the view to the userlogin.xml view created
		builder.setView(v)
				// Set what happens when the ok button is clicked
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Get username and password strings from the views
						String username = usernameView.getText().toString();
						String password = passwordView.getText().toString();
						
						// Pass those values to doPositiveUserClick method in
						// clips activity class to
						// make the api calls and get the user information
						((ClipsActivity) getActivity()).doPositiveUserClick(username, password);
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
