package net.gamehd.othello.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * (c) 2012 B-Gate Corp.
 * 
 * This class is useful to save high score into database by using SharedPrer database.
 * 
 * @author Hai Do Minh
 * @since 03.03.2012
 */

public class HighScore { 
	
	private static HighScore instance;
	
	private SharedPreferences preferences;
	private String names[];
	private long score[];
	public static int NUMBER_STORE = 10;
	
	
	public static HighScore getInstance(Context context) {
		if(instance == null) {
			instance = new HighScore(context);
		}
		return instance;
	}
	private HighScore(Context context) {
		preferences = context.getSharedPreferences("Highscore", 0); 
		names = new String[NUMBER_STORE];
		score = new long[NUMBER_STORE];
		for (int i = 0; i < NUMBER_STORE; i++) {
			names[i] = preferences.getString("name" + i, "-");
			score[i] = preferences.getLong("score" + i, 0);
		}
	}

	public String getName(int i) {
		return names[i];
	}

	public long getScore(int i) {
		return score[i];
	}
	
	public String getScoreString(int i) {
		return String.valueOf(score[i]);
	}

	public boolean isHighscore(long score) {
		if(score <= 0) {
			return false;
		}
		int position;
		for (position = 0; position < NUMBER_STORE && this.score[position] > score; position++)		;
		return (position != NUMBER_STORE);
	}

	public boolean addScore(String name, long score) {
		int position;
		for (position = 0; position < NUMBER_STORE && this.score[position] >= score; position++); 
		if (position == NUMBER_STORE)
			return false;
		for (int x = NUMBER_STORE - 1; x > position; x--) {
			names[x] = names[x - 1];
			this.score[x] = this.score[x - 1];
		}
		this.names[position] = new String(name);
		this.score[position] = score;
		SharedPreferences.Editor editor = preferences.edit();
		for (int x = 0; x < NUMBER_STORE; x++) {
			editor.putString("name" + x, this.names[x]);
			editor.putLong("score" + x, this.score[x]);
		}
		editor.commit();
		return true;
	}
}
