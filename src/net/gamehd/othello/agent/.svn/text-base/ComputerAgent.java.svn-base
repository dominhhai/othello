package net.gamehd.othello.agent;

import java.util.ArrayList;
import java.util.Random;

import net.gamehd.othello.GameConfig;
import net.gamehd.othello.constants.GameConstants;
import net.gamehd.othello.util.Unity;

import org.andengine.util.debug.Debug;

/**
 * (c) 2012 HEDSPI - SoICT - HUST.
 * 
 * Computer Agent is a auto agent. Which will play with player.<br/>
 * We will user MiniMax with Alpha-Beta Pruning to search the best choice.
 * 
 * @author Hai Do Minh
 * @author Binh Nguyen Duc
 * @author Nhat Dinh Van
 * @since 04.04.2012
 */

public class ComputerAgent implements GameConstants {

	private Random random;
	
	public ComputerAgent() {
		random = new Random();
	}
	
	public int action(final byte[][] pMap, final ArrayList<ArrayList<Byte>> mValidWaysGraph) {
		Debug.e("Computer action");
		int wayIndex = random.nextInt(mValidWaysGraph.size());
		
		final ArrayList<Byte> mNeighbours = mValidWaysGraph.get(wayIndex);
		byte putCell = mNeighbours.get(0).byteValue();
		byte[] putIndex = Unity.cellToIndex(putCell);
		if(pMap[putIndex[COL_INDEX]][putIndex[ROW_INDEX]] != NONE_PIECE) {
			putCell = mNeighbours.get(random.nextInt(mNeighbours.size() - 1) + 1);
			wayIndex = Unity.findCellInValidWaysGraph(putCell, mValidWaysGraph);
			putIndex = Unity.cellToIndex(putCell);
		}
		return wayIndex;
	}
	
	public byte action(final byte[][] pMap) {
		Debug.e("Computer action with depth : " + GameConfig.maxDepth);
		long startTime = System.currentTimeMillis();
		byte[] mResult = new byte[2];
		getNextMove(pMap, WHITE_PIECE, true, 1, MIN_ALPHA_VALUE, MAX_BETA_VALUE, mResult);
		Debug.e("Computer action complete : " + (System.currentTimeMillis() - startTime));
		return mResult[COL_INDEX] == -1 ? -1 : Unity.indexToCell(mResult[COL_INDEX], mResult[ROW_INDEX]);
	}
	
	/* minimax with alpha-beta pruning. */
	private int getNextMove(final byte[][] pBoard, byte pColor, boolean isMaximizing, int currentDepth, int alpha, int beta, byte[] mResult) {
       mResult[COL_INDEX] = 0;
       mResult[ROW_INDEX] = 0;

        byte color = isMaximizing ? pColor : Unity.getOpposite(pColor);
        boolean playerSkipsMove = false;
        ArrayList<ArrayList<Byte>> mValidWaysGraph = new ArrayList<ArrayList<Byte>>();

        boolean isFinalMove = currentDepth >= GameConfig.maxDepth;

        if (!isFinalMove) {
        	Unity.searchAllValidWays(color, pBoard, mValidWaysGraph);
        	if (mValidWaysGraph.isEmpty()) {
        		playerSkipsMove = true;
        		Unity.searchAllValidWays(Unity.getOpposite(color), pBoard, mValidWaysGraph);
        	}
        	isFinalMove = mValidWaysGraph.isEmpty();
        }

        if (isFinalMove) {
        	mResult[COL_INDEX] = -1;
        	mResult[ROW_INDEX] = -1;
            return this.evaluateBoard(pBoard, pColor);
        } else {
            int bestBoardValue = isMaximizing ? MIN_ALPHA_VALUE : MAX_BETA_VALUE;
            byte bestMoveRowIndex = -1;
            byte bestMoveColumnIndex = -1;

            for (ArrayList<Byte> move : mValidWaysGraph) {
                byte[] index = Unity.cellToIndex(move.get(0).byteValue());
                if (pBoard[index[COL_INDEX]][index[ROW_INDEX]] != NONE_PIECE) {
                	continue;
                }

                byte[][] nextBoard = new byte[NUM_COL][NUM_ROW];
                for (int i = 0; i < NUM_COL; i ++) {
                	System.arraycopy(pBoard[i], 0, nextBoard[i], 0, NUM_ROW);
                }
                Unity.setFieldColor(nextBoard, color, move);

                boolean nextIsMaximizing = playerSkipsMove ? isMaximizing : !isMaximizing;

                byte[] dummyIndex = new byte[2]; // values of resultRowIndex and resultColumnIndex are not needed in recursive function calls
                int currentBoardValue = this.getNextMove(nextBoard, pColor, nextIsMaximizing, currentDepth + 1, alpha, beta, dummyIndex);
                if (isMaximizing) {
                    if (currentBoardValue > bestBoardValue) {
                        bestBoardValue = currentBoardValue;
                        bestMoveRowIndex = index[ROW_INDEX];
                        bestMoveColumnIndex = index[COL_INDEX];

                        if (bestBoardValue > alpha) {
                            alpha = bestBoardValue;
                        }

                        if (bestBoardValue >= beta) {
                            break;
                        }
                    }
                } else {
                    if (currentBoardValue < bestBoardValue) {
                        bestBoardValue = currentBoardValue;
                        bestMoveRowIndex = index[ROW_INDEX];
                        bestMoveColumnIndex = index[COL_INDEX];

                        if (bestBoardValue < beta) {
                            beta = bestBoardValue;
                        }

                        if (bestBoardValue <= alpha) {
                            break;
                        }
                    }
                }
            }

            mResult[ROW_INDEX] = bestMoveRowIndex;
            mResult[COL_INDEX] = bestMoveColumnIndex;
            return bestBoardValue;
        }
    }

