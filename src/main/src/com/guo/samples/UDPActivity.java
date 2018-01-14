package com.guo.samples;

import android.app.ListActivity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guo.android_extend.java.network.udp.UDPModule;
import com.guo.android_extend.widget.ExtImageView;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqj3375 on 2015/12/22.
 */
public class UDPActivity extends ListActivity implements UDPModule.OnUDPListener {
	private String TAG = "UDPActivity";
	private UDPModule mUDPModule;

	private ListDevice mListDevice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		String mac = wifiManager.getConnectionInfo().getMacAddress();

		mUDPModule = new UDPModule(multicastLAN(), false, mac, Build.MODEL,5000);
		mUDPModule.setOnUDPListener(this);
		mListDevice = new ListDevice(this);
		this.setListAdapter(mListDevice);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		mUDPModule.destroy();
	}

	/**
	 *
	 * @param ip
	 * @param mask
	 * @return
	 * @throws Exception
	 */
	private InetAddress getBroadcastAddress(int ip, int mask) throws Exception {
		int broadcast = (ip & mask) | ~mask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++) {
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		}
		return InetAddress.getByAddress(quads);
	}

	public InetAddress multicastLAN() {
		try {
			InetAddress mInetAddress = InetAddress.getByName("224.0.0.5");
			Log.d(TAG, "multicast=" + mInetAddress);
			return mInetAddress;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String FormatString(int value){
		return String.format("%d.%d.%d.%d",
				(value & 0xff), (value >> 8 & 0xff),
				(value >> 16 & 0xff), (value >> 24 & 0xff));
	}

	private void debug_print(WifiManager wifiManager) {
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		Log.d(TAG, "AP=" + dhcpInfo.ipAddress + ",MS=" + dhcpInfo.netmask);
		String wifiProperty = "褰撳墠杩炴帴Wifi淇℃伅濡備笅锛" + wifiInfo.getSSID() + '\n' +
				"ip:" + FormatString(dhcpInfo.ipAddress) + '\n' +
				"mask:" + FormatString(dhcpInfo.netmask) + '\n' +
				"netgate:" + FormatString(dhcpInfo.gateway) + '\n' +
				"dns:" + FormatString(dhcpInfo.dns1);
		Log.d(TAG, wifiProperty);
		try {
			Log.d(TAG, "test:" + getBroadcastAddress(dhcpInfo.ipAddress, dhcpInfo.netmask));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 局域网广播
	 * @return true or not.
	 */
	public InetAddress broadcastLAN() {
		try {
			InetAddress mInetAddress;
			WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
			debug_print(wifiManager);
			if (dhcpInfo.ipAddress == 0) { // ANDROID AP
				mInetAddress = InetAddress.getByName("192.168.43.255");
			} else {
				mInetAddress = getBroadcastAddress(dhcpInfo.ipAddress, dhcpInfo.netmask);
			}
			Log.d(TAG, "broadcast=" + mInetAddress);
			return mInetAddress;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
