/********************************************************************************
 * Copyright (c) 2013, Bashar Jarrar, Alexander Meijer, All Rights Reserved
 * Filename: ClipsActivity.java
 * Author: Bashar Jarrar
 * Date: Dec 1, 2013
 * 
 * Located in package: com.app.clipbored
 * Project: ClipBored
 */

package com.app.clipboredapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.samplesolutions.mobileassistant.userendpoint.Userendpoint;
import com.google.samplesolutions.mobileassistant.userendpoint.model.User;
import com.google.samplesolutions.mobileassistant.videoendpoint.Videoendpoint;
import com.google.samplesolutions.mobileassistant.videoendpoint.model.CollectionResponseVideo;
import com.google.samplesolutions.mobileassistant.videoendpoint.model.Video;

public class ClipsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

	public static final String PREFS = "MyPrefs";
	public static final String FRAGMENT_CHOOSE_CATEGORIES = "fragment_choose_categories";
	public static final String FRAGMENT_USER_LOGIN = "fragment_user_login";
	public static final String FRAGMENT_USER_SIGNUP = "fragment_user_signup";
	public static final String INITIALIZATION_FAIL = "Initialization Fail";
	public static final String CATEGORIES_UPDATED = "Categories updated";
	public static final String USERNAME_PASS_INCORRECT = "Username or Password incorrect";
	public static final String SIGNUP_SUCCESS = "Sign Up success! Welcome to ClipBored!";
	
	public CollectionResponseVideo CollectionOfVideos, CollectionOfStartUpVideos;
	public List<String> listOfVideos = new ArrayList<String>();
	public List<String> listOfStartUpVideos = new ArrayList<String>();
	public ArrayList<String> categoriesList = new ArrayList<String>();
	public YouTubePlayer player;
	public Provider provider;
	public Handler mHandler;
	public User user, newUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clips);

		// Declare YouTubePlayerView
		YouTubePlayerView ytpv = (YouTubePlayerView) findViewById(R.id.youtubeplayer);

		// Initialize YouTubePlayerView using DeveloperKey obtained from
		// YouTubeAPIDemo
		ytpv.initialize("REDACTED", this);

		// List all videos
		try {
			new VideoListAsyncRetriever().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.clips, menu);

		return true;
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		// Set class variable player to current initialized player in order to
		// be used inside the whole class
		this.player = player;

		// Make a list of cues that is used to be passed to the player because
		// the getTag method returns two extra characters
		// so we use the substring to chop those two off.
		List<String> cue = new ArrayList<String>();

		for (String tag : listOfStartUpVideos) {
			cue.add(tag.substring(2));
		}

		// Play YouTube videos in the cue.
		player.loadVideos(cue);
	}

	@Override
	public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {
		// When initialization fails for whatever reason, print out fail.
		Toast.makeText(this, INITIALIZATION_FAIL, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.categories:
			popUpCategories();
			return true;
		case R.id.user_login:
			popUpUserLogin();
			return true;
		case R.id.user_signup:
			popUpUserSignUp();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Play all videos passed as a list of tags from a certain category
	private void playCategories(List<String> listOfCategoryVideos) {
		List<String> cue = new ArrayList<String>();

		// Make a list of cues that is used to be passed to the player because
		// the getTag method returns two extra characters so we use the
		// substring to chop those two off.
		for (String tag : listOfCategoryVideos) {
			cue.add(tag.substring(2));
		}

		player.loadVideos(cue);
	}

	// Thread that deals with signing up a new user from the sign up dialog
	public class MyUserSignUpThread implements Runnable {
		String username, password, email;
		ArrayList<String> categoriesOfInterest;

		// Pass in user name, password, email and categories into the thread to
		// be used to sign up the user.
		public MyUserSignUpThread(String username, String password, String email,
				ArrayList<String> categoriesOfInterest) {
			this.username = username;
			this.password = password;
			this.email = email;
			this.categoriesOfInterest = categoriesOfInterest;
		}

		public void run() {
			// An API call that creates a builder that builds a userendpoint
			// that can deal with everything user related
			Userendpoint.Builder builder = new Userendpoint.Builder(AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(), null);

			builder = CloudEndpointUtils.updateBuilder(builder);

			Userendpoint endpoint = builder.build();

			// Specify all user attributes
			User user = new User();
			user.setName(username);
			user.setPassword(password);
			user.setEmail(email);
			user.setCategories(categoriesOfInterest);
			user.setSignUpDate(new DateTime(System.currentTimeMillis()));

			try {
				// Set class variable newUser to the retrieved user to make sure
				// that it was actually created using an API call
				newUser = endpoint.insertUser(user).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Thread that deals with logging in a user from the login dialog
	public class MyUserThread implements Runnable {
		String username, password;

		// Pass user name and password to login the user to the thread.
		public MyUserThread(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public void run() {
			// An API call that creates a builder that builds a user end point
			// that can deal with everything user related
			Userendpoint.Builder builder = new Userendpoint.Builder(AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(), null);

			builder = CloudEndpointUtils.updateBuilder(builder);

			Userendpoint endpoint = builder.build();
			User result = null;

			try {
				// API call that gets the user back
				result = endpoint.getUser(username, password).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Set class variable user to result for further processing
			user = result;
		}
	}

	// Thread that deals with getting category videos from the categories dialog
	public class MyVideoThread implements Runnable {
		String category;

		// Pass in the category to the thread
		public MyVideoThread(String category) {
			this.category = category;
		}

		public void run() {

			// An API call that creates a builder that builds a video end point
			// that can deal with everything video related
			Videoendpoint.Builder endpointBuilder = new Videoendpoint.Builder(AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(), null);

			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);

			// A collectionResponseVideo is a collection of video objects
			CollectionResponseVideo result;

			Videoendpoint endpoint = endpointBuilder.build();

			try {
				// API call that returns the list of videos back
				result = endpoint.listVideoCategory(category).execute();
			} catch (IOException e) {
				e.printStackTrace();
				result = null;
			}

			// Setting class video to the list returned back from the API call
			CollectionOfVideos = result;

			// Create an empty list of tags
			List<String> tags = new ArrayList<String>();

			// Call getItems on the collection of videos to get all the videos
			List<Video> vids_in_category = CollectionOfVideos.getItems();

			for (Video vid : vids_in_category) {
				// Adding every tag from the videos in the list and add them to
				// the empty list of tags
				tags.add(vid.getTag());
			}

			// Add all the tags to the class variable ArrayList listOfVideos
			for (String tag : tags) {
				listOfVideos.add(tag);
			}
		}
	};

	private void popUpCategories() {
		// Get fragment manager
		FragmentManager fm = getFragmentManager();

		// Create new categoriesDialog to pop up
		CategoriesDialog chooseCategories = new CategoriesDialog();

		// Show pop up dialog
		chooseCategories.show(fm, FRAGMENT_CHOOSE_CATEGORIES);
	}

	private void popUpUserLogin() {
		// Get fragment manager
		FragmentManager fm = getFragmentManager();

		// Create new user login dialog to pop up
		UserLogin login = new UserLogin();

		// Show pop up dialog
		login.show(fm, FRAGMENT_USER_LOGIN);
	}

	private void popUpUserSignUp() {
		// Get fragment manager
		FragmentManager fm = getFragmentManager();

		// Create new user sign up dialog to pop up
		UserSignUp signup = new UserSignUp();

		// Show pop up dialog
		signup.show(fm, FRAGMENT_USER_SIGNUP);
	}

	// Method called when the ok button is clicked in the categories dialog
	public void doPositiveClick(ArrayList<String> categoriesList, CheckBox[] checkboxes) {
		// Create a new list of strings
		List<String> videos = new ArrayList<String>();

		// Pop up message saying that the categories were updated
		Toast.makeText(this, CATEGORIES_UPDATED, Toast.LENGTH_SHORT).show();

		// Setting class variable categoriesList to the list passed to the
		// method
		this.categoriesList = categoriesList;

		// Get shared preferences for the class
		SharedPreferences settings = getSharedPreferences(PREFS, 0);

		SharedPreferences.Editor editor = settings.edit();

		// Save all the check box values
		for (int i = 0; i < checkboxes.length; i++) {
			boolean checkBoxValue = checkboxes[i].isChecked();
			editor.putBoolean("checkbox_value " + i, checkBoxValue);
		}

		// Commit changes
		editor.commit();

		// For each category in the categories list run a new thread and get the
		// videos
		for (String category : categoriesList) {
			Runnable r = new MyVideoThread(category);

			Thread t = new Thread(r);
			t.start();

			try {
				// Wait until each thread is done to go to the next instruction
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Compile the list of tags and add them to the empty videos variable
		for (String string : listOfVideos) {
			videos.add(string);
		}

		// Clear the list of videos in the list so users can be able to call
		// categories again and to fill the list
		// with new videos
		listOfVideos.clear();

		// Play the videos in the current category(ies) selected
		playCategories(videos);
	}

	// Method called when the ok button is clicked in the user login dialog
	public void doPositiveUserClick(String username, String password) {
		// Run the my user thread to get the user information from the login
		Runnable r = new MyUserThread(username, password);

		Thread t = new Thread(r);
		t.start();

		try {
			// Wait until each thread is done to go to the next instruction
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Check that the user actually exists with these credentials by
		// checking against the key and checking whether it is null or not
		if (user.getKey() == null) {
			// If user key does not exist that means the user name or password
			// were not correct
			Toast.makeText(this, USERNAME_PASS_INCORRECT, Toast.LENGTH_SHORT).show();
		} else {
			// Otherwise the user exists with these credentials, so print
			// greeting message
			Toast.makeText(this, "Hello " + user.getName(), Toast.LENGTH_SHORT).show();
		}
	}

	//// Method called when the ok button is clicked in the user sign up dialog
	public void doPositiveUserSignUpClick(String username, String password, String email,
			ArrayList<String> categories) {
		// Run the user sign up thread with the given parameters
		Runnable r = new MyUserSignUpThread(username, password, email, categories);

		Thread t = new Thread(r);
		t.start();

		try {
			// Wait until each thread is done to go to the next instruction
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// If the new user was correctly created, it will have a key, we again
		// check against the key to check if the new user exists
		if (newUser.getKey() == null) {
			// If the key is null that means the user does not exist and we
			// print an error
			Toast.makeText(this, "Error in adding user", Toast.LENGTH_SHORT).show();
		} else {
			// Otherwise the user does exist and the sign up was successful
		}
		
		Toast.makeText(this, SIGNUP_SUCCESS , Toast.LENGTH_SHORT).show();
	}

	/**
	 * AsyncTask for retrieving the list of places (e.g., stores) and updating
	 * the corresponding results list.
	 */
	private class VideoListAsyncRetriever extends AsyncTask<Void, Void, CollectionResponseVideo> {

		@Override
		protected CollectionResponseVideo doInBackground(Void... params) {

			// An API call that creates a builder that builds a video end point
			// that can deal with everything video related
			Videoendpoint.Builder endpointBuilder = new Videoendpoint.Builder(AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(), null);

			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);

			// Creating a new CollectionResponseVideo object which is basically
			// a collection of videos
			CollectionResponseVideo result;

			Videoendpoint endpoint = endpointBuilder.build();

			try {
				// API call to return all the videos available in the data store
				result = endpoint.listVideo().execute();
			} catch (IOException e) {
				e.printStackTrace();
				
				result = null;
			}

			return result;
		}

		
		@Override
		protected void onPostExecute(CollectionResponseVideo result) {

			if (result == null || result.getItems() == null || result.getItems().size() < 1) {
				if (result == null) {
					Log.d("Retrieving videos from category failed.", "Error in video category retrieval:");
				} else {
					Log.d("No videos in that category found", "Error in video category retrieval:");
				}

				return;
			}

			// Set class variable to result to be used in later calls
			CollectionOfStartUpVideos = result;

			// Create empty list of tags to be passed to later function calls
			List<String> tags = new ArrayList<String>();

			// Call getItems on the collection of videos to get all the videos
			List<Video> vids_in_category = CollectionOfStartUpVideos.getItems();
			for (Video vid : vids_in_category) {
				// Adding every tag from the videos in the list and add them to
				// the empty list of tags
				tags.add(vid.getTag());
			}

			// Set class variable equals to tags to be passed to
			// onInitializeSuccess function
			listOfStartUpVideos = tags;
		}
	}

}
