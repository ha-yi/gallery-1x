package com.teloquitous.lab.one.xposure.imageviewer;

import java.util.Observable;

public class ZoomState extends Observable {
	private float mZoom;
	private float mPanX;
	private float mPanY;
	
	public float getZoom() {
		return mZoom;
	}
	
	public void setZoom(float zoom) {
		if(zoom != mZoom) {
			mZoom = zoom;
			setChanged();
		}
	}
	
	public float getPanX() {
		return mPanX;
	}
	
	public void setPanX(float panX) {
		if(panX != mPanX) {
			mPanX = panX;
			setChanged();
		}
		
	}
	
	public float getPanY() {
		return mPanY;
	}
	
	public void setPanY(float panY) {
		if(panY != mPanY) {
			mPanY = panY;
			setChanged();
		}
	}
	
	public float getZoomX(float aspectQuotient) {
		return Math.min(mZoom, mZoom * aspectQuotient);
	}
	
	public float getZoomY(float aspectQuotient) {
		return Math.min(mZoom, mZoom / aspectQuotient);
	}
	
	
}