    private int evaluateBoard(final byte[][] board, byte pColor) {
        byte color = pColor;
        byte oppositeColor = Unity.getOpposite(pColor);

        ArrayList<ArrayList<Byte>> oppositePlayerPossibleMoves = new ArrayList<ArrayList<Byte>>();
        Unity.searchAllValidWays(oppositeColor, board, oppositePlayerPossibleMoves);
        ArrayList<ArrayList<Byte>> possibleMoves =  new ArrayList<ArrayList<Byte>>();
        Unity.searchAllValidWays(color, board, oppositePlayerPossibleMoves);

        if (possibleMoves.isEmpty() && oppositePlayerPossibleMoves.isEmpty()) {
            int result = Unity.getDiscsCount(board, color) - Unity.getDiscsCount(board, oppositeColor);
            int addend = (int) Math.pow(NUM_COL, 4) + (int) Math.pow(NUM_COL, 3); // because it is a terminal state, its weight must be bigger than the heuristic ones
            if (result < 0) {
                addend = -addend;
            }
            return result + addend;
        } else {
            int mobility = this.getPossibleConvertions(board, color, possibleMoves) - this.getPossibleConvertions(board, oppositeColor, oppositePlayerPossibleMoves);
            int stability = (this.getStableDiscsCount(board, color) - this.getStableDiscsCount(board, oppositeColor)) * NUM_COL * 2 / 3;
            int skipMove = this.getSkipMove(oppositePlayerPossibleMoves);
            int differentWays = this.getDifferentWays(possibleMoves, oppositePlayerPossibleMoves);
            return mobility + stability + skipMove + differentWays;
        }
    }

    private int getPossibleConvertions(final byte[][] board, byte color, ArrayList<ArrayList<Byte>> possibleMoves) {
        int result = 0;
        for (ArrayList<Byte> move : possibleMoves) {
			byte[] index = Unity.cellToIndex(move.get(0).byteValue());
			if (board[index[COL_INDEX]][index[ROW_INDEX]] == NONE_PIECE) {
				for (Byte beginCell : move) {
					byte[] beginIndex = Unity.cellToIndex(beginCell.byteValue());
					byte[] neighbours = Unity.findNeighbours(color, beginIndex[COL_INDEX], beginIndex[ROW_INDEX], board, 
							new byte[] {DIRECTION_NONE, DIRECTION_NONE, DIRECTION_LEFT, DIRECTION_RIGHT, DIRECTION_LEFT, DIRECTION_LEFT, DIRECTION_RIGHT, DIRECTION_RIGHT}, 
							new byte[] {DIRECTION_UP  , DIRECTION_DOWN, DIRECTION_NONE, DIRECTION_NONE , DIRECTION_UP  , DIRECTION_DOWN, DIRECTION_UP   , DIRECTION_DOWN });
					for (byte neighbour : neighbours) {
						if (neighbour != -1) {
							result ++;
						}
					}
				}
			}
		}
        return result;
    }

