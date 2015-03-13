package net.gamehd.othello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.gamehd.othello.util.HighScore;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * (c) 2012 B-Gate Corp.
 * 
 * This class will show 10 people who have highest score.
 * 
 * @author Hai Do Minh
 * @since 03.03.2012
 */

public class HighScoreActivity extends Activity{

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.highscore);
		
		final ListView listView = (ListView) findViewById(R.id.listScore);
		listView.setSelectionAfterHeaderView();
		listView.setAdapter(getHighScore());
	}
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Runtime.getRuntime().gc();
			finish();
			return true;
		} else {
			return false;
		}
	}
	
	public SimpleAdapter getHighScore() {
		String[] from = new String[] { "name", "score"};
		int[] to = new int[] { R.id.hsc_name, R.id.hsc_score };
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		byte index = 1;
		final HighScore mHighScore = HighScore.getInstance(getApplicationContext());
		while(index <= HighScore.NUMBER_STORE && mHighScore.getScore(index - 1) > 0) {
			HashMap<String, String> map = new HashMap<String, String>();
			System.out.println("hs + "+index +"__"+mHighScore.getName(index - 1)+"__"+mHighScore.getScoreString(index -1));
			map.put("name", index +". " + mHighScore.getName(index - 1));
			map.put("score", mHighScore.getScoreString(index -1));
			fillMaps.add(map);
			index ++;
		}
		return new SimpleAdapter(this, fillMaps, R.layout.highscorerow, from, to);
	}
}
