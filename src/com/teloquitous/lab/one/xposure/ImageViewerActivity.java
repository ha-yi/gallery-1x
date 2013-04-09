package com.teloquitous.lab.one.xposure;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchDoubleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageZoomListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.OnDrawableChangeListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.analytics.tracking.android.EasyTracker;
import com.teloquitous.lab.one.xposure.data.OneXThumbnail;
import com.teloquitous.lab.one.xposure.imageviewer.ImageThreadLoader;
import com.teloquitous.lab.one.xposure.imageviewer.ImageThreadLoader.ImageLoadedListener;
import com.teloquitous.lab.one.xposure.mukamuka.OneKaliDotComs;

public class ImageViewerActivity extends Activity implements OneKaliDotComs {
	private AQuery aq;
	private OneXThumbnail data;
	private ProgressBar progress;
	private LinearLayout layoutBawah;
	private Animation fadeIn;
	private Animation fadeOut;
	/**
	 * zoom view
	 */

	private static final String LOG_TAG = "image viewer";

	ImageViewTouch mImage;
	static int displayTypeCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_viewer);
		layoutBawah = (LinearLayout) findViewById(R.id.layout_bottom);
		fadeIn = new AlphaAnimation(0.00f, 1.00f);
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
            	layoutBawah.setVisibility(View.VISIBLE);
            }
        });

        fadeOut = new AlphaAnimation(1.00f, 0.00f);
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                layoutBawah.setVisibility(View.GONE);
            }
        });
        

		data = (OneXThumbnail) getIntent().getExtras().getParcelable("data");
		aq = new AQuery(this);
		progress = (ProgressBar) findViewById(R.id.progress);
		loadDataFromPage();
	}

	private void loadDataFromPage() {

		if (data != null) {
			aq.id(R.id.textViewLoadInfo).text(R.string.loading1);
			String url = data.getUrl();
			url = PARENT_URL + url;
			Log.d("URL", url);
			aq.ajax(url, String.class, this, "xmlCallback");
		}
	}

	public void xmlCallback(String url, String html, AjaxStatus status) {
		if (html == null) {
			Log.d(LOG_TAG, "DATA null");
			aq.id(R.id.textViewLoadInfo).text(R.string.fail_load_url);
		} else {
			String thumb = data.getImg();
			thumb = "','" + thumb.substring(0, thumb.lastIndexOf("-"));
			int start = html.indexOf(thumb);
			int fin = html.lastIndexOf(thumb);

			String img = html.substring(start, fin);
			if (img.isEmpty()) {
				fin = html.lastIndexOf(".jpg');\">") + 4;
				img = html.substring(start, fin);
			}
			if (!img.isEmpty()) {
				img = PARENT_URL + img.substring(img.indexOf("/"));
				Log.d("FILTER", img);
				loadBitmap(img);
			} else {
				aq.id(R.id.textViewLoadInfo).text(R.string.fail_load_url);
			}

			// set image title
			Document doc = Jsoup.parse(html);
			Element title = doc.getElementsByAttributeValue("property",
					"og:title").first();
			
			if (title != null) {

				if (title.attr("content") != null
						|| !title.attr("content").isEmpty()) {
					aq.id(R.id.textViewImageTitle).text("Title: " + title.attr("content"));
					layoutBawah.startAnimation(fadeIn);
				}

			} else {
				layoutBawah.startAnimation(fadeIn);
				aq.id(R.id.textViewImageTitle).text(R.string.fail_load_title);
			}
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mImage = (ImageViewTouch) findViewById(R.id.image);

		// set the default image display type
		mImage.setDisplayType(DisplayType.FIT_IF_BIGGER);

		mImage.setSingleTapListener(new OnImageViewTouchSingleTapListener() {

			@Override
			public void onSingleTapConfirmed() {
				Log.d(LOG_TAG, "onSingleTapConfirmed");
			}
		});

		mImage.setDoubleTapListener(new OnImageViewTouchDoubleTapListener() {

			@Override
			public void onDoubleTap() {
				Log.d(LOG_TAG, "onDoubleTap");
			}
		});

		mImage.setOnDrawableChangedListener(new OnDrawableChangeListener() {

			@Override
			public void onDrawableChanged(Drawable drawable) {
			}
		});
		
		mImage.setOnImageZoomListener(new OnImageZoomListener() {
			
			@Override
			public void onImageZoom(float scale) {
				if(scale > 1) {
					if(layoutBawah.getVisibility() != View.GONE) {
						layoutBawah.startAnimation(fadeOut);
					}
				} else {
					layoutBawah.startAnimation(fadeIn);
				}
			}
		});
	}

	Matrix imageMatrix;

	private void loadBitmap(String img) {
		aq.id(R.id.textViewLoadInfo).text(R.string.loading2);
		ImageThreadLoader loader = new ImageThreadLoader();
		Bitmap cachedImg = null;

		try {
			cachedImg = loader.loadImage(img, new ImageLoadedListener() {

				@Override
				public void imageLoaded(Bitmap imageBitmap) {
					if (imageBitmap != null) {
						progress.setVisibility(View.GONE);
						setBitmap(imageBitmap);
					}
				}
			});

		} catch (Exception e) {
		}

		if (cachedImg != null) { // instant image loaded,, wow...
			if (null == imageMatrix) {
				imageMatrix = new Matrix();
			} else {
				// get the current image matrix, if we want restore the
				// previous matrix once the bitmap is changed
				imageMatrix = mImage.getDisplayMatrix();
			}
			// mImage.setImageBitmap(cachedImg);
			mImage.setImageBitmap(cachedImg, imageMatrix.isIdentity() ? null
					: imageMatrix, ImageViewTouchBase.ZOOM_INVALID,
					ImageViewTouchBase.ZOOM_INVALID);
		}

	}

	private void setBitmap(Bitmap bmp) {
		aq.id(R.id.textViewLoadInfo).visibility(View.GONE);
		mImage.setImageBitmap(bmp);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}
	
	@Override
	protected void onStop() {
		EasyTracker.getInstance().activityStop(this);
		super.onStop();
	}

}
