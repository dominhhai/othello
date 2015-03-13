package net.gamehd.othello;

import java.util.ArrayList;

import net.gamehd.othello.agent.ComputerAgent;
import net.gamehd.othello.constants.GameConstants;
import net.gamehd.othello.pool.PiecePool;
import net.gamehd.othello.util.HighScore;
import net.gamehd.othello.util.SoundManager;
import net.gamehd.othello.util.Unity;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * (c) 2012 HEDSPI - SoICT - HUST.
 * 
 * This class is the main class of game. 
 * This will construct game engine, game's camera, game's options.<br/>
 * Moreover, this will manager all game's scenes. Such as, External Menu, Game Mode, Options, Help, High Scores, result of game.
 * 
 * @author Hai Do Minh
 * @author Binh Nguyen Duc
 * @author Nhat Dinh Van
 * @since 04.04.2012
 */

public class OthelloActivity extends SimpleBaseGameActivity implements GameConstants, IOnSceneTouchListener, IUpdateHandler {

	public final static int DIALOG_EXIT_ID = 0;
	public final static int DIALOG_SETTING_ID = 1;
	public final static int DIALOG_BACK_ID = 2;
	public final static int DIALOG_SAVE_SCORE_ID = 3;
	
	public byte currentGameScene = SCENE_MENU;
	
	private BitmapTextureAtlas mMenuBackgroundAtlas;
	private ITextureRegion     mMenuBackgroundRegion;
	
	private BitmapTextureAtlas mMenuItemsAtlas;
	private ITextureRegion     mMenuItemPlayRegion;
	private ITextureRegion     mMenuItemOptionsRegion;
	private ITextureRegion     mMenuItemHighScoresRegion;
	private ITextureRegion     mMenuItemHelpRegion;
	private ITextureRegion     mMenuTitleRegion;
	
	
	private BitmapTextureAtlas	mBackgroundAtlas;
	private ITextureRegion      mBackgroundRegion;
	
	private BitmapTextureAtlas  mBoardAtlas;
	private ITextureRegion      mBoardRegion;
	
	private BitmapTextureAtlas  mPlayerInfoAtlas;
	private ITextureRegion      mPlayerInfoRegion;
	
	private BitmapTextureAtlas	mPieceAtlas;
	private TiledTextureRegion  mPieceRegion;
	
	private BitmapTextureAtlas  mHelperAtlas;
	private ITextureRegion      mHintRegion;
	private ITextureRegion      mLastmoveRegion;
	
	private BitmapTextureAtlas mStateInfoAtlas;
	private TiledTextureRegion mStateBlackRegion;
	private TiledTextureRegion mStateWhiteRegion;
	
	private BitmapTextureAtlas mEndgameBackgroundAtlas;
	private ITextureRegion     mEndgameBackgroundRegion;
	private BitmapTextureAtlas mEndgameButtonAtlas;
	private ITextureRegion     mEndgameUndoRegion;
	private ITextureRegion     mEndgameNewRegion;
	private CameraScene               mHUDEndGame;
	
	private Font mFontSmall;
	private Font mFontBig;
	
	private byte[][] boardMap = new byte[NUM_COL][NUM_ROW];
	private AnimatedSprite[][] boardEntity = new AnimatedSprite[NUM_COL][NUM_COL];
	private ArrayList<Sprite>  mHintSprites = new ArrayList<Sprite>();
	
	private PiecePool  mPiecePool;
	
	private Sprite    mLastmoveSprite;
	
	private Rectangle mInputDetectX;
	private Rectangle mInputDetectY;
	
	private boolean isPlayerSide;
	private byte playerSide = BLACK_PIECE;
	private byte autoSide   = WHITE_PIECE;
	private byte playerTotal = 2;
	private byte autoTotal   = 2;
	private int playerStep = 0;
	private int autoStep   = 0;
	
	private AnimatedSprite playerState;
	private AnimatedSprite autoState;
	
	private Text playerName;
	private Text autoName;
	private Text playerTotalText;
	private Text autoTotalText;
	private Text playerStepText;
	private Text autoStepText;
	
	private ComputerAgent mComputerAgent;
	private byte computerPutCell  = -1;
	private int computerWayIndex = -1;
	
	private ArrayList<ArrayList<Byte>> mValidWaysGraph = new ArrayList<ArrayList<Byte>>();
	private byte[] playerInput = null;

	private boolean isSettingShow = false;
	
	private boolean isRestoreHistory = false;
	private ArrayList<byte[][]> boardHistory = new  ArrayList<byte[][]>();
	private ArrayList<Byte> lastmoveHistory = new ArrayList<Byte>();
	private byte preLastmove;
	private Scene mainSceneSave = null;
	
