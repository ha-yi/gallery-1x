package com.teloquitous.lab.one.xposure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.teloquitous.lab.one.xposure.data.Category;
import com.teloquitous.lab.one.xposure.mukamuka.GantiKategoriListener;

public class KategoriFragment extends SherlockListFragment {
	// private AQuery aq;
	GantiKategoriListener mCallback;
	private KategoriAdapter adap;
	private ArrayList<String> data;
	private TypedArray arayImgs;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_kategori, container, false);
		// aq = new AQuery(getActivity(), v);
		arayImgs = getResources().obtainTypedArray(R.array.arrays_array_img);
		getActivity().setTitle("Select Category");
		adap = new KategoriAdapter(data, arayImgs,getActivity());
		setListAdapter(adap);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (GantiKategoriListener) activity;
		} catch (Exception e) {
			throw new ClassCastException();
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mCallback.onKategoriBerganti(position);
		getListView().setItemChecked(position, true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] d = Category.catName;
		data = new ArrayList<String>(Arrays.asList(d));
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (getFragmentManager().findFragmentById(R.id.fragment_photos) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
	}

	@Override
	public void onDestroy() {
		arayImgs.recycle();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private class KategoriAdapter extends BaseAdapter {
		ArrayList<String> data;
		Context c;
		LayoutInflater inflater;
		Resources r;
		TypedArray imgsArray;

		public KategoriAdapter(ArrayList<String> d, TypedArray a, Context ct) {
			data = d;
			c = ct;
			imgsArray = a;
			r = c.getResources();
			inflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int pos) {
			return data.get(pos);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int pos, View cv, ViewGroup vg) {
			View v = cv;
			if (cv == null) {
				v = inflater.inflate(R.layout.list_kategori_item, null);
			}
			AQuery a = new AQuery(v);
			
			if(imgsArray != null && imgsArray.length() == data.size() && r != null) {
				
				TypedArray arr = r.obtainTypedArray(imgsArray.getResourceId(pos, -1));
				Drawable[] gambar = new Drawable[2];
				gambar[1] = r.getDrawable(R.drawable.drawable_grad_ltr_white);
				if(arr != null) {
					int l = arr.length();
					Random rand = new Random();
					int rInt = rand.nextInt(l-1);
					gambar[0] = r.getDrawable(arr.getResourceId(rInt, -1));
					if(gambar[1] != null) {
						LayerDrawable ld = new LayerDrawable(gambar);
						a.id(R.id.imageViewKategori).image(ld);
					}
				}
				arr.recycle();
			}

			a.id(R.id.textViewKategori).text(data.get(pos).replace("-", " "));
			return v;
		}

	}
	
	

}
