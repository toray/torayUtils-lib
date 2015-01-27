package com.toraysoft.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class FileUtil {

	/**
	 * 读取assets目录下的文本文件
	 * 
	 * @param context
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> readAssetFile(Context context, String path)
			throws IOException {
		List<String> ret = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new BufferedInputStream(context.getAssets().open(path))));
		String line = null;
		while ((line = reader.readLine()) != null) {
			ret.add(line);
		}
		return ret;
	}

	// 将字符串写入到文本文件中
	public static void writeTxtFile(String strcontent, String strFilePath) {
		// 每次写入时，都换行写
		String strContent = strcontent + "\n";
		try {
			File file = new File(strFilePath);
			if (!file.exists()) {
				Log.d("File", "Create the file:" + strFilePath);
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(strContent.getBytes());
			raf.close();
		} catch (Exception e) {
			Log.e("File", "Error on write File.");
			e.printStackTrace();
		}
	}
}
