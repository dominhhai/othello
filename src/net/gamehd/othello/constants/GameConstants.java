package net.gamehd.othello.constants;

/**
 * (c) 2012 HEDSPI - SoICT - HUST.
 * 
 * Manage all game's constants.
 * 
 * @author Hai Do Minh
 * @author Binh Nguyen Duc
 * @author Nhat Dinh Van
 * @since 04.04.2012
 */

public interface GameConstants {
	
	public final static int CAMERA_WIDTH = 800;
	public final static int CAMERA_HEIGHT = 480;
	
	public final static byte SCENE_SPLASH = 0;
	public final static byte SCENE_MENU   = SCENE_SPLASH + 1;
	public final static byte SCENE_PLAY   = SCENE_MENU + 1; 
	public final static byte SCENE_END	  = SCENE_PLAY + 1;
	
	public final static int BOARD_WIDTH  = CAMERA_HEIGHT;
	public final static int BOARD_HEIGHT = BOARD_WIDTH;
	
	public final static int BOARD_PX = 160;
	public final static int BOARD_PY = 0;
	
	public final static byte NUM_COL = 8;
	public final static byte NUM_ROW = NUM_COL;
	public final static byte NUM_CELL = NUM_COL * NUM_ROW;
	public final static byte PIECE_WIDTH = 60;
	public final static byte PIECE_HEIGHT = PIECE_WIDTH;
	
	public final static byte NONE_PIECE = 0;
	public final static byte BLACK_PIECE = 1;
	public final static byte WHITE_PIECE = BLACK_PIECE + 1;
	
	public final static int BLACK_PIECE_INDEX 		= 0;
	public final static int WHITE_PIECE_INDEX 		= 11;
	public final static int BLACK_TO_WHITE_INDEX 	= BLACK_PIECE_INDEX + 1;
	public final static int WHITE_TO_BLACK_INDEX 	= WHITE_PIECE_INDEX - 1;
	public final static long[] ANIMATE_DURATION = new long[] {80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80};
	public final static int[]  BLACK_TO_WHITE = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
	public final static int[]  WHILE_TO_BLACK = new int[] {11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
	
	public final static byte DIRECTION_NONE  = 0;
	public final static byte DIRECTION_UP    = DIRECTION_NONE + 1;
	public final static byte DIRECTION_DOWN  = DIRECTION_UP   + 1;
	public final static byte DIRECTION_LEFT  = DIRECTION_DOWN + 1;
	public final static byte DIRECTION_RIGHT = DIRECTION_LEFT + 1;
	
	public final static byte COL_INDEX = 0;
	public final static byte ROW_INDEX = COL_INDEX + 1;
	
	public final static int MAX_BETA_VALUE = Integer.MAX_VALUE;
	public final static int MIN_ALPHA_VALUE  = Integer.MIN_VALUE;
	
}