    public int getStableDiscsCount(byte[][] board, byte color) {
        return this.getStableDiscsFromCorner(board, color, 0, 0)
            + this.getStableDiscsFromCorner(board, color, 0, NUM_COL - 1)
            + this.getStableDiscsFromCorner(board, color, NUM_COL - 1, 0)
            + this.getStableDiscsFromCorner(board, color, NUM_COL - 1, NUM_COL - 1)
            
            + this.getStableDiscsFromEdge(board, color, 0, true)
            + this.getStableDiscsFromEdge(board, color, NUM_COL - 1, true)
            + this.getStableDiscsFromEdge(board, color, 0, false)
            + this.getStableDiscsFromEdge(board, color, NUM_COL - 1, false);
    }

    private int getStableDiscsFromCorner(byte[][] board, byte color, int cornerRowIndex, int cornerColumnIndex) {
        int result = 0;

        int rowIndexChange = (cornerRowIndex == 0) ? 1 : -1;
        int columnIndexChange = (cornerColumnIndex == 0) ? 1 : -1;

        int rowIndex = cornerRowIndex;
        int rowIndexLimit = (cornerRowIndex == 0) ? NUM_COL : 0;
        int columnIndexLimit = (cornerColumnIndex == 0) ? NUM_COL : 0;
        for (rowIndex = cornerRowIndex; rowIndex != rowIndexLimit; rowIndex += rowIndexChange) {
            int columnIndex;
            for (columnIndex = cornerColumnIndex; columnIndex != columnIndexLimit; columnIndex += columnIndexChange)
            {
                if (board[rowIndex][columnIndex] == color) {
                    result++;
                } else {
                    break;
                }
            }

            if ((columnIndexChange > 0 && columnIndex < NUM_COL) || (columnIndexChange < 0 && columnIndex > 0)) {
                columnIndexLimit = columnIndex - columnIndexChange;

                if (columnIndexChange > 0 && columnIndexLimit == 0) {
                    columnIndexLimit++;
                } else if (columnIndexChange < 0 && columnIndexLimit == NUM_COL - 1) {
                    columnIndexLimit--;
                }

                if ((columnIndexChange > 0 && columnIndexLimit < 0) || (columnIndexChange < 0 && columnIndexLimit > NUM_COL - 1)) {
                    break;
                }
            }
        }

        return result;
    }

    private int getStableDiscsFromEdge(byte[][] board, byte color, int edgeCoordinate, boolean isHorizontal) {
        int result = 0;

        if (isEdgeFull(board, edgeCoordinate, isHorizontal)) {
            boolean oppositeColorDiscsPassed = false;
            for (int otherCoordinate = 0; otherCoordinate < NUM_COL; otherCoordinate++) {                
                byte fieldColor = (isHorizontal) ? board[edgeCoordinate][otherCoordinate] : board[otherCoordinate][edgeCoordinate];
                if (fieldColor != color) {
                    oppositeColorDiscsPassed = true;
                } else if (oppositeColorDiscsPassed) {
                    int consecutiveDiscsCount = 0;
                    while ((otherCoordinate < NUM_ROW) && (fieldColor == color)) {
                        consecutiveDiscsCount++;

                        otherCoordinate++;
                        if (otherCoordinate < NUM_ROW) {
                            fieldColor = (isHorizontal) ? board[edgeCoordinate][otherCoordinate] : board[otherCoordinate][edgeCoordinate];
                        }
                    }
                    if (otherCoordinate != NUM_ROW)
                    {
                        result += consecutiveDiscsCount;
                        oppositeColorDiscsPassed = true;
                    }                                             
                }
            }
        }

        return result;
    }

    private boolean isEdgeFull(byte[][] board, int edgeCoordinate, boolean isHorizontal) {
        for (int otherCoordinate = 0; otherCoordinate < NUM_COL; otherCoordinate++) {
            if (isHorizontal && (board[edgeCoordinate][otherCoordinate] == NONE_PIECE) || !isHorizontal && (board[otherCoordinate][edgeCoordinate] == NONE_PIECE)) {
                return false;
            }
        }
        return true;
    }

    private int getSkipMove(ArrayList<ArrayList<Byte>> oppositePlayerPossibleMoves) {
    	return (oppositePlayerPossibleMoves.isEmpty() ? 20 : 0);
    }
    
    private int getDifferentWays(ArrayList<ArrayList<Byte>> possibleMoves, ArrayList<ArrayList<Byte>> oppositePlayerPossibleMoves) {
    	return 5 * (possibleMoves.size() - oppositePlayerPossibleMoves.size());
    }
}
