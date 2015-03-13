package net.gamehd.othello.util;

import java.util.ArrayList;

import net.gamehd.othello.constants.GameConstants;

import org.andengine.util.debug.Debug;

/**
 * (c) 2012 HEDSPI - SoICT - HUST.
 * 
 * Unity of game. This will convert cell, index, position and also find all possible ways of a pieces.
 * 
 * @author Hai Do Minh
 * @author Binh Nguyen Duc
 * @author Nhat Dinh Van
 * @since 04.04.2012
 */

public final class Unity implements GameConstants {

	public static byte[] positionToIndex(final float pX, final float pY) {
		final float realPx = pX - BOARD_PX;
		final float realPy = pY - BOARD_PY;
		
		if(realPx < 0 || realPx > BOARD_WIDTH || realPy < 0 || realPy > BOARD_HEIGHT) {
			Debug.w("OutOfBound (0, " + BOARD_WIDTH + "), but your parameter are : " + realPx +", " + realPy);
			return null;
		} else {
			return (new byte[] {(byte) (realPx / PIECE_WIDTH), (byte) (realPy / PIECE_HEIGHT)});
		}
	}
	
	public static float[] indexToPosition(final byte pCol, final byte pRow) {
		if(pCol < 0 || pCol >= NUM_COL || pRow < 0 || pRow >= NUM_ROW) {
			Debug.e("OutOfBound (0, " + NUM_COL + "), but your parameter are : " + pCol +", " + pRow);
			return null;
		} else {
			return (new float[] {pCol * PIECE_WIDTH + BOARD_PX, pRow * PIECE_HEIGHT + BOARD_PY});
		}
	}
	
	public static byte indexToCell(final byte pCol, final byte pRow) {
		if(pCol < 0 || pCol >= NUM_COL || pRow < 0 || pRow >= NUM_ROW) {
			Debug.e("OutOfBound (0, " + NUM_COL + "), but your parameter are : " + pCol +", " + pRow);
			return -1;
		} else {
			return (byte) (pCol + pRow * NUM_COL);
		}
	}
	
	public static byte[] cellToIndex(final byte pCell) {
		if(pCell < 0 || pCell >= NUM_COL * NUM_ROW) {
			Debug.e("OutOfBound (0, " + (NUM_COL * NUM_ROW) + "), but your parameter are : " + pCell);
			return null;
		} else {
			return (new byte[] {(byte) (pCell % NUM_COL), (byte) (pCell / NUM_COL)});
		}
	}
	
	public static ArrayList<Byte> getBetweenCells(final byte pCell1, final byte pCell2, final byte[][] pBoard) {
		byte[] index1 = cellToIndex(pCell1);
		byte[] index2 = cellToIndex(pCell2);
		return getBetweenCells(index1[COL_INDEX], index1[ROW_INDEX], index2[COL_INDEX], index2[ROW_INDEX], pBoard);
	}
	
	public static ArrayList<Byte> getBetweenCells(final byte pCol1, final byte pRow1, final byte pCol2, final byte pRow2, final byte[][] pBoard) {
		byte col = pCol1;
		byte row = pRow1;
		ArrayList<Byte> mArrayList = new ArrayList<Byte>();
		while (col != pCol2 || row != pRow2) {
			if(col < pCol2) {
				col ++;
			} else if(col > pCol2) {
				col --;
			}
			if(row < pRow2) {
				row ++;
			} else if(row > pRow2) {
				row --;
			}
			
			if (col != pCol2 || row != pRow2) {
				mArrayList.add(Byte.valueOf(indexToCell(col, row)));
			}
		}
		return mArrayList;
	}
	