	//@Override
	public EngineOptions onCreateEngineOptions() {
		Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions mEngineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), mCamera);
		mEngineOptions.getAudioOptions().setNeedsSound(true).setNeedsMusic(true);
		return mEngineOptions;
	}

	@Override
	protected void onCreateResources() {
		// music
		SoundManager.loadSoundResource(this);
		// menu game
		onCreateGameResources();
	}
	
	//@Override
	protected void onCreateGameResources() {
		final VertexBufferObjectManager VBOManager = this.getVertexBufferObjectManager();
		final TextureManager mTextureManager = this.mEngine.getTextureManager();
		final AssetManager   mAssetManager	 = this.getAssets();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		/* Load Menu Scene. */
		this.mMenuBackgroundAtlas = new BitmapTextureAtlas(mTextureManager, 800, 175, TextureOptions.BILINEAR);
		this.mMenuBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBackgroundAtlas, mAssetManager, "mn_background.png", 0, 0);
		this.mMenuBackgroundAtlas.load();
		this.mMenuItemsAtlas = new BitmapTextureAtlas(mTextureManager, 666, 380, TextureOptions.BILINEAR);
		this.mMenuItemPlayRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuItemsAtlas, mAssetManager, "mn_play.png", 0, 0);
		this.mMenuItemOptionsRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuItemsAtlas, mAssetManager, "mn_options.png", 0, 95);
		this.mMenuItemHighScoresRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuItemsAtlas, mAssetManager, "mn_highscore.png", 0, 190);
		this.mMenuItemHelpRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuItemsAtlas, mAssetManager, "mn_help.png", 0, 285);
		this.mMenuTitleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuItemsAtlas, mAssetManager, "mn_title.png", 341 , 0);
		this.mMenuItemsAtlas.load();
		/* Load Main Scene. */
		// loadfont
		FontFactory.setAssetBasePath("font/");
		final ITexture fontSmallTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		this.mFontSmall = FontFactory.createFromAsset(this.getFontManager(), fontSmallTexture, this.getAssets(), "UVNDaLat_R.ttf", 30, true, Color.WHITE);
		this.mFontSmall.load();
		final ITexture fontBigTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		this.mFontBig = FontFactory.createFromAsset(this.getFontManager(), fontBigTexture, this.getAssets(), "UVNMangCau_B.ttf", 50, true, Color.WHITE);
		this.mFontBig.load();
		
		this.playerName = new Text(2, 50, mFontSmall, "You", VBOManager);
		this.autoName = new Text(646, 50, mFontSmall, "Computer", VBOManager);
		this.playerTotalText = new Text(20, 195, mFontSmall, "total : 00", VBOManager);
		this.autoTotalText = new Text(663, 195, mFontSmall, "total : 00", VBOManager);
		this.playerStepText = new Text(20, 255, mFontSmall, "step : 00", VBOManager);
		this.autoStepText = new Text(663, 255, mFontSmall, "step : 00", VBOManager);
		// load image
		this.mBackgroundAtlas = new BitmapTextureAtlas(mTextureManager, 800, 480, TextureOptions.BILINEAR);
		this.mBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundAtlas, mAssetManager, "game_background.png", 0, 0);
		this.mBackgroundAtlas.load();
		
		this.mBoardAtlas = new BitmapTextureAtlas(mTextureManager, 480, 480, TextureOptions.BILINEAR);
		this.mBoardRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBoardAtlas, mAssetManager, "playfield_background_wood.png", 0, 0);
		this.mBoardAtlas.load();
		
		this.mPlayerInfoAtlas = new BitmapTextureAtlas(mTextureManager, 140, 292, TextureOptions.BILINEAR);
		this.mPlayerInfoRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPlayerInfoAtlas, mAssetManager, "player_info.png", 0, 0);
		this.mPlayerInfoAtlas.load();
		
		this.mPieceAtlas = new BitmapTextureAtlas(mTextureManager, 240, 180, TextureOptions.BILINEAR);
		this.mPieceRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mPieceAtlas, mAssetManager, "flip_wood_4x12.png", 0, 0, 4, 3);
		this.mPieceAtlas.load();
		
		this.mHelperAtlas = new BitmapTextureAtlas(mTextureManager, 60, 120, TextureOptions.BILINEAR);
		this.mHintRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mHelperAtlas, mAssetManager, "hint_wood.png", 0, 0);
		this.mLastmoveRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mHelperAtlas, mAssetManager, "lastmove_wood.png", 0, 60);
		this.mHelperAtlas.load();
		
		this.mStateInfoAtlas = new BitmapTextureAtlas(mTextureManager, 266, 152, TextureOptions.BILINEAR);
		this.mStateBlackRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mStateInfoAtlas, mAssetManager, "state_black_wood.png", 0, 0, 7, 2);
		this.mStateWhiteRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mStateInfoAtlas, mAssetManager, "state_white_wood.png", 0, 76, 7, 2);
		this.mStateInfoAtlas.load();
		
		this.mEndgameBackgroundAtlas = new BitmapTextureAtlas(mTextureManager, 400, 400, TextureOptions.BILINEAR);
		this.mEndgameBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mEndgameBackgroundAtlas, mAssetManager, "endgame.png", 0, 0);
		this.mEndgameBackgroundAtlas.load();
		
		this.mEndgameButtonAtlas = new BitmapTextureAtlas(mTextureManager, 120, 200, TextureOptions.BILINEAR);
		this.mEndgameUndoRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mEndgameButtonAtlas, mAssetManager, "inter_undo_button.png", 0, 0);
		this.mEndgameNewRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mEndgameButtonAtlas, mAssetManager, "inter_newgame_button.png", 0, 100);
		this.mEndgameButtonAtlas.load();
	}

	@Override
	protected Scene onCreateScene() {
		return onCreateMenuScene();
	}
	
	private void switchScene(final Scene scene, final boolean isClear) {
		if(this.mEngine.getScene() != scene) {
			this.mEngine.getScene().postRunnable(new Runnable(){

				public void run() {
					if(isClear) {
						OthelloActivity.this.clearCurrentScene();
					} else {
						OthelloActivity.this.saveCurrentScene();
					}
					OthelloActivity.this.mEngine.setScene(scene);
				}
			});
		}
	}
	
	private void clearCurrentScene() {
		Scene scene = this.mEngine.getScene(); 
		scene.clearUpdateHandlers();
		scene.clearTouchAreas();
		scene.clearEntityModifiers();
		scene.clearChildScene();
		scene.detachChildren();
		scene.dispose();
	}
	
	private void saveCurrentScene() {
		this.mainSceneSave = this.mEngine.getScene();
		this.mainSceneSave.detachSelf();
	}
	
	protected Scene onCreateMenuScene() {
		/* music effect. */
		if (GameConfig.enableMusic) {
			if (SoundManager.msc_game.isPlaying()) {
				SoundManager.msc_game.pause();
			}
			if (!SoundManager.msc_menu.isPlaying()) {
				SoundManager.msc_menu.play();
			}
		} else {
			if (SoundManager.msc_menu.isPlaying()) {
				SoundManager.msc_menu.pause();
			}
		}
		final Scene mScene = new Scene();
		
		final VertexBufferObjectManager mVBOManager = this.mEngine.getVertexBufferObjectManager();
		mScene.attachChild(new Sprite(16, 18, this.mMenuTitleRegion, mVBOManager));
		mScene.attachChild(new Sprite(0, CAMERA_HEIGHT - this.mMenuBackgroundRegion.getHeight(), this.mMenuBackgroundRegion, mVBOManager));
		final ButtonSprite playButton = new ButtonSprite(370, 20, this.mMenuItemPlayRegion, mVBOManager, new ButtonSprite.OnClickListener() {
			
			public void onClick(ButtonSprite arg0, float arg1, float arg2) {
				OthelloActivity.this.switchScene(OthelloActivity.this.onCreateMainScene(), true);
			}
		}) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					this.setScale(1.0f, 1.0f);
				} else {
					this.setScale(1.5f, 1.2f);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		final ButtonSprite optionsButton = new ButtonSprite(370, 130, this.mMenuItemOptionsRegion, mVBOManager, new ButtonSprite.OnClickListener() {
			
			public void onClick(ButtonSprite arg0, float arg1, float arg2) {
				startActivity(new Intent(getApplicationContext(), OptionsActivity.class));
				mEngine.stop();
			}
		}) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					this.setScale(1.0f, 1.0f);
				} else {
					this.setScale(1.5f, 1.2f);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		final ButtonSprite highscoresButton = new ButtonSprite(370, 240, this.mMenuItemHighScoresRegion, mVBOManager, new ButtonSprite.OnClickListener() {
			
			public void onClick(ButtonSprite arg0, float arg1, float arg2) {
				startActivity(new Intent(getApplicationContext(), HighScoreActivity.class));
				mEngine.stop();
			}
		}) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					this.setScale(1.0f, 1.0f);
				} else {
					this.setScale(1.5f, 1.2f);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		final ButtonSprite helpButton = new ButtonSprite(370, 350, this.mMenuItemHelpRegion, mVBOManager, new ButtonSprite.OnClickListener() {
			
			public void onClick(ButtonSprite arg0, float arg1, float arg2) {
				startActivity(new Intent(getApplicationContext(), HelpActivity.class));
				mEngine.stop();
			}
		}) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					this.setScale(1.0f, 1.0f);
				} else {
					this.setScale(1.5f, 1.2f);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		mScene.attachChild(playButton);
		mScene.attachChild(optionsButton);
		mScene.attachChild(highscoresButton);
		mScene.attachChild(helpButton);
		
		mScene.registerTouchArea(playButton);
		mScene.registerTouchArea(optionsButton);
		mScene.registerTouchArea(highscoresButton);
		mScene.registerTouchArea(helpButton);
		
		this.currentGameScene = SCENE_MENU;
		return mScene;
	}
	
	//@Override
	protected Scene onCreateMainScene() {
		/* music effect. */
		if (GameConfig.enableMusic) {
			if (SoundManager.msc_menu.isPlaying()) {
				SoundManager.msc_menu.pause();
			}
			if (!SoundManager.msc_game.isPlaying()) {
				SoundManager.msc_game.play();
			}
		} else {
			if (SoundManager.msc_game.isPlaying()) {
				SoundManager.msc_game.pause();
			}
		}
		
		this.currentGameScene = SCENE_PLAY;
		if (mainSceneSave != null) {
			if(!lastmoveHistory.isEmpty()) {
				mLastmoveSprite.setVisible(GameConfig.enableLastmove);
			}
			showHints(mainSceneSave);
			return mainSceneSave;
		}
		final Scene mScene = new Scene();
		mScene.registerUpdateHandler(new FPSLogger());
		final VertexBufferObjectManager mVBOManager = this.mEngine.getVertexBufferObjectManager();
		this.mPiecePool = new PiecePool(this.mPieceRegion, mVBOManager);
		mScene.attachChild(new Sprite(0, 0, this.mBackgroundRegion, mVBOManager));
		mScene.attachChild(new Sprite(BOARD_PX, BOARD_PY, this.mBoardRegion, mVBOManager));
		
		mScene.attachChild(new Sprite(7, 60, this.mPlayerInfoRegion, mVBOManager));
		mScene.attachChild(new Sprite(650, 60, this.mPlayerInfoRegion, mVBOManager));
		
		this.playerState = new AnimatedSprite(7 + (this.mPlayerInfoRegion.getWidth() - this.mStateBlackRegion.getWidth()) / 2, 145, this.mStateBlackRegion, mVBOManager);
		this.autoState = new AnimatedSprite(650 + (this.mPlayerInfoRegion.getWidth() - this.mStateWhiteRegion.getWidth()) / 2, 145, this.mStateWhiteRegion, mVBOManager);
		mScene.attachChild(this.playerState);
		mScene.attachChild(this.autoState);
		
		this.playerTotalText.setText("total : " + this.playerTotal);
		this.autoTotalText.setText("total : " + this.autoTotal);
		this.playerStepText.setText("step : " + this.playerStep);
		this.autoStepText.setText("step : " + this.autoStep);
		
		this.playerName.setColor(0.0f, 0.0f, 1.0f);
		this.autoName.setColor(0.0f, 0.0f, 1.0f);
		this.playerName.setPosition(7 + (this.mPlayerInfoRegion.getWidth() - this.playerName.getWidth()) / 2, 80);
		this.autoName.setPosition(650 + (this.mPlayerInfoRegion.getWidth() - this.autoName.getWidth()) / 2, 80);
		mScene.attachChild(this.playerName);
		mScene.attachChild(this.autoName);
		this.playerTotalText.setColor(1.0f, 0.0f, 0.0f);
		this.autoTotalText.setColor(1.0f, 0.0f, 0.0f);
		mScene.attachChild(this.playerTotalText);
		mScene.attachChild(this.autoTotalText);
		this.playerStepText.setColor(1.0f, 0.0f, 0.0f);
		this.autoStepText.setColor(1.0f, 0.0f, 0.0f);
		mScene.attachChild(playerStepText);
		mScene.attachChild(this.autoStepText);
		
		/* initial state. */
		this.playerInput = new byte[2];
		this.mComputerAgent = new ComputerAgent();
		Rectangle mRectangle;
		for(byte col = 0; col < NUM_COL; col ++) {
			// space line
			final float[] mPosition = Unity.indexToPosition(col, col);
			mRectangle = new Rectangle(mPosition[COL_INDEX], BOARD_PY, 1, BOARD_HEIGHT, mVBOManager);
			mRectangle.setColor(0.0f, 0.0f, 1.0f, 0.4f);
			mScene.attachChild(mRectangle);
			mRectangle = new Rectangle(BOARD_PX, mPosition[ROW_INDEX], BOARD_WIDTH, 1, mVBOManager);
			mRectangle.setColor(0.0f, 0.0f, 1.0f, 0.4f);
			mScene.attachChild(mRectangle);
		}
		
		this.mInputDetectX = new Rectangle(0, 0, BOARD_WIDTH, PIECE_HEIGHT, mVBOManager);
		this.mInputDetectY = new Rectangle(0, 0, PIECE_WIDTH, BOARD_HEIGHT, mVBOManager);
		this.mInputDetectX.setColor(0.0f, 0.0f, 0.3f, 0.3f);
		this.mInputDetectY.setColor(0.0f, 0.0f, 0.3f, 0.3f);
		this.mInputDetectX.setVisible(false);
		this.mInputDetectY.setVisible(false);
		mScene.attachChild(this.mInputDetectX);
		mScene.attachChild(this.mInputDetectY);
		
		this.mLastmoveSprite = new Sprite(0, 0, this.mLastmoveRegion, mVBOManager);
		this.mLastmoveSprite.setVisible(false);
		mScene.attachChild(this.mLastmoveSprite);
		
		initState(mScene);
		
		mScene.setOnSceneTouchListener(this);
		
		mScene.registerUpdateHandler(this);
		
		initChildScene(mScene);
		
		return mScene;
	}

	private void showChildScene(final Scene mScene, final byte win, final int playerAll, final int autoAll) {
		final VertexBufferObjectManager mVBOManager = this.mEngine.getVertexBufferObjectManager();
		String message = "Congratulation";
		String youwin = "You Won";
		if (win < 1) {
			message = "Try your best";
			if(win < 0) {
				youwin = "You Lost";
			} else {
				youwin = "You Draw";
			}
		}
		Text congraText = new Text(0, 0, this.mFontBig, message, mVBOManager);
		congraText.setPosition((CAMERA_WIDTH - congraText.getWidth()) / 2, 50);
		congraText.setColor(1.0f, 0.0f, 0.0f);
		congraText.setUserData("textData");
		this.mHUDEndGame.attachChild(congraText);
		Text winer = new Text(0, 0, this.mFontBig, youwin, mVBOManager);
		winer.setPosition((CAMERA_WIDTH - winer.getWidth()) / 2, 120);
		winer.setColor(0.0f, 0.0f, 1.0f);
		winer.setUserData("textData");
		this.mHUDEndGame.attachChild(winer);
		Text result = new Text(0, 0, this.mFontBig, "Result : " + playerAll + " - " + autoAll, mVBOManager);
		result.setPosition((CAMERA_WIDTH - result.getWidth()) / 2, 180);
		result.setColor(1.0f, 1.0f, 0.0f);
		result.setUserData("textData");
		this.mHUDEndGame.attachChild(result);
		
		this.currentGameScene = SCENE_END;
		mScene.setChildScene(mHUDEndGame, false, true, true);
	}
	
	private void initChildScene(final Scene mScene) {
		final VertexBufferObjectManager mVBOManager = this.mEngine.getVertexBufferObjectManager();
		this.mHUDEndGame = new CameraScene(mEngine.getCamera());
		this.mHUDEndGame.setBackgroundEnabled(false);
		this.mHUDEndGame.attachChild(new Sprite(200, 40, this.mEndgameBackgroundRegion, mVBOManager));
		final ButtonSprite mUndoButton = new ButtonSprite(218, 332, this.mEndgameUndoRegion, mVBOManager, new ButtonSprite.OnClickListener() {
			
			public void onClick(ButtonSprite arg0, float arg1, float arg2) {
				isRestoreHistory = true;
				currentGameScene = SCENE_PLAY; 
				mScene.clearChildScene();
				mHUDEndGame.detachChildren(new IEntityMatcher() {
					
					public boolean matches(IEntity arg0) {
						String textData = (String) arg0.getUserData();
						return textData != null && textData.equals("textData");
					}
				});
				if (GameConfig.enableMusic) {
					if (!SoundManager.msc_game.isPlaying()) {
						SoundManager.msc_game.play();
					}
				} else {
					if (SoundManager.msc_game.isPlaying()) {
						SoundManager.msc_game.pause();
					}
				}
			}
		}) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					this.setScale(1.0f, 1.0f);
				} else {
					this.setScale(1.5f, 1.2f);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		final ButtonSprite mNewButton = new ButtonSprite(464, 332, this.mEndgameNewRegion, mVBOManager, new ButtonSprite.OnClickListener() {
			
			public void onClick(ButtonSprite arg0, float arg1, float arg2) {
				currentGameScene = SCENE_PLAY; 
				clearAllState();
				initState(getEngine().getScene());
				mScene.clearChildScene();
				mHUDEndGame.detachChildren(new IEntityMatcher() {
					
					public boolean matches(IEntity arg0) {
						String textData = (String) arg0.getUserData();
						return textData != null && textData.equals("textData");
					}
				});
				if (GameConfig.enableMusic) {
					if (!SoundManager.msc_game.isPlaying()) {
						SoundManager.msc_game.play();
					}
				} else {
					if (SoundManager.msc_game.isPlaying()) {
						SoundManager.msc_game.pause();
					}
				}
			}
		}) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					this.setScale(1.0f, 1.0f);
				} else {
					this.setScale(1.5f, 1.2f);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		this.mHUDEndGame.attachChild(mUndoButton);
		this.mHUDEndGame.attachChild(mNewButton);
		this.mHUDEndGame.registerTouchArea(mUndoButton);
		this.mHUDEndGame.registerTouchArea(mNewButton);
	}
	//@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		final float touchX = pSceneTouchEvent.getX();
		final float touchY = pSceneTouchEvent.getY();
		final byte[] mIndex = Unity.positionToIndex(touchX, touchY);
		if(mIndex == null) {
			System.err.println("Error : OutOfBound Touch");
			return false;
		} else {
			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
				this.mInputDetectX.setVisible(false);
				this.mInputDetectY.setVisible(false);
				if (isPlayerSide && playerInput[COL_INDEX] == -1 && this.boardMap[mIndex[COL_INDEX]][mIndex[ROW_INDEX]] == NONE_PIECE) {
					System.out.println("index : " + mIndex[COL_INDEX] +", " + mIndex[ROW_INDEX]);
					this.playerInput[COL_INDEX] = mIndex[COL_INDEX];
					this.playerInput[ROW_INDEX] = mIndex[ROW_INDEX];
				}
			} else {
				this.mInputDetectX.setPosition(BOARD_PX, BOARD_PY + PIECE_HEIGHT * mIndex[ROW_INDEX]);
				this.mInputDetectY.setPosition(BOARD_PX + PIECE_WIDTH * mIndex[COL_INDEX], BOARD_PY);
				this.mInputDetectX.setVisible(true);
				this.mInputDetectY.setVisible(true);
			}
		}
		return true;
	}
	
	
	private void endGameProcess(int playerAll, int autoAll) {
		byte win = 0;
		/* music effect. */
		if (SoundManager.msc_game.isPlaying()) {
			SoundManager.msc_game.pause();
		}
		if (playerAll > autoAll) {
			win = 1;
			if (GameConfig.enableMusic) {
				if (!SoundManager.msc_win.isPlaying()) {
					SoundManager.msc_win.play();
				}
			}
		} else if (playerAll < autoAll) {
			win = -1;
			if (GameConfig.enableMusic) {
				if (!SoundManager.msc_lose.isPlaying()) {
					SoundManager.msc_lose.play();
				}
			}
		} else {
			if (GameConfig.enableMusic) {
				if (!SoundManager.msc_draw.isPlaying()) {
					SoundManager.msc_draw.play();
				}
			}
		}
		this.playerInput[COL_INDEX] = -1;
		this.isPlayerSide = true;
		showChildScene(getEngine().getScene(), win, playerAll, autoAll);
		if (win ==1 && HighScore.getInstance(getApplicationContext()).isHighscore(playerAll)) {
			runOnUiThread(new Runnable() {
				public void run() {
					showDialog(DIALOG_SAVE_SCORE_ID);
				}
			});
		}
	}
	
	private void initState(Scene mScene) {
		this.isPlayerSide = GameConfig.isPlayerSide;
		this.playerInput[COL_INDEX] = -1;
		for (byte col = 0; col < NUM_COL; col ++) {
			for (byte row = 0; row < NUM_ROW; row ++) {
				byte type = NONE_PIECE;
				
				// initial 4 first pieces at the center
				if((col == 3 && row == 3)|| (col == 4 && row == 4)) {
					type = WHITE_PIECE;
				} else if((col == 3 && row == 4)|| (col == 4 && row == 3)) {
					type = BLACK_PIECE;
				}
				
				this.boardMap[col][row] = type;
				if(type != NONE_PIECE) {
					final AnimatedSprite mAnimatedSprite = mPiecePool.obtainPoolItem(type, col, row); 
					this.boardEntity[col][row] = mAnimatedSprite;
					mScene.attachChild(mAnimatedSprite);
				}
			}
		}
		Unity.searchAllValidWays(this.isPlayerSide ? this.playerSide : this.autoSide, this.boardMap, this.mValidWaysGraph);
		if (!getEngine().isRunning()) {
			getEngine().start();
		}
		/* show hints. */
		showHints(mScene);
	}
	
	
	private void showHints(Scene mScene) {
		int counter = 0;
		if(GameConfig.enableHint && this.isPlayerSide) {
			for (ArrayList<Byte> validWay : this.mValidWaysGraph) {
				byte[] hintIndex = Unity.cellToIndex(validWay.get(0));
				if(this.boardMap[hintIndex[COL_INDEX]][hintIndex[ROW_INDEX]] == NONE_PIECE) {
					Sprite mHintSprite;
					if (counter < this.mHintSprites.size()) {
						mHintSprite = this.mHintSprites.get(counter);
					} else {
						mHintSprite = new Sprite(0, 0, this.mHintRegion, mEngine.getVertexBufferObjectManager());
						mScene.attachChild(mHintSprite);
						this.mHintSprites.add(mHintSprite);
					}
					
					final float[] mPosition = Unity.indexToPosition(hintIndex[COL_INDEX], hintIndex[ROW_INDEX]);
					mHintSprite.setPosition(mPosition[0], mPosition[1]);
					mHintSprite.setVisible(true);
					counter ++;
				}
			}
		}
		for (; counter < this.mHintSprites.size(); counter ++) {
			this.mHintSprites.get(counter).setVisible(false);
		}
		
		if (this.isPlayerSide) {
			this.playerState.animate(ANIMATE_DURATION, 2, 13, true);
			this.autoState.stopAnimation(0);
		} else {
			this.autoState.animate(ANIMATE_DURATION, 2, 13, true);
			this.playerState.stopAnimation(0);
		}
		
	}
	
	private void clearAllState() {
		for (byte col = 0; col < NUM_COL; col ++) {
			for (byte row = 0; row < NUM_ROW; row ++) {
				if (this.boardEntity[col][row] != null) {
					final AnimatedSprite mRecycle = this.boardEntity[col][row];
					this.boardEntity[col][row] = null;
					getEngine().getScene().postRunnable(new Runnable() {
						public void run() {
							mPiecePool.recyclePoolItem(mRecycle);
						}
					});
				}
			}
		}
		
		this.boardHistory.clear();
		this.lastmoveHistory.clear();
		
		this.mLastmoveSprite.setVisible(false);
		
		this.playerTotal = 2;
		this.autoTotal   = 2;
		this.playerStep = 0;
		this.autoStep   = 0;
		
		this.playerTotalText.setText("total : " + this.playerTotal);
		this.autoTotalText.setText("total : " + this.autoTotal);
		this.playerStepText.setText("step : " + this.playerStep);
		this.autoStepText.setText("step : " + this.autoStep);
	}
	
	private void updateStates(final boolean currentSide) {
		boolean playerSide = !currentSide;
		this.playerTotalText.setText("total : " + this.playerTotal);
		this.autoTotalText.setText("total : " + this.autoTotal);
		this.playerStepText.setText("step : " + this.playerStep);
		this.autoStepText.setText("step : " + this.autoStep);
		if(this.playerTotal == 0 || this.autoTotal == 0 || ((this.playerTotal + this.autoTotal) == NUM_CELL)) {
			endGameProcess(this.playerTotal, this.autoTotal);
		} else {
			Unity.searchAllValidWays(playerSide ? this.playerSide : this.autoSide, this.boardMap, this.mValidWaysGraph);
			 //no way
			if (this.mValidWaysGraph.isEmpty()) {
				playerSide = currentSide;
				Unity.searchAllValidWays(playerSide ? this.playerSide : this.autoSide, this.boardMap, this.mValidWaysGraph);
				if (this.mValidWaysGraph.isEmpty()) {
					endGameProcess(this.playerTotal, this.autoTotal);		
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(), ( currentSide ? "Computer" : "You" ) +  " have no valid way, " + ( !currentSide ? "computer" : "you" ) + " will go.", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
			this.playerInput[COL_INDEX] = -1;
			this.isPlayerSide = playerSide;
			/* show hints. */
			showHints(getEngine().getScene());
			/* computer action. */
			if (!this.isPlayerSide) {
				this.computerPutCell  = -1;
				this.computerWayIndex = -1;
				new Thread(new Runnable() {
					public void run() {
						System.out.println("Begin thread computer action");
						long startTime = System.currentTimeMillis();
						computerPutCell = mComputerAgent.action(boardMap);
						computerWayIndex = computerPutCell == -1 ? -1 : Unity.findCellInValidWaysGraph(computerPutCell, mValidWaysGraph);
						System.out.println("Completed thread computer action in : " + (System.currentTimeMillis() - startTime));
					}
				}).start();
			}
		}
	}
	
	private void saveHistory() {
		byte[][] savingData = new byte[NUM_COL][NUM_ROW];
		for(int i = 0; i < NUM_COL; i ++) {
			System.arraycopy(this.boardMap[i], 0, savingData[i], 0, NUM_ROW);
		}
		this.boardHistory.add(savingData);
		this.lastmoveHistory.add(Byte.valueOf(this.preLastmove));		
	}
	
	private void restoreFromHistory() {
		this.isRestoreHistory = false;
		if (this.boardHistory.isEmpty()) {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getBaseContext(), "Can't undo.", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			this.playerStep --;
			this.autoStep --;
			this.boardMap = this.boardHistory.remove(this.boardHistory.size() - 1);
			this.preLastmove = this.lastmoveHistory.remove(this.lastmoveHistory.size() - 1);
			this.playerTotal = 0;
			this.autoTotal   = 0;
			for (byte col = 0; col <NUM_COL; col ++) {
				for (byte row = 0; row < NUM_ROW; row ++) {
					if (this.boardMap[col][row] == BLACK_PIECE) {
						this.playerTotal ++;
						if ((this.boardEntity[col][row] != null)) {
							this.boardEntity[col][row].setCurrentTileIndex(BLACK_PIECE_INDEX);
						} else {
							this.boardEntity[col][row] = this.mPiecePool.obtainPoolItem(BLACK_PIECE, col, row);
							getEngine().getScene().attachChild(this.boardEntity[col][row]);
						}
					} else if (this.boardMap[col][row] == WHITE_PIECE) {
						this.autoTotal ++;
						if ((this.boardEntity[col][row] != null)) {
							this.boardEntity[col][row].setCurrentTileIndex(WHITE_PIECE_INDEX);
						} else {
							this.boardEntity[col][row] = this.mPiecePool.obtainPoolItem(WHITE_PIECE, col, row);
							getEngine().getScene().attachChild(this.boardEntity[col][row]);
						}
					} else {
						if (this.boardEntity[col][row] != null) {
							final AnimatedSprite mRecycle = this.boardEntity[col][row];
							this.boardEntity[col][row] = null;
							getEngine().getScene().postRunnable(new Runnable() {
								public void run() {
									mPiecePool.recyclePoolItem(mRecycle);
								}
							});
						}
					}
				}
			}
			final byte[] putIndex = Unity.cellToIndex(this.preLastmove); 
			/* track last move. */
			if (GameConfig.enableLastmove) {
				if (!this.mLastmoveSprite.isVisible()) {
					this.mLastmoveSprite.setVisible(true);
				}
			}
			final float[] mPosition = Unity.indexToPosition(putIndex[COL_INDEX], putIndex[ROW_INDEX]);
			this.mLastmoveSprite.setPosition(mPosition[COL_INDEX], mPosition[ROW_INDEX]);
			Unity.searchAllValidWays(this.playerSide, this.boardMap, this.mValidWaysGraph);
			/* show hints. */
			showHints(getEngine().getScene());
			if(this.boardHistory.isEmpty()) {
				this.mLastmoveSprite.setVisible(false);
			}
			this.playerTotalText.setText("total : " + this.playerTotal);
			this.autoTotalText.setText("total : " + this.autoTotal);
			this.playerStepText.setText("step : " + this.playerStep);
			this.autoStepText.setText("step : " + this.autoStep);
		}
	}
	
	//@Override
	public void onUpdate(float pSecondsElapsed) {
		if((!this.isPlayerSide && this.playerInput[COL_INDEX] == -1) || (this.isPlayerSide && this.playerInput[COL_INDEX] != -1)) {
			final byte pType = this.isPlayerSide ? this.playerSide : this.autoSide;
			final int[] frameAnimate = this.isPlayerSide ? WHILE_TO_BLACK : BLACK_TO_WHITE;
			byte putCell = -1;
			int  wayIndex = -1;
			/* input index. */
			if (this.isPlayerSide) {
				// wait for input
				if (this.playerInput[COL_INDEX] > -1) {
					putCell = Unity.indexToCell(this.playerInput[COL_INDEX], this.playerInput[ROW_INDEX]);
					wayIndex = Unity.findCellInValidWaysGraph(putCell, this.mValidWaysGraph);
					if (wayIndex != -1) {
						/* save pre step. */
						saveHistory();
						this.preLastmove = putCell;
					}
					Debug.e("Player action");
				}
			} else {
				// agent action
//				wayIndex = this.mComputerAgent.action(this.boardMap);
//				putCell = this.mValidWaysGraph.get(wayIndex).get(0).byteValue();
//				putCell = this.mComputerAgent.action(this.boardMap);
//				wayIndex = putCell == -1 ? -1 : Unity.findCellInValidWaysGraph(putCell, this.mValidWaysGraph);
				if (this.computerPutCell != -1 && this.computerWayIndex != -1) {
					wayIndex = this.computerWayIndex;
					putCell  = this.computerPutCell;
				}
			}
			/* handle input index. */
			if(wayIndex == -1) {
				/* sound effect. */
				if (GameConfig.enableSound && this.isPlayerSide) {
					SoundManager.sfx_invalid_move.play();
				}
				/* continue wait player input. */
				this.playerInput[COL_INDEX] = -1;
			} else {
				/* sound effect. */
				if (GameConfig.enableSound) {
					 if (this.isPlayerSide) {
						 SoundManager.sfx_player1_move.play();
					 } else {
						 SoundManager.sfx_player2_move.play();
					 }
				}
				
				if(this.isPlayerSide) {
					this.playerStep ++;
					this.playerTotal ++;
				} else {
					this.autoStep ++;
					this.autoTotal ++;
				}
				final byte[] putIndex = Unity.cellToIndex(putCell); 
				/* track last move. */
				if (GameConfig.enableLastmove) {
					if (!this.mLastmoveSprite.isVisible()) {
						this.mLastmoveSprite.setVisible(true);
					}
				}
				final float[] mPosition = Unity.indexToPosition(putIndex[COL_INDEX], putIndex[ROW_INDEX]);
				this.mLastmoveSprite.setPosition(mPosition[COL_INDEX], mPosition[ROW_INDEX]);
				/* add new index. */
				this.boardMap[putIndex[COL_INDEX]][putIndex[ROW_INDEX]] = pType;
				final AnimatedSprite mAnimatedSprite = mPiecePool.obtainPoolItem(pType, putIndex[COL_INDEX], putIndex[ROW_INDEX]); 
				this.boardEntity[putIndex[COL_INDEX]][putIndex[ROW_INDEX]] = mAnimatedSprite;
				mEngine.getScene().attachChild(mAnimatedSprite);
				/* change pieces between input index and owner. */
				ArrayList<Byte> neighbours = this.mValidWaysGraph.get(wayIndex);
				final boolean isPlayerSideConstance = this.isPlayerSide;
				this.isPlayerSide = false;
				this.playerInput[COL_INDEX] = -2;
				for (int i = neighbours.size() - 1; i > 0; i --) {
					ArrayList<Byte> betweenCells = Unity.getBetweenCells(putCell, neighbours.get(i).byteValue(), this.boardMap);
					final int betweenCellsSize = betweenCells.size();
					if(isPlayerSideConstance) {
						this.playerTotal += betweenCellsSize;
						this.autoTotal   -= betweenCellsSize;
					} else {
						this.autoTotal   += betweenCellsSize;
						this.playerTotal -= betweenCellsSize;
					}
					for (int j = betweenCellsSize - 1; j >= 0 ; j --) {
						final int index_j = j;
						final int index_i = i;
						final byte betweenCell = betweenCells.get(j).byteValue();
						final byte[] betweenIndex = Unity.cellToIndex(betweenCell);
						this.boardMap[betweenIndex[COL_INDEX]][betweenIndex[ROW_INDEX]] = pType;
						this.boardEntity[betweenIndex[COL_INDEX]][betweenIndex[ROW_INDEX]].animate(ANIMATE_DURATION, frameAnimate, 0, new AnimatedSprite.IAnimationListener() {
							
							//@Override
							public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) { }
							
							//@Override
							public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) { }
							
							//@Override
							public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) { }
							
							//@Override
							public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
								OthelloActivity.this.boardEntity[betweenIndex[COL_INDEX]][betweenIndex[ROW_INDEX]].setCurrentTileIndex(frameAnimate[11]);
								if (index_i == 1 && index_j == 0) {
									/* handle update final state. */
									updateStates(isPlayerSideConstance);
								} else if (index_i == 2 && index_j == betweenCellsSize - 1) {
									/* sound effect. */
									if (GameConfig.enableSound) {
										if (isPlayerSideConstance) {
											SoundManager.sfx_player1_pass.play();
										} else {
											SoundManager.sfx_player2_pass.play();
										}
									}
								}
							}
						});
					}
				}
			}
		} else if(this.isPlayerSide && this.playerInput[COL_INDEX] == -1) {
			/* restore from history. */
			if (this.isRestoreHistory) {
				restoreFromHistory();
			} else if (this.isSettingShow) {
				runOnUiThread(new Runnable() {
					public void run() {
						showDialog(DIALOG_SETTING_ID);
					}
				});
			}
		}
	}
	
	//@Override
	public void reset() { }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean hasAction = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.currentGameScene == SCENE_MENU) {
				hasAction = true;
				getEngine().stop();
				showDialog(DIALOG_EXIT_ID);
			} else if (this.currentGameScene == SCENE_PLAY){
				hasAction = true;
				getEngine().stop();
				showDialog(DIALOG_BACK_ID);
			}
		}
		return hasAction;
	}
	
	@Override
	protected Dialog onCreateDialog(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (id == DIALOG_EXIT_ID) {
			builder.setMessage("Exit Game?");
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					SoundManager.releaseSourceResources();
					finish();
					Runtime.getRuntime().gc();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					getEngine().start();
				}
			});
		} else if (id == DIALOG_SETTING_ID) {
			final CharSequence[] items = {"Turn on Sound", "Turn on Music", "Show Suggest", "Show Last Move"};
			final boolean[] checkedItems = {GameConfig.enableSound, GameConfig.enableMusic, GameConfig.enableHint, GameConfig.enableLastmove};
			builder.setTitle("Setting Game");
			builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
				
				public void onClick(DialogInterface dialog, int which, final boolean isChecked) {
					if (which == 0) {
						GameConfig.enableSound = isChecked;
					} else if (which == 1) {
						GameConfig.enableMusic = isChecked;
						if (GameConfig.enableMusic) {
							if (!SoundManager.msc_game.isPlaying()) {
								SoundManager.msc_game.play();
							}
						} else {
							if (SoundManager.msc_game.isPlaying()) {
								SoundManager.msc_game.pause();
							}
						}
					} else if (which == 2) {
						GameConfig.enableHint = isChecked;
					} else if (which == 3) {
						GameConfig.enableLastmove = isChecked;
					}
				}
			});
		} else if (id == DIALOG_BACK_ID) {
			builder.setMessage("Return Menu?");
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					getEngine().start();
					switchScene(onCreateMenuScene(), false);
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					getEngine().start();
				}
			});
		} else if (id == DIALOG_SAVE_SCORE_ID) {
			builder.setMessage("Save your score into top highest scores");
			final EditText mEditName = new EditText(this);
			mEditName.setSingleLine(true);
			mEditName.setHint("Enter your name here");
			builder.setView(mEditName);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					getEngine().start();
					// save here
					String name = mEditName.getText().toString();
					if (name != null && !name.equals("")) {
						HighScore.getInstance(getApplicationContext()).addScore(name, playerTotal);
					}
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					getEngine().start();
				}
			});
		}
		builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
					if (!getEngine().isRunning()) {
						getEngine().start();
					}
					dialog.dismiss();
					if (id == DIALOG_SETTING_ID) {
						if(!lastmoveHistory.isEmpty()) {
							mLastmoveSprite.setVisible(GameConfig.enableLastmove);
						}
						showHints(getEngine().getScene());
					}
					return true;
				}
				return false;
			}
		});
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if (id == DIALOG_SETTING_ID) {
			this.isSettingShow = false;
			getEngine().stop();
		} else if (id == DIALOG_BACK_ID) {
			getEngine().stop();
		}
		super.onPrepareDialog(id, dialog, args);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 MenuInflater inflater = getMenuInflater();
    	 inflater.inflate(R.menu.internal_menu, menu);
    	 return true;
    }
	    
	 @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		 final boolean isVisible = (this.currentGameScene == SCENE_PLAY);
		 for (int i = menu.size() - 1; i >= 0; i --) {
			 menu.getItem(i).setVisible(isVisible);
		 }
		return super.onPrepareOptionsMenu(menu);
	}
	 
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	int itemId = item.getItemId();
	    	if (itemId == R.id.internalmenu_undo) {
	    		this.isRestoreHistory = true;
	    		/* sound effect. */
	    		if (GameConfig.enableSound) {
	    			SoundManager.sfx_undo_move.play();
	    		}
	    	} else if (itemId == R.id.internalmenu_setting) {
	    		this.isSettingShow = true;
	    	} else if (itemId == R.id.internalmenu_newgame) {
	    		this.clearAllState();
				this.initState(getEngine().getScene());
	    	}
	    	return super.onOptionsItemSelected(item);
	    }
	 
}