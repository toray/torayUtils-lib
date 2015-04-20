package com.toraysoft.utils.image.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.toraysoft.utils.image.ImageUtil;

public class ImageCutChooserActivity extends Activity {

	int CHOOSE_IMAGE = 999;
	int CUT_IMAGE = 998;

	String tempFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String tempfolder = null;
		if (getIntent().hasExtra("temp")) {
			tempfolder = getIntent().getStringExtra("temp");
		} else {
			tempfolder = Environment.getExternalStorageDirectory() + "/temp/";
		}
		File ffolder = new File(tempfolder);
		if (!ffolder.exists()) {
			ffolder.mkdirs();
		}
		tempFile = tempfolder + "tmp_" + System.currentTimeMillis() + ".jpg";

		Intent intent = ImageUtil.getImageChooserIntent("");
		startActivityForResult(intent, CHOOSE_IMAGE);
	}

	void cutImage(Uri uri) {
		int w = getIntent().getIntExtra("width", 0);
		int h = getIntent().getIntExtra("height", 0);
		if (w > 0 && h > 0) {
			File f = new File(tempFile);
			Intent intent = ImageUtil.getCutImageIntent(uri, Uri.fromFile(f),
					w, h, w, h);
			startActivityForResult(intent, CUT_IMAGE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CHOOSE_IMAGE) {
				Uri imgUri = data.getData();
				cutImage(imgUri);
			} else if (requestCode == CUT_IMAGE) {
				data.putExtra("file_path", tempFile);
				setResult(RESULT_OK, data);
				finish();
			}
		} else {
			finish();
		}
	}

}
