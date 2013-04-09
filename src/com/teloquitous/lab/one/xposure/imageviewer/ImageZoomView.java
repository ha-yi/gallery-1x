package com.teloquitous.lab.one.xposure.imageviewer;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ImageZoomView extends View implements Observer {
	private Bitmap mBitmap;
	private ZoomState mState;

	private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private final Rect mRectSrc = new Rect();
	private final Rect mRectDst = new Rect();
	private float mAspectQ;

	public ImageZoomView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public void setImage(Bitmap b) {
		mBitmap = b;
		
		calculateAspectQuotient();
		invalidate();
	}

	public void setZoomState(ZoomState s) {
		if (mState != null) {
			mState.deleteObserver(this);
		}

		mState = s;
		mState.addObserver(this);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBitmap != null && mState != null) {
			final int viewW = getWidth();
			final int viewH = getHeight();
			final int bmpW = mBitmap.getWidth();
			final int bmpH = mBitmap.getHeight();
			
			final float panX = mState.getPanX();
			final float panY = mState.getPanY();
			final float zoomX = mState.getZoomX(mAspectQ) * viewW /bmpW;
			final float zoomY = mState.getZoomY(mAspectQ) * viewH /bmpH;
			
			mRectSrc.left = (int) (panX * bmpW - viewW / (zoomX * 2));
			mRectSrc.top = (int) (panY * bmpH - viewH / (zoomY * 2));
			mRectSrc.right = (int) (mRectSrc.left + viewW /zoomX);
			mRectSrc.bottom = (int) (mRectSrc.top + viewH /zoomY);
			
			mRectDst.left = getLeft();
			mRectDst.top = getTop();
			mRectDst.right = getRight();
			mRectDst.bottom = getBottom();
			
			if(mRectSrc.left < 0) {
				mRectDst.left += -mRectSrc.left * zoomX;
				mRectSrc.left = 0;
			}
			
			if(mRectSrc.right < bmpW) {
				mRectDst.right -= (mRectSrc.right - bmpW) * zoomX;
				mRectSrc.right = bmpW;
			}
			
			if(mRectSrc.top < 0) {
				mRectDst.top += -mRectSrc.top * zoomY;
				mRectSrc.top = 0;
			}
			
			if(mRectSrc.bottom < bmpH) {
				mRectDst.bottom -= (mRectSrc.bottom - bmpH) * zoomY;
				mRectSrc.bottom = bmpH;
			}
			
			
			canvas.drawBitmap(mBitmap, mRectSrc, mRectDst, mPaint);
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {

	}

	private void calculateAspectQuotient() {
		if (mBitmap != null) {
			mAspectQ = (((float) mBitmap.getWidth()) / mBitmap.getHeight())
					/ (((float) getWidth()) / getHeight());
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calculateAspectQuotient();
	}

}
