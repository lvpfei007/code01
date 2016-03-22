package com.codefiles.adapter;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codefiles.BrowserFileActivity2;
import com.codefiles.R;

public class AdapterList extends BaseAdapter{

	private List<Map<String,Object>> mDataList;
	private Context mContext;
	private BrowserFileActivity2 mfileActivity;
	private File mRoot;
	
	private String ROOT_PATH1 = "/mnt/sdcard/code/coderesource/Reviewed/1.T";
	private String ROOT_PATH2 = "/mnt/sdcard/code/coderesource/Reviewed/2.M";
	private String ROOT_PATH3 = "/mnt/sdcard/code/coderesource/Reviewed/3.X";
	private String ROOT_PATH4 = "/mnt/sdcard/code/coderesource/Reviewed/4.D";
	private String ROOT_PATH5 = "/mnt/sdcard/code/coderesource/Reviewed/5.O";
	
	private class Holder{
		private ImageView imgIcon;
		private TextView fileName;
		private TextView lastTime;
		private ImageView imgDetail;
		private TextView fileDetail;
	}
	
	public AdapterList(BrowserFileActivity2 fileActivity, 
			Context context, 
			List<Map<String,Object>> dataList, File root){
		
		this.mfileActivity = fileActivity;
		this.mContext = context;
		this.mDataList = dataList;
		this.mRoot = root;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mDataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
	
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.item, null);
		
		Holder mHolder = new Holder();
		mHolder.imgIcon = (ImageView) view.findViewById(R.id.imageView_file);
		mHolder.fileName = (TextView) view.findViewById(R.id.textView_fileName);
		mHolder.lastTime = (TextView) view.findViewById(R.id.textView_time);		
		mHolder.imgDetail = (ImageView) view.findViewById(R.id.img_file_detail);
		mHolder.fileDetail = (TextView) view.findViewById(R.id.textView_file_detail);
		
		mHolder.imgIcon.setImageResource((Integer) mDataList.get(arg0).get("icon"));
		mHolder.fileName.setText((String)(mDataList.get(arg0).get("title")));
		mHolder.lastTime.setText((String)(mDataList.get(arg0).get("date")));
		mHolder.imgDetail.setImageResource(R.drawable.menu1);
		mHolder.fileDetail.setText((String)mDataList.get(arg0).get("filedetail"));
		
		if(mRoot.getAbsolutePath().equals(ROOT_PATH1) ||
				mRoot.getAbsolutePath().equals(ROOT_PATH2) ||
				mRoot.getAbsolutePath().equals(ROOT_PATH3) ||
				mRoot.getAbsolutePath().equals(ROOT_PATH4) ||
				mRoot.getAbsolutePath().equals(ROOT_PATH5)){
		
			
			mHolder.imgDetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View viewOne) {
					
					mfileActivity.openDetail(mRoot.listFiles()[arg0]);
				}
			});
		}else{
			mHolder.imgDetail.setAlpha(0);
		}
		
		return view;
	}

}
