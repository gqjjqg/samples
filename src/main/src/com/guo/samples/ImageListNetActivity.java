package com.guo.samples;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.guo.android_extend.cache.BitmapMonitor;
import com.guo.android_extend.cache.BitmapMonitorThread;
import com.guo.android_extend.java.network.http.Downloader;
import com.guo.android_extend.java.network.http.DownloaderManager;
import com.guo.android_extend.widget.ExtImageView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * A list view example where the 
 * data for the list comes from an array of strings.
 */
public class ImageListNetActivity extends ListActivity implements OnItemClickListener , FilenameFilter{

	private BitmapMonitorThread<ExtImageView, String> mCacheThread;
	
	private DownloaderManager<String> mDownloaderManager;
	
	private ListImage mListImage;
	private String mImagePath;
    private Handler mHandler;
	
	private String name[] = {
			"http://gqjjqg.github.io/images/00.jpg",
			"http://gqjjqg.github.io/images/01.jpg",
			"http://gqjjqg.github.io/images/02.jpg",
			"http://gqjjqg.github.io/images/03.jpg",
			"http://gqjjqg.github.io/images/04.jpg",
			"http://gqjjqg.github.io/images/05.jpg",
			"http://gqjjqg.github.io/images/06.jpg",
			"http://gqjjqg.github.io/images/07.jpg",
			"http://gqjjqg.github.io/images/08.jpg",
			"http://gqjjqg.github.io/images/09.jpg"
	};
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        mImagePath = Environment.getExternalStorageDirectory().getPath() + "/DownLoad/";
        
        mListImage = new ListImage(this, name);
        setListAdapter(mListImage);
		getListView().setOnItemClickListener(this);
        mCacheThread = new BitmapMonitorThread<ExtImageView, String>(new Handler());
        mCacheThread.start();

        mHandler = new Handler();
        mDownloaderManager = new DownloaderManager<String>();
    }
    
    private class ListImage extends BaseAdapter {
    	
    	public class Holder {
    		ExtImageView siv;
    		TextView tv;
    		Data data;
    	}

        private class DataFresh implements Runnable {

            Data mData;
            boolean isSuccess;

            private DataFresh(Data mData, boolean ok) {
                this.mData = mData;
                this.isSuccess = ok;
            }

            @Override
            public void run() {
                Log.d("debug", mData.getRemoteFileName() + ", finish:" + isSuccess);

                ListView root = ImageListNetActivity.this.getListView();
                for (int i = 0; i < root.getChildCount(); i++) {
                    Holder holder = (Holder) root.getChildAt(i).getTag();
                    if (holder.data.equals(mData)) {
                        if (isSuccess) {
                            mCacheThread.postLoadBitmap(new Mointor(holder.siv, mData.getLocalFile(), mData));
                        } else {
                            //mHttpDownloadThread.postLoadImage(content);
                        }
                        break;
                    }
                }
            }
        }

    	private class Data extends Downloader<String> {

            @Override
            public void onTaskOver(Downloader context, int error) {
                super.onTaskOver(context, error);
                mHandler.post(new DataFresh(this, error == DOWNLOAD_OK));
            }

            @Override
            public void onDownloadUpdate(Downloader context, int percent) {

            }

            private Data(String mLocalDir, String mUrl, String mID) {
                super(mLocalDir, mUrl, mID);
            }
    	}
    	
    	private class Mointor extends BitmapMonitor<ExtImageView, String> {
	
    		private Downloader<String> data;
    		
    		public Mointor(ExtImageView view, String id, Downloader<String> data) {
				super(view, id);
				// TODO Auto-generated constructor stub
				this.data = data;
			}

    		@Override
    		public Bitmap decodeImage() {
    			// TODO Auto-generated method stub
    			super.mBitmap = null;
    			try {
    				
    				File file = new File(data.getLocalFile());
    				if (!file.exists()) {
    					//post download.
    					mDownloaderManager.postDownload(data);
    					return null;
    				}
    				
    				BitmapFactory.Options op = new BitmapFactory.Options();    
    		        op.inJustDecodeBounds = true;
    		        super.mBitmap = BitmapFactory.decodeFile(super.mBitmapID, op);
    		        op.inJustDecodeBounds = false;
    		        int h = op.outHeight;  
    		        int w = op.outWidth;  
    		        int beWidth = w / 320;  
    		        int beHeight = h / 240;  
    		        int be = 1;  
    		        if (beWidth < beHeight) {  
    		            be = beWidth;  
    		        } else {  
    		            be = beHeight;  
    		        }  
    		        if (be <= 0) {  
    		            be = 1;  
    		        }  
    		        op.inSampleSize = be;  
    		        super.mBitmap = BitmapFactory.decodeFile(super.mBitmapID, op);  
    		        super.mBitmap = ThumbnailUtils.extractThumbnail(super.mBitmap, 320, 240,  
    		                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    			} catch (Exception e) {
    		    	e.printStackTrace();
    		    }
    			return super.mBitmap;
    		}

			@Override
			protected void freshBitmap(boolean isOld) {
				// TODO Auto-generated method stub
				super.mView.setImageResource(0);
    			super.mView.setImageBitmap(null);
    			if (!isOld && super.mBitmap != null) {
	    			super.mView.setImageBitmap(super.mBitmap);
    			} else {
    				super.mView.setImageResource(R.drawable.ic_launcher);
    			}
				super.mView.invalidate();
			}

        }
    	
    	List<Data> mData;
    	LayoutInflater mLInflater;
		
		public ListImage(Context context, String[] files) {
			super();
			mLInflater = LayoutInflater.from(context);
			mData = new ArrayList<Data>();
			for (int i = 0; i < files.length; i++) {
				mData.add(new Data(mImagePath, files[i], files[i]));
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
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
				convertView = mLInflater.inflate(R.layout.item_sample, null);
				holder = new Holder();
				holder.siv = (ExtImageView) convertView.findViewById(R.id.imageView1);
				holder.tv = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			}
			
			Data data = mData.get(position);
			holder.data = data;
			holder.tv.setText(data.getRemoteFileName());
			holder.siv.setImageResource(R.drawable.ic_launcher);
			
			mCacheThread.postLoadBitmap(new Mointor(holder.siv, data.getLocalFile(), data));
			return convertView;
		}
    	
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		String full = ((ListImage.Data)mListImage.getItem(position)).getLocalFile();
		Log.d("Image", full);
		Intent intent = new Intent(ImageListNetActivity.this, ImageViewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", full);
		intent.putExtras(bundle);
		startActivity(intent);
	}

    /* (non-Javadoc)
	 * @see android.app.ListActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		/*final int count = this.getListView().getChildCount();
		for (int i = 0; i < count ; i++ ) {
			Holder holder = (Holder) this.getListView().getChildAt(i).getTag();
			holder.siv.setImageBitmap(null);
		}*/
		
		mCacheThread.shutdown();
        mDownloaderManager.shutdown();
	}
	
	@Override
	public boolean accept(File arg0, String arg1) {
		// TODO Auto-generated method stub
		return arg1.endsWith(".jpg") || arg1.endsWith(".png");
	}
    
}
