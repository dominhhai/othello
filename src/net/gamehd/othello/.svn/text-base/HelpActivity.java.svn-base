package net.gamehd.othello;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

/**
 * (c) 2012 B-Gate Corp.
 * 
 * This class tell how to play this game.
 * 
 * @author Hai Do Minh
 * @since 03.03.2012
 */

public class HelpActivity extends Activity{

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.help);
		
		final WebView mWebView = (WebView) findViewById(R.id.help_webView);
		mWebView.setDrawingCacheEnabled(false);
		mWebView.setDrawingCacheBackgroundColor(0x00000000);
		mWebView.setBackgroundColor(0x00000000);
		mWebView.loadUrl("file:///android_asset/help.html");
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

}