	public static void searchAllValidWays(final byte pType, final byte[][] pMap, ArrayList<ArrayList<Byte>> mValidWaysGraph) {
		mValidWaysGraph.clear();
		for(byte col = 0; col < NUM_COL; col ++) {
			for(byte row = 0; row < NUM_ROW; row ++) {
				if(pMap[col][row] == pType) {
					byte[] neighbours = findNeighbours(pType, col, row, pMap, 
							new byte[] {DIRECTION_NONE, DIRECTION_NONE, DIRECTION_LEFT, DIRECTION_RIGHT, DIRECTION_LEFT, DIRECTION_LEFT, DIRECTION_RIGHT, DIRECTION_RIGHT}, 
							new byte[] {DIRECTION_UP  , DIRECTION_DOWN, DIRECTION_NONE, DIRECTION_NONE , DIRECTION_UP  , DIRECTION_DOWN, DIRECTION_UP   , DIRECTION_DOWN });
					final byte cell = indexToCell(col, row);
					final int index = findCellInValidWaysGraph(cell, mValidWaysGraph);
					for (byte neighbour : neighbours) {
						if(neighbour != -1) {
							/* append owner. */
							if(index == -1) {
								ArrayList<Byte> newIndex = new ArrayList<Byte>();
								newIndex.add(Byte.valueOf(cell));
								newIndex.add(Byte.valueOf(neighbour));
								mValidWaysGraph.add(newIndex);
							} else {
								int neighbourIndex = findCellInNeighbours(neighbour, mValidWaysGraph.get(index));
								if(neighbourIndex == -1) {
									mValidWaysGraph.get(index).add(Byte.valueOf(neighbour));
								}
							}
							/* append neighbour. */
							int neighIndex = findCellInValidWaysGraph(neighbour, mValidWaysGraph);
							if(neighIndex == -1) {
								ArrayList<Byte> newIndex = new ArrayList<Byte>();
								newIndex.add(Byte.valueOf(neighbour));
								newIndex.add(Byte.valueOf(cell));
								mValidWaysGraph.add(newIndex);
							} else {
								int neighbourIndex = findCellInNeighbours(cell, mValidWaysGraph.get(neighIndex));
								if(neighbourIndex == -1) {
									mValidWaysGraph.get(neighIndex).add(Byte.valueOf(cell));
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static byte[] findNeighbours(final byte pType, final byte pCol, final byte pRow, final byte[][] pMap, final byte[] pDirecXs, final byte[] pDirecYs) {
		byte[] mNeighbours = new byte[pDirecXs.length];
		for(int index = pDirecXs.length - 1; index >= 0; index --) {
			mNeighbours[index] = findNeighbour(pType, pCol, pRow, pMap, pDirecXs[index], pDirecYs[index]);
		}
		return mNeighbours;
	}
	
	public static byte findNeighbour(final byte pType, final byte pCol, final byte pRow, final byte[][] pMap, final byte pDirecX, final byte pDirecY) {
		byte col = pCol;
		byte row = pRow;
		boolean isOtherPiece = false;
		while(true) {
			if(pDirecY == DIRECTION_UP) {
				row --;
			} else if(pDirecY == DIRECTION_DOWN) {
				row ++;
			} 
			if(pDirecX == DIRECTION_LEFT) {
				col --;
			} else if(pDirecX == DIRECTION_RIGHT){
				col ++;
			}
			
			if(col < 0 || col == NUM_COL || row < 0 || row == NUM_ROW) {
				break;
			}
			
			if(pMap[col][row] == NONE_PIECE) {
				return (isOtherPiece ? indexToCell(col, row) : -1);
			} else if(pMap[col][row] == pType) {
				break;
			} else {
				isOtherPiece = true;
			}
		}
		return -1;
	}
	
	public static int findCellInValidWaysGraph(final byte pCell, final ArrayList<ArrayList<Byte>> mValidWaysGraph) {
		for(int index = mValidWaysGraph.size() - 1; index >= 0; index--) {
			if(mValidWaysGraph.get(index).get(0).byteValue() == pCell) {
				return index;
			}
		}
		return -1;
	}
	
	public static int findCellInNeighbours(final byte neighbour, final ArrayList<Byte> mNeighbours) {
		return mNeighbours.indexOf(Byte.valueOf(neighbour));
	}
	
	public static byte getOpposite(byte pType) {
		if (pType == BLACK_PIECE) return WHITE_PIECE;
		else if (pType == WHITE_PIECE) return BLACK_PIECE;
		else return NONE_PIECE;
	}
	
	public static int getDiscsCount(byte[][] pBoard, byte pColor) {
		int result = 0;
		for (int col = 0; col < NUM_COL; col ++) {
			for (int row = 0; row < NUM_ROW; row ++) {
				if (pBoard[col][row] == pColor) {
					result ++;
				}
			}
		}
		return result;
	}
	
	public static void setFieldColor(byte[][] mBoard, byte pColor, ArrayList<Byte> neighbours) {
		/* add new index. */
		byte cell = neighbours.get(0).byteValue();
		byte[] index = cellToIndex(cell);
		mBoard[index[COL_INDEX]][index[ROW_INDEX]] = pColor;
		/* change pieces between input index and owner. */
		for (int i = neighbours.size() - 1; i > 0; i --) {
			ArrayList<Byte> betweenCells = getBetweenCells(cell, neighbours.get(i).byteValue(), mBoard);
			for (Byte betweenCell : betweenCells) {
				index = Unity.cellToIndex(betweenCell.byteValue());
				mBoard[index[COL_INDEX]][index[ROW_INDEX]] = pColor;
			}
		}
	}
}
