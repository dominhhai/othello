package net.gamehd.othello;

import net.gamehd.othello.util.SoundManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * (c) 2012 HEDSPI - SoICT - HUST.
 * 
 * This class will show all options of game. Player can config the game here.
 * 
 * @author Hai Do Minh
 * @author Binh Nguyen Duc
 * @author Nhat Dinh Van
 * @since 04.04.2012
 */

public class OptionsActivity extends Activity {
	
	public final static int DIALOG_LEVEL_ID = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.options);
		
		final Button levelButton = (Button) findViewById(R.id.options_level);
		levelButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				showDialog(DIALOG_LEVEL_ID);
			}
		});
		
		final ListView listView = (ListView) findViewById(R.id.options_listview);
		listView.setSelectionAfterHeaderView();
		listView.setDuplicateParentStateEnabled(false);
		
		String[] options = new String[] {"Turn on Sound", "Turn on Music", "Show Suggest", "Show Last Move"};
		listView.setAdapter(new OptionsListAdapter(this, R.layout.options_row, options));
		listView.invalidate();
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (id == DIALOG_LEVEL_ID) {
			CharSequence[] items = new CharSequence[6];
			for (int i = 0; i < 6; i ++) {
				items[i] = "Level " + (i + 2);
			}
			builder.setTitle("Select a level");
			builder.setSingleChoiceItems(items, GameConfig.maxDepth - 2, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int index) {
					GameConfig.maxDepth = index + 2;
				}
			});
		}
		return builder.create();
	}
	
	private static class ViewHolder {
		TextView options_row_name;
		CheckBox options_row_checkbox;
	}
	
	class OptionsListAdapter extends BaseAdapter {

		String[] objects;
		int resource;
		Context context;
		LayoutInflater li;
		
		public OptionsListAdapter(Context context, int textViewResourceId, String[] objects) {
			this.context = context;
			this.resource = textViewResourceId;
			this.objects = objects;
			li = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View optionsView = convertView;
			
			if (optionsView == null) {
				optionsView = li.inflate(resource, null);
				holder = new ViewHolder();

				holder.options_row_name = (TextView) optionsView.findViewById(R.id.options_row_name);
				holder.options_row_checkbox = (CheckBox) optionsView.findViewById(R.id.options_row_checkbox);

				optionsView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final String option = this.objects[position];
			if (option != null) {
				holder.options_row_name.setText(option);
				switch (position) {
				case 0: // sound
					holder.options_row_checkbox.setChecked(GameConfig.enableSound);
					System.out.println("sound : " + GameConfig.enableSound);
					break;
				case 1: // music
					holder.options_row_checkbox.setChecked(GameConfig.enableMusic);
					System.out.println("music : " + GameConfig.enableMusic);
					break;
				case 2: // suggest
					holder.options_row_checkbox.setChecked(GameConfig.enableHint);
					System.out.println("hint : " + GameConfig.enableHint);
					break;
				case 3: // last move
					holder.options_row_checkbox.setChecked(GameConfig.enableLastmove);
					System.out.println("last : " + GameConfig.enableLastmove);
					break;
				case 4: // switch side
					holder.options_row_checkbox.setChecked(GameConfig.isAutoSwitchSide);
					break;
				default: // player side
					holder.options_row_checkbox.setChecked(false);
					break;
				}
				holder.options_row_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						switch (position) {
						case 0: // sound
							GameConfig.enableSound = holder.options_row_checkbox.isChecked();
							System.out.println("sound : " + GameConfig.enableSound);
							break;
						case 1: // music
							GameConfig.enableMusic = holder.options_row_checkbox.isChecked();
							if (GameConfig.enableMusic) {
								if (!SoundManager.msc_menu.isPlaying()) {
									SoundManager.msc_menu.play();
								}
							} else {
								if (SoundManager.msc_menu.isPlaying()) {
									SoundManager.msc_menu.pause();
								}
							}
							System.out.println("music : " + GameConfig.enableMusic);
							break;
						case 2: // suggest
							GameConfig.enableHint = holder.options_row_checkbox.isChecked();
							System.out.println("hint : " + GameConfig.enableHint);
							break;
						case 3: // last move
							GameConfig.enableLastmove = holder.options_row_checkbox.isChecked();
							System.out.println("last : " + GameConfig.enableLastmove);
							break;
						case 4: // switch side
							GameConfig.isAutoSwitchSide = holder.options_row_checkbox.isChecked();
							break;
						}
					}
				});
			}
			return optionsView;
		}

		public int getCount() {
			return objects.length;
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
	}
}
