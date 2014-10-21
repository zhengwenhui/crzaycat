package com.zwh.crzaycat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends Activity implements ActionInterface{
	private ReseauView mReseauView;
	private GifView mGifView;
/*	private GifView mGifView1;
	private GifView mGifView2;
	private GifView mGifView3;*/
	private Dialog mDialog;
	//private GifView mGifViewCur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*mGifView1 = (GifView)findViewById(R.id.gif_view1);
		mGifView1.setGifImage(R.drawable.bianzi);
		mGifView2 = (GifView)findViewById(R.id.gif_view2);
		mGifView2.setGifImage(R.drawable.yang);
		mGifView3 = (GifView)findViewById(R.id.gif_view3);
		mGifView3.setGifImage(R.drawable.hospital);
		mGifViewCur = mGifView2;
		mGifViewCur.setVisibility(View.INVISIBLE);*/

		mGifView = (GifView)findViewById(R.id.gif_view);
		mGifView.setGifImage(R.drawable.yang);


		mReseauView = (ReseauView) findViewById(R.id.reseau_view);
		mReseauView.setActionInterface(this);

		mReseauView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mReseauView.checkTouchPostion(event.getX(), event.getY());
					break;

				default:
					break;
				}

				return false;
			}
		});
	}

	/*public void onClickGifView(View view){
		Log.i("onclick", "onClickGifView");

		switch (view.getId()) {
		case R.id.gif_view1:
			mGifView.setGifImage(R.drawable.bianzi);
			break;
		case R.id.gif_view2:
			mGifView.setGifImage(R.drawable.yang);
			break;
		case R.id.gif_view3:
			mGifView.setGifImage(R.drawable.hospital);
			break;

		default:
			break;
		}

		mGifViewCur.setVisibility(View.VISIBLE);
		mGifViewCur = (GifView) view;
		mGifViewCur.setVisibility(View.INVISIBLE);
	}*/

	@Override
	public void gameOver(boolean success, int step) {
		// TODO Auto-generated method stub
		String str = success ? "success" : "fail";
		Log.e("game over", str);

		if( null == mDialog){
			mDialog = new Dialog(this,R.style.NobackDialog);

			LayoutInflater mInflater =  LayoutInflater.from(this);
			View view = mInflater.inflate(R.layout.dialog_layout,null);  	

			if(!checkApkExist(this, "com.tencent.mm")){
				View moment = view.findViewById(R.id.moment);
				moment.setVisibility(View.INVISIBLE);
			}

			mDialog.setContentView(view); 
			mDialog.setCanceledOnTouchOutside(false);
		}
		mDialog.show();
	}

	public void onClickButton(View view){
		String str = "help";
		switch (view.getId()) {
		case R.id.restart:
			mReseauView.reStart();
			mDialog.dismiss();
			break;

		case R.id.moment:
			shareImageAndText(str);
			break;

		case R.id.share:
			share(str);
			break;

		default:
			break;
		}
	}

	private void share(String text){
		Intent intent=new Intent(Intent.ACTION_SEND);   
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void shareImageAndText(String text){
		String sdcardPath = Environment.getExternalStorageDirectory().getPath();
		FileCopyFromAssetsToSD(this, "message.png");

		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/png");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.putExtra("Kdescription", text);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+sdcardPath+"/message.png"));
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(intent);
	}

	public boolean checkApkExist(Context context, String packageName) {
		PackageInfo packageInfo = null;
		try {  
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {  
			packageInfo = null;
			e.printStackTrace();
		}

		return packageInfo != null;
	} 

	public void FileCopyFromAssetsToSD(Context context, String fileName){
		int BUFFER_LEN = 1024;
		AssetManager assetManager = context.getAssets();
		InputStream is;
		FileOutputStream fos;

		try {
			is = assetManager.open(fileName);

			File out = new File(Environment.getExternalStorageDirectory(), fileName);
			byte[] buffer = new byte[BUFFER_LEN];
			fos = new FileOutputStream(out);
			int read = 0;

			while ((read = is.read(buffer, 0, BUFFER_LEN)) >= 0) {
				fos.write(buffer, 0, read);
			}

			fos.flush();
			fos.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void moveView(int x, int y) {
		// TODO Auto-generated method stub
		int halfWidth = mGifView.getWidth() / 2;
		int height = mGifView.getHeight();

		mGifView.layout(x - halfWidth, y - height, x + halfWidth, y);
	}
}
