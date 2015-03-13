package net.gamehd.othello.util;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

/**
 * (c) 2012 HEDSPI - SoICT - HUST.
 * 
 * Manager all sounds and musics of the game.
 * 
 * @author Hai Do Minh
 * @author Binh Nguyen Duc
 * @author Nhat Dinh Van
 * @since 04.04.2012
 */

public final class SoundManager {

	public static Music msc_draw;
	public static Music msc_game;
	public static Music msc_lose;
	public static Music msc_menu;
	public static Music msc_win;
	
	public static Sound sfx_invalid_move;
	public static Sound sfx_player1_move;
	public static Sound sfx_player1_pass;
	public static Sound sfx_player2_move;
	public static Sound sfx_player2_pass;
	public static Sound sfx_undo_move;
	
	public static void loadSoundResource(final BaseGameActivity mBaseGameActivity) {
		final Engine mEngine = mBaseGameActivity.getEngine();
		// load sound
		SoundFactory.setAssetBasePath("music/");
		try {
			org.andengine.audio.sound.SoundManager mSoundManager = mEngine.getSoundManager();
			sfx_invalid_move = SoundFactory.createSoundFromAsset(mSoundManager, mBaseGameActivity, "sfx_invalid_move.ogg");
			sfx_player1_move = SoundFactory.createSoundFromAsset(mSoundManager, mBaseGameActivity, "sfx_player1_move.ogg");
			sfx_player1_pass = SoundFactory.createSoundFromAsset(mSoundManager, mBaseGameActivity, "sfx_player1_pass.ogg");
			sfx_player2_move = SoundFactory.createSoundFromAsset(mSoundManager, mBaseGameActivity, "sfx_player2_move.ogg");
			sfx_player2_pass = SoundFactory.createSoundFromAsset(mSoundManager, mBaseGameActivity, "sfx_player2_pass.ogg");
			sfx_undo_move = SoundFactory.createSoundFromAsset(mSoundManager, mBaseGameActivity, "sfx_undo_move.ogg");
		} catch (final IOException e) {
					Debug.e(e);
		}
		// load music
		MusicFactory.setAssetBasePath("music/");
		try {
			MusicManager mMusicManager = mEngine.getMusicManager();
			msc_menu = MusicFactory.createMusicFromAsset(mMusicManager, mBaseGameActivity, "msc_menu.ogg");
			msc_menu.setLooping(true);
			msc_game = MusicFactory.createMusicFromAsset(mMusicManager, mBaseGameActivity, "msc_game.ogg");
			msc_game.setLooping(true);
			
			msc_draw = MusicFactory.createMusicFromAsset(mMusicManager, mBaseGameActivity, "msc_draw.ogg");
			msc_lose = MusicFactory.createMusicFromAsset(mMusicManager, mBaseGameActivity, "msc_lose.ogg");
			msc_win = MusicFactory.createMusicFromAsset(mMusicManager, mBaseGameActivity, "msc_win.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}
	
	public static void releaseSourceResources() {
		// sound
		sfx_invalid_move.release();
		sfx_player1_move.release();
		sfx_player2_move.release();
		sfx_player1_pass.release();
		sfx_player2_pass.release();
		sfx_undo_move.release();
		// music
		msc_menu.release();
		msc_game.release();
		msc_draw.release();
		msc_lose.release();
		msc_win.release();
	}
	
}
