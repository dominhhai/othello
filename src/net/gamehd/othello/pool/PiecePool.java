package net.gamehd.othello.pool;

import net.gamehd.othello.constants.GameConstants;
import net.gamehd.othello.util.Unity;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

/**
 * (c) 2012 HEDSPI - SoICT - HUST.
 * 
 * The pool which will manage all pieces in the game.<br/>
 * When remove pieces from game's board we will store them into this pool 
 * and we will get from this pool pieces which will appear on game's board.
 * 
 * @author Hai Do Minh
 * @author Binh Nguyen Duc
 * @author Nhat Dinh Van
 * @since 04.04.2012
 */

public class PiecePool extends GenericPool<AnimatedSprite> implements GameConstants{

	private TiledTextureRegion			mTiledTextureRegion;
	private VertexBufferObjectManager 	mVBOManager;
	
	public PiecePool(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		this.mTiledTextureRegion 	= 	pTiledTextureRegion;
		this.mVBOManager			= 	pVertexBufferObjectManager;
	}
	
	@Override
	protected AnimatedSprite onAllocatePoolItem() {
		return (new AnimatedSprite(0, 0, this.mTiledTextureRegion, this.mVBOManager));
	}
	
	@Override
	protected void onHandleRecycleItem(AnimatedSprite pItem) {
		super.onHandleRecycleItem(pItem);
		pItem.detachSelf();
		pItem.setVisible(false);
		pItem.reset();
	}
	
	public AnimatedSprite obtainPoolItem(final byte type, final byte pCol, final byte pRow) {
		AnimatedSprite mAnimatedSprite = obtainPoolItem();
		final float[] mPosition = Unity.indexToPosition(pCol, pRow);
		mAnimatedSprite.setPosition(mPosition[COL_INDEX], mPosition[ROW_INDEX]);
		if(type == BLACK_PIECE) {
			mAnimatedSprite.setCurrentTileIndex(BLACK_PIECE_INDEX);
		} else if (type == WHITE_PIECE) {
			mAnimatedSprite.setCurrentTileIndex(WHITE_PIECE_INDEX);
		}
		mAnimatedSprite.setVisible(true);
		return mAnimatedSprite;
	}

	
}
