package com.guo.samples;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.guo.android_extend.network.socket.TCP.SocketClient;
import com.guo.android_extend.network.socket.TCP.SocketServer;
import com.guo.android_extend.network.socket.UDP.UDPModule;
import com.guo.android_extend.widget.ExtImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqj3375 on 2015/12/22.
 */
public class SocketActivity extends Activity implements UDPModule.OnUDPListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener {

	private UDPModule mUDPModule;

	private SocketClient mSocketClient;
	private SocketServer mSocketServer;

	private ListDevice mListDevice;

	private String workdir = Environment.getExternalStorageDirectory().getPath() + "/DownLoad/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_socket);
		ListView view = (ListView) findViewById(R.id.listView);
		CheckBox box = (CheckBox) findViewById(R.id.checkBox1);
		box.setOnCheckedChangeListener(this);
		view.setOnItemClickListener(this);

		mUDPModule = new UDPModule(this, Build.MODEL);
		mUDPModule.setOnUDPListener(this);
		mListDevice = new ListDevice(this);
		view.setAdapter(mListDevice);

		mSocketClient = null;
		mSocketServer = null;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mSocketServer != null) {
			mSocketServer.destroy();
		}
		if (mSocketClient != null) {
			mSocketClient.destroy();
		}
		mUDPModule.destroy();
	}

	@Override
	public void onReceiveDevice(List<UDPModule.Device> list, String name, String ip) {
		mListDevice.mData = list;
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListDevice.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			mSocketServer = new SocketServer(workdir);
		} else {
			mSocketServer.destroy();
			mSocketServer = null;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UDPModule.Device dev = mListDevice.mData.get(position);
		if (mSocketClient != null) {
			mSocketClient.destroy();
		}
		mSocketClient = new SocketClient(workdir, dev.mIP);
	}

	private class Holder {
		ExtImageView siv;
		TextView tv;
		int id;
	}

	private class ListDevice extends BaseAdapter {

		List<UDPModule.Device> mData;

		LayoutInflater mLInflater;

		public ListDevice(Context context) {
			super();
			mData = new ArrayList<UDPModule.Device>();
			mLInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView != null) {
				holder = (Holder) convertView.getTag();
			} else {
				convertView = mLInflater.inflate(R.layout.item_sample, null);
				holder = new Holder();
				holder.siv = (ExtImageView) convertView.findViewById(R.id.imageView1);
				holder.tv = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			}

			UDPModule.Device data = mData.get(position);

			holder.tv.setText(data.mName + ":" + data.mIP);
			holder.siv.setImageResource(R.drawable.ic_launcher);

			return convertView;
		}
	}
}
