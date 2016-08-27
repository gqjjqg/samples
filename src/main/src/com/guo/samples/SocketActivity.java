package com.guo.samples;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.guo.android_extend.network.socket.Data.AbsTransmitter;
import com.guo.android_extend.network.socket.OnSocketListener;
import com.guo.android_extend.network.socket.SocketModule;
import com.guo.android_extend.network.udp.UDPModule;
import com.guo.android_extend.widget.ExtImageView;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqj3375 on 2015/12/22.
 */
public class SocketActivity extends Activity implements UDPModule.OnUDPListener, AdapterView.OnItemClickListener, OnSocketListener, View.OnClickListener {

	private final String TAG = this.getClass().getSimpleName();

	private UDPModule mUDPModule;

	private SocketModule mSocketModule;

	private ListDevice mListDevice;

	private String workdir = Environment.getExternalStorageDirectory().getPath() + "/DownLoad/";

	CheckBox mCheckBox;
	TextView mTextView;
	String mData = Build.MODEL + "Send:";
	int mCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_socket);
		ListView view = (ListView) findViewById(R.id.listView);
		mCheckBox = (CheckBox) findViewById(R.id.checkBox1);
		view.setOnItemClickListener(this);
		Button btn = (Button) findViewById(R.id.button);
		btn.setOnClickListener(this);
		mTextView = (TextView) findViewById(R.id.textView);
		mTextView.setText(mData + mCount);

		mUDPModule = new UDPModule(this, 5000);
		mUDPModule.setOnUDPListener(this);
		mListDevice = new ListDevice(this);
		view.setAdapter(mListDevice);

		mSocketModule = new SocketModule(workdir);
		mSocketModule.setOnSocketListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mSocketModule != null) {
			mSocketModule.destroy();
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UDPModule.Device dev = mListDevice.mData.get(position);
		if (mSocketModule != null) {
			mSocketModule.connect(dev.mIP);
		}
	}

	@Override
	public void onSocketException(int e) {
		Log.e(TAG, "ERROR=" + e);
	}

	@Override
	public void onSocketEvent(Socket socket, int e) {
		Log.e(TAG, "EVENT=" + e);
		if (e == OnSocketListener.EVENT_RECEIVER_CONNECTED) {
			Log.e(TAG, "EVENT_RECEIVER_CONNECTED");
		} else if (e == OnSocketListener.EVENT_RECEIVER_DISCONNECTED) {
			Log.e(TAG, "EVENT_RECEIVER_DISCONNECTED");
		} else if (e == OnSocketListener.EVENT_SENDER_CONNECTED) {
			Log.e(TAG, "EVENT_SENDER_CONNECTED : " + socket.isConnected());
		} else if (e == OnSocketListener.EVENT_SENDER_DISCONNECTED) {
			Log.e(TAG, "EVENT_SENDER_DISCONNECTED");
		}
	}

	@Override
	public void onReceiveProcess(AbsTransmitter obj, int percent) {
		Log.d(TAG, "onFileReceiveProcess [" + obj.getName() + "] progress:" + percent);
	}

	@Override
	public void onReceived(AbsTransmitter obj) {
		Log.d(TAG, "onFileReceived [" + obj.getName() + "]");
		final String name = obj.getName();
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {mTextView.setText(name);
			}
		});Log.d(TAG, "onFileReceived [" + name + "]");
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mTextView.setText(name);
			}
		});
	}

	@Override
	public void onSendProcess(AbsTransmitter obj, int percent) {
		Log.d(TAG, "onFileSendProcess [" + obj.getName() + "] progress:" + percent);
	}

	@Override
	public void onSended(AbsTransmitter obj) {
		Log.d(TAG, "onFileSended [" + obj.getName() + "]");
	}

	@Override
	public void onClick(View v) {
		if (!mCheckBox.isChecked()) {
			mCount++;
			String val = mData + mCount;
			byte[] data = val.getBytes();
			boolean success = mSocketModule.send(data, data.length);
			if (success) {
				Log.d(TAG, "Send success!");
			} else {
				Log.d(TAG, "Send fail!");
			}
		} else {
			boolean success = mSocketModule.send(workdir + "test.APK");
			if (success) {
				Log.d(TAG, "Send success!");
			} else {
				Log.d(TAG, "Send fail!");
			}
		}
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
