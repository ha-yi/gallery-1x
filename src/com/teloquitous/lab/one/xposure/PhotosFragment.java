package com.teloquitous.lab.one.xposure;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.teloquitous.lab.one.xposure.data.Category;
import com.teloquitous.lab.one.xposure.data.GambarAdapter;
import com.teloquitous.lab.one.xposure.data.OneXThumbnail;
import com.teloquitous.lab.one.xposure.mukamuka.OneKaliDotComs;

public class PhotosFragment extends SherlockFragment implements OneKaliDotComs {
	
	final static String ARG_POSITION = "position";
	final static String ARG_SORT = "sort";
	int kategori;
	int sort;
	private AQuery aq;
	private ArrayList<OneXThumbnail> data = new ArrayList<OneXThumbnail>();
	int pageNum = 0;
	private GridView gv;
	private GambarAdapter adapter;
	private ProgressBar pbar;
	

	// private ActionBar actionBar;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		if (savedInstanceState != null) {
			kategori = savedInstanceState.getInt(ARG_POSITION);
			sort = savedInstanceState.getInt(ARG_SORT);
		}
		View v = inflater.inflate(R.layout.fragment_photos, container, false);
		pbar = (ProgressBar) v.findViewById(R.id.progress);
		// actionBar = getSherlockActivity().getSupportActionBar();

		aq = new AQuery(getActivity(), v);
		gv = (GridView) v.findViewById(R.id.gridViewPhotos);
		
		
//		loadAds(ar);
		

		return v;
	}
	

	


	public void clearAllGrid() {
		data.clear();
		gv.invalidateViews();
		adapter = null;
		pageNum = 0;
		pbar.setVisibility(View.VISIBLE);
		aq.id(R.id.text_data_stat).visibility(View.GONE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@Override
	public void onDestroy() {
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

	@Override
	public void onStart() {
		super.onStart();
		Bundle args = getArguments();
		if (args != null) {
			updateKategori(args.getInt(ARG_POSITION), args.getInt(ARG_SORT));
		} else if (kategori != -1) {
			updateKategori(kategori, sort);
		}
	}

	public void init(int k, int s) {
		kategori = k;
		sort = s;
	}

	public void updateKategori(int k, int s) {
		// set window title
		getActivity().setTitle(
				Category.catName[k].replace("-", " ") + " - "
						+ Category.sortType[s].replace("-", " "));

		// update tampilan galeri disini.
		kategori = k;
		sort = s;
		String id = Category.sortType[s] + "/" + Category.catName[k];
		String url = loadImageURL + id.toLowerCase();
		aq.ajax(url, XmlDom.class, this, "xmlCallback");
	}

	public void xmlCallback(String url, XmlDom xml, AjaxStatus status) {
		Log.d("URL", url);
		pbar.setVisibility(View.GONE);
		if (xml == null) {
			Log.d("data kosong", "KOSONG");
		} else {
			String html = xml.text("data");
			Document doc = Jsoup.parse(html);
			Elements tables = doc.getElementsByClass("photos_rendertable");

			for (Element e : tables) {
				Elements links = e.getElementsByClass("dynamiclink");
				Elements imgs = e
						.getElementsByClass("photos_rendertable_photo");
				int ukuran = 0;
				if (links.size() != imgs.size()) {
					ukuran = links.size() < imgs.size() ? links.size() : imgs
							.size();
				} else {
					ukuran = links.size();
				}

				for (int x = 0; x < ukuran; x++) {

					String link = links.get(x).select("a").first().attr("href");
					String img = imgs.get(x).select("img").first().attr("src");
					if (!img.contains(FILTER)) {
						OneXThumbnail d = new OneXThumbnail();
						if (!link.isEmpty() && !img.isEmpty()) {
							d.setUrl(link);
							d.setImg(img);
							data.add(d);
						}
					}
				}

			}
			if (data.size() <= 0) {
				String text = getString(R.string.no_data) + " "
						+ Category.catName[kategori].replace("-", " ") + " - "
						+ Category.sortType[sort].replace("-", " ");
				aq.id(R.id.text_data_stat).text(text).visibility(View.VISIBLE);
			} else {
				aq.id(R.id.text_data_stat).visibility(View.GONE);
			}

			if (data.size() > (pageNum * 30)) {

				pageNum++;
				updateGrid();
			}
		}
	}

	private void updateGrid() {
		if (adapter == null) {

			Display display = ((WindowManager) getActivity().getSystemService(
					Context.WINDOW_SERVICE)).getDefaultDisplay();
			int gallery_item_size = Math.min(display.getWidth() / 2,
					display.getHeight() / 2);
			int numcols = display.getWidth() / gallery_item_size;
			if (numcols < ((double) display.getWidth() / gallery_item_size))
				++numcols;
			gallery_item_size = display.getWidth() / numcols - 3; // padding

			adapter = new GambarAdapter(data, getActivity(), gallery_item_size);
			gv.setNumColumns(numcols);
			gv.setAdapter(adapter);
			gv.setOnScrollListener(new EndlessScrollListener());
			gv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int pos,
						long id) {
					OneXThumbnail dt = data.get(pos);
					Intent i = new Intent(getActivity(),
							ImageViewerActivity.class);
					i.putExtra("data", dt);
					startActivity(i);
				}
			});
		} else {
			gv.invalidateViews();
		}

	}

	/**
	 * 
	 * @author Ben Cull
	 *         http://benjii.me/2010/08/endless-scrolling-listview-in-android/
	 */
	public class EndlessScrollListener implements OnScrollListener {

		private int visibleThreshold = 5;
		private int currentPage = 0;
		private int previousTotal = 0;
		private boolean loading = true;

		public EndlessScrollListener() {
		}

		public EndlessScrollListener(int visibleThreshold) {
			this.visibleThreshold = visibleThreshold;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
					currentPage++;
				}
			}
			if (!loading
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				loadMore(currentPage);
				loading = true;
			}

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}
	}

	public void loadMore(int currentPage) {
		String s = "&sort=" + Category.sortType[sort];
		String cat = "&cat=" + Category.catName[kategori];
		String from = "&from=" + (currentPage * 30);

		Log.d("Kategori", cat);
		Log.d("Sort", s);
		Log.d("FROM", from);

		String url = loadMoreImageURL + from + cat + s;
		aq.ajax(url.toLowerCase(), XmlDom.class, this, "xmlCallback");

	}

}
