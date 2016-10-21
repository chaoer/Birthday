package com.chaoer.birthday;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter{
	private Context mContext;
	private List<BirthdayData> mList;
	private LayoutInflater mInflater;
	private int[] mImgs = { R.drawable.ic_listitem_head, R.drawable.ic_listitem_birthday};
	
	public MyListAdapter(Context context, List<BirthdayData> list) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mList = list;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public void setList(List<BirthdayData> list)
	{
		this.mList = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void remove(BirthdayData birdata)
	{
		mList.remove(birdata);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null)
		{
			convertView = mInflater.inflate(R.layout.birthday_item, null);
			holder = new ViewHolder();
			holder.head = (ImageView) convertView
					.findViewById(R.id.bir_item_head);
			holder.name = (TextView) convertView
					.findViewById(R.id.bir_item_name);
			holder.age = (TextView) convertView
					.findViewById(R.id.bir_item_age);
			holder.birinfo = (TextView) convertView
					.findViewById(R.id.bir_item_info);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		if(mList==null)return convertView;
		
		final BirthdayData birdata = mList.get(position);
		// 头像
		if(birdata.isToday())
		{
			holder.head.setImageResource(mImgs[1]);
		}
		else
		{
			holder.head.setImageResource(mImgs[0]);
		}
		// name
		holder.name.setText(birdata.getName());
		// age
		holder.age.setText(birdata.getAge());
		// birinfo
		holder.birinfo.setText(birdata.getInfo());
		
		// 点击事件
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BirthdayData bir = new BirthdayData(birdata.mName, birdata.mbir_date, birdata.mbir_type);	
				Intent intent = new Intent(mContext, EditBirthdayActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("WorkType", 0);
				intent.putExtra("Birdata", bir);
				mContext.startActivity(intent);
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
//				BirthdayData bir = new BirthdayData(birdata.mName, birdata.mbir_date, birdata.mbir_type);	
//				Intent intent = new Intent(mContext, EditBirthdayActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.putExtra("WorkType", 0);
//				intent.putExtra("Birdata", bir);
//				mContext.startActivity(intent);
				return false;
			}
		});
		return convertView;
	}

	static class ViewHolder {
		public ImageView head;
		public TextView name;
		public TextView age;
		public TextView birinfo;		
	}
}
