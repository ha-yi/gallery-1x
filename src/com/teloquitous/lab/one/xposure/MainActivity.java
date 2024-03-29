package com.teloquitous.lab.one.xposure;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.teloquitous.lab.one.xposure.mukamuka.GantiKategoriListener;

public class MainActivity extends SherlockFragmentActivity implements
		GantiKategoriListener {
	private static String TAG_PHOTO = "photo";
	private static String TAG_CAT = "kategori";
	private ActionBar abar;
	private GantiKategoriListener mCallback;
	private int sort = 0;
	private int kategori = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		Log.d("MAIN", "on start");

		abar = getSupportActionBar();
		mCallback = this;
		

		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			KategoriFragment kf = new KategoriFragment();
			kf.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, kf).commit();
		}

		
	}


	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getSupportMenuInflater();
		i.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menuSortByLatest:
			if (sort != 0) {
				fireOnSortChanged(0);
			}
			break;
		case R.id.menuSortByPopToday:
			if (sort != 1) {
				fireOnSortChanged(1);
			}
			break;
		case R.id.menuSortByPopEver:
			if (sort != 2) {
				fireOnSortChanged(2);
			}
			break;

		default:
			break;
		}
		return true;
	}

	private void fireOnSortChanged(int s) {
		mCallback.onSortBerganti(s);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
//		if(iklan != null) {
//			try {
//				iklan.destroy();
//			} catch (Exception e) {
//			}
			
//		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
//		if(iklan != null) {
//			iklan.loadAd(new AdRequest());
//		}
		super.onResume();
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

	@Override
	public void onKategoriBerganti(int k) {
		kategori = k;
		changeFragment();
	}

	@Override
	public void onSortBerganti(int sortMode) {
		sort = sortMode;
		// cek dual layout
		if (findViewById(R.id.fragment_photos) != null) {
			PhotosFragment pf = (PhotosFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_photos);
			if (pf != null) {
				Log.d("DUAL FRAME", "DUAL FRAME");
				// dual fragment
				pf.updateKategori(kategori, sort);
			} else {
				// do nothing
			}
		} else {
			// on single fragment
			// cek fragment yang aktif
			PhotosFragment pf = (PhotosFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_PHOTO);
			if (pf != null) {
				if (pf.isVisible()) {
					pf.clearAllGrid();
					pf.updateKategori(kategori, sort);
				} else {
					// do nothing
				}
			}
		}
	}

	private void changeFragment() {
		// cek dual layout
		if (findViewById(R.id.fragment_photos) != null) {

			PhotosFragment pf = (PhotosFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_photos);
			if (pf != null) {
				Log.d("DUAL FRAME", "DUAL FRAME");
				// dual fragment
				pf.updateKategori(kategori, sort);
			} else {
				pf = new PhotosFragment();
				Bundle b = new Bundle();
				b.putInt(PhotosFragment.ARG_POSITION, kategori);
				b.putInt(PhotosFragment.ARG_SORT, sort);
				pf.setArguments(b);

				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.replace(R.id.fragment_photos, pf);
				ft.commit();
			}
		} else {
			// berada pada single fragment
			PhotosFragment p = new PhotosFragment();
			Bundle b = new Bundle();
			b.putInt(PhotosFragment.ARG_POSITION, kategori);
			b.putInt(PhotosFragment.ARG_SORT, sort);
			p.setArguments(b);

			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.fragment_container, p, "photo");
			ft.addToBackStack(null);
			ft.commit();
		}
	}

}
