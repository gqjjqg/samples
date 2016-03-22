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
import android.widget.Toast;

import com.guo.android_extend.network.socket.OnSocketListener;
import com.guo.android_extend.network.socket.SocketClient;
import com.guo.android_extend.network.socket.SocketModule;
import com.guo.android_extend.network.udp.UDPModule;
import com.guo.android_extend.widget.ExtImageView;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqj3375 on 2015/12/22.
 */
public class SocketClientActivity extends Activity implements UDPModule.OnUDPListener, AdapterView.OnItemClickListener, OnSocketListener, View.OnClickListener {

	private final String TAG = this.getClass().getSimpleName();

	private UDPModule mUDPModule;

	private SocketClient mSocketClient;

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

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Client");

		mUDPModule = new UDPModule(this, Build.MODEL);
		mUDPModule.setOnUDPListener(this);
		mListDevice = new ListDevice(this);
		view.setAdapter(mListDevice);

		mSocketClient = null;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mSocketClient != null) {
			mSocketClient.shutdown();
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
		if (mSocketClient != null) {
			mSocketClient.disconnect();
		}
		mSocketClient = new SocketClient(workdir);
		mSocketClient.setOnSocketListener(this);
		mSocketClient.connect(dev.mIP);
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
	public void onFileReceiveProcess(String file, int percent) {
		Log.d(TAG, "onFileReceiveProcess [" + file + "] progress:" + percent);
	}

	@Override
	public void onFileReceived(final String file) {
		Log.d(TAG, "onFileReceived [" + file + "]");
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mTextView.setText(file);
			}
		});
	}

	@Override
	public void onFileSendProcess(String file, int percent) {
		Log.d(TAG, "onFileSendProcess [" + file + "] progress:" + percent);
	}

	@Override
	public void onFileSended(String file) {
		Log.d(TAG, "onFileSended [" + file + "]");
	}

	@Override
	public void onDataReceiveProcess(String tag, int percent) {
		Log.d(TAG, "onDataReceiveProcess [" + tag + "] progress:" + percent);
	}

	@Override
	public void onDataReceived(String tag, byte[] data) {
		final String val = new String(data, 0, data.length);
		Log.d(TAG, "onDataReceived=" + val + "," + tag + "," + data.length);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mTextView.setText(val);
			}
		});
	}

	@Override
	public void onDataSendProcess(String tag, int percent) {
		Log.d(TAG, "onDataSendProcess progress:" + percent);
	}

	@Override
	public void onDataSended(String tag) {
		Log.d(TAG, "onDataSended [" + tag + "]");
	}

	@Override
	public void onClick(View v) {
		if (mSocketClient == null) {
			return ;
		}
		if (!mCheckBox.isChecked()) {
			mCount++;
			String val = mData + mCount;
			byte[] data = val.getBytes();
			boolean success = mSocketClient.send(data, data.length);
			if (success) {
				Log.d(TAG, "Send success!");
			} else {
				Log.d(TAG, "Send fail!");
			}
		} else {
			boolean success = mSocketClient.send(workdir + "test.APK");
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
