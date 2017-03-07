/**
 * Project : sample
 * File : TestActivity.java
 * 
 * The MIT License
 * Copyright (c) 2014 QiJiang.Guo (qijiang.guo@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package com.guo.samples;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.guo.android_extend.widget.AbsHAdapterView;
import com.guo.android_extend.widget.AbsHAdapterView.OnScrollListener;
import com.guo.android_extend.widget.ExtImageView;
import com.guo.android_extend.widget.ExtRelativeLayout;
import com.guo.android_extend.widget.effective.HCSEffectAdapter;
import com.guo.android_extend.widget.effective.HCSListView;

import java.util.ArrayList;
import java.util.List;

public class HCSListActivity extends Activity implements OnItemClickListener,
	OnItemLongClickListener, OnScrollListener {

	private static final String TAG = "HCSListActivity";

	HCSListView mHListView;
	ViewList2Adapter mAdapter2;
	/**
	 * scale percent.
	 */
	float percent_add = 0.5f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_hcslist);
		
		mHListView = (HCSListView) this.findViewById(R.id.hlist);
		mAdapter2 = new ViewList2Adapter(mHListView);
		mHListView.setAdapter(mAdapter2);
		mHListView.setOnItemScrollListener(new HScrollListener2());
		mHListView.setOnItemClickListener(this);
		mHListView.setOnItemLongClickListener(this);
	}

	private void scale(View v, float percent) {
		Holder holder = (Holder) v.getTag();
		if (holder.siv.setScale(percent, percent)) {
			holder.siv.invalidate();
		}
	}

	private void scale2(View v, float percent) {
		ExtRelativeLayout ert = (ExtRelativeLayout) v;
		ert.setScale(percent, percent);
		ert.invalidate();
	}

	class Holder {
		ExtImageView siv;
		TextView tv;
		int id;
	}
	
	class ViewList2Adapter extends HCSEffectAdapter {
		Context mContext;
		LayoutInflater mLInflater;
		int key = 0;
		List<String> mNames = new ArrayList<String>();
		
		public ViewList2Adapter(HCSListView c) {
			// TODO Auto-generated constructor stub
			super(c);
			mContext = c.getContext();
			mLInflater = LayoutInflater.from(mContext);
			for (int i = 0; i < 25; i++) {
				mNames.add("name_id=" + i);
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mNames.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder = null;
			if (convertView != null) {
				holder = (Holder) convertView.getTag();
			} else {
				convertView = mLInflater.inflate(R.layout.item_sample2, null);
				holder = new Holder();
				holder.siv = (ExtImageView) convertView.findViewById(R.id.imageView1);
				holder.tv = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			}
			
			holder.id = position;
			holder.tv.setText("T2->"+ key + " | " + mNames.get(position));
			holder.siv.setBackgroundColor(Color.RED);
			scale2(convertView, 1f - percent_add);
			convertView.setWillNotDraw(false);
			
			return convertView;
		}

		@Override
		public void frashViewList() {
			// TODO Auto-generated method stub
			notifyDataSetChanged();
		}

		@Override
		public void scaleView(View v, float percent) {
			// TODO Auto-generated method stub
			scale(v, percent);
		}

		@Override
		public void animaView(View v, Animation ani) {
			// TODO Auto-generated method stub
			Holder holder = (Holder) v.getTag();
			holder.siv.startAnimation(ani);
		}

		@Override
		public void animaClearView(View v) {
			// TODO Auto-generated method stub
			Holder holder = (Holder) v.getTag();
			holder.siv.clearAnimation();
		}

		@Override
		public int removeView(View v) {
			// TODO Auto-generated method stub
			int id = 0;
			Holder holder = (Holder) v.getTag();
			if (holder.id + 1 >= mNames.size()) {
				id = holder.id - 1;
			} else {
				id = holder.id;
			}
			if (id < 0) {
				holder.id = 0;
			}
			if (mNames.size() == 1) {
				id = holder.id;
			} else {
				mNames.remove(holder.id);
			}
			return id;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.d("onItemClick", "onItemClick = " + arg2 + "pos=" + mHListView.getPositionForView(arg1));
		Holder holder = (Holder) arg1.getTag();
		holder.siv.setBackgroundColor(Color.BLUE);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		Log.d("onItemClick", "onItemLongClick = " + arg2);
		Holder holder = (Holder) arg1.getTag();
		holder.siv.setBackgroundColor(Color.GREEN);
		return false;
	}

	@Override
	public void onScrollStateChanged(AbsHAdapterView view, int scrollState) {
		// TODO Auto-generated method stub
		Log.d("onScrollStateChanged", "scrollState=" + scrollState);
	}

	@Override
	public void onScroll(AbsHAdapterView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		//Log.d("onScroll", "first=" + firstVisibleItem + ",visible =" + visibleItemCount + ",total=" + totalItemCount);
		
	}

	class HScrollListener2 implements HCSListView.OnItemScrollListener {

		@Override
		public void OnScrollCenter(AdapterView<ListAdapter> adp, View v,
								   int pos, float percent) {
			// TODO Auto-generated method stub
			Log.i(TAG, "OnScrollCenter pos=" + pos + ", percent=" + percent);
			float scale = (1f - percent_add) + percent_add * percent;
			scale2(v, scale);
			if (adp.getChildCount() > pos + 1) {
				scale2(adp.getChildAt(pos + 1), 1f + percent_add - scale);
			}
			if (pos > 0) {
				scale2(adp.getChildAt(pos - 1), 1f - percent_add);
			}

		}

		@Override
		public void OnScrollStart(AdapterView<ListAdapter> adp) {
			// TODO Auto-generated method stub
			Log.i(TAG, "OnScrollStart");
		}

		@Override
		public void OnScrollEnd(AdapterView<ListAdapter> adp, int pos) {
			// TODO Auto-generated method stub
			Log.i(TAG, "OnScrollEnd pos=" + pos);
			//mODetector.forceOrientationChanged();
		}

		@Override
		public boolean OnDraging(AdapterView<ListAdapter> adp, float dx,
								 float dy) {
			// TODO Auto-generated method stub
			Log.i(TAG, "OnDraging dx=" + dx + ", dy=" + dy);
			return false;
		}

		@Override
		public boolean OnDragingOver(AdapterView<ListAdapter> adp) {
			// TODO Auto-generated method stub
			Log.i(TAG, "OnDragingOver");
			return true;
		}

	}
}
