package com.teloquitous.lab.one.xposure.data;

import android.os.Parcel;
import android.os.Parcelable;

public class OneXThumbnail implements Parcelable{
	private String url;
	private String img;
	
	public OneXThumbnail() {
		super();
	}

	public OneXThumbnail(String url, String img) {
		super();
		this.url = url;
		this.img = img;
	}
	
	public OneXThumbnail(Parcel s) {
		this.url = s.readString();
		this.img = s.readString();
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel d, int flags) {
		d.writeString(url);
		d.writeString(img);
	}
	
	public static Parcelable.Creator<OneXThumbnail> CREATOR = new Parcelable.Creator<OneXThumbnail>() {

		@Override
		public OneXThumbnail createFromParcel(Parcel source) {
			return new OneXThumbnail(source);
		}

		@Override
		public OneXThumbnail[] newArray(int size) {
			return new OneXThumbnail[size];
		}
		
	};
	

}
