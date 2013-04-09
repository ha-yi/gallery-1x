package com.teloquitous.lab.one.xposure.data;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.teloquitous.lab.one.xposure.R;
import com.teloquitous.lab.one.xposure.mukamuka.OneKaliDotComs;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class GambarAdapter extends BaseAdapter implements OneKaliDotComs {
	private ArrayList<OneXThumbnail> data;
	private Context c;
	private LayoutInflater inflater;
	private int item_size;

	public GambarAdapter(ArrayList<OneXThumbnail> d, Context ctx, int sz) {
		this.data = d;
		this.c = ctx;
		this.item_size = sz;
		this.inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int pos, View cv, ViewGroup vg) {
		View v = cv;
		if (cv == null) {
			v = inflater.inflate(R.layout.grid_item, null);
		}
		String url = PARENT_URL + data.get(pos).getImg();
		AQuery a = new AQuery(v);
		ImageOptions opt = new ImageOptions();
		opt.animation = AQuery.FADE_IN_NETWORK;
		opt.fileCache = true;
		opt.memCache = false;
//		a.id(R.id.image).progress(R.id.progress).image(url, opt);
		a.id(R.id.image).progress(R.id.progress).image(url,false, true, 0,0,new BitmapAjaxCallback() {

			@Override
			protected void callback(String url, ImageView iv, Bitmap bm,
					AjaxStatus status) {
				iv.setImageBitmap(bm);
				iv.setScaleType(ScaleType.CENTER_CROP);
				iv.setTag(url);
				iv.setLayoutParams(new RelativeLayout.LayoutParams(item_size, item_size));
			}
			
		});
		return v;
	}

}
