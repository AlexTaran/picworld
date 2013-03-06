package alex.taran.opengl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import alex.taran.opengl.AndroidOpenGLActivity;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.util.Log;

public class ResourceUtils {
	public static List<String> loadRawTextFile(Context context, String name) {
		return loadInputStreamAsLines(getInputStreamForRawResource(context, name));
	}
	
	public static InputStream getInputStreamForRawResource(Context context, String name) {
		Resources res = context.getResources();
		int id = res.getIdentifier(name, "raw", context.getPackageName());
		InputStream inputStream = res.openRawResource(id);

		return inputStream;
	}
	
	public static List<String> loadInputStreamAsLines(InputStream inputStream) {
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		List<String> file = new ArrayList<String>();
		String s;
		try {
			while ((s = buffreader.readLine()) != null) {
				file.add(s);
			}
		} catch (IOException e) {
			return null;
		}
		return file;
	}
	
	public static InputStream getInputStreamForRawFile(String fileName) {
		Log.i("FUCK", "Start getting resource " + fileName);
		Resources res = AndroidOpenGLActivity.getContext().getResources();
		Log.i("FUCK", "Start getting resource id");
		int id = res.getIdentifier(fileName, "raw", AndroidOpenGLActivity.getContext().getPackageName());
		Log.i("FUCK", "Start getting inputstream, id = " + id);
		InputStream fileInput = res.openRawResource(id);
		Log.i("FUCK", "Input stream opened! returning ..." + fileInput);
		return fileInput;
	}
	
	public static void saveObjectToExternalStorage(Context context, Serializable obj, String filename) {
		File root = android.os.Environment.getExternalStorageDirectory(); 
		File myFolder = new File(root, "PicWorld");
		myFolder.mkdir();
		File file = new File(myFolder, filename);
		try {
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream os = new ObjectOutputStream(f);
			os.writeObject(obj);
			os.close();
			f.close();
		} catch (Exception e) {
			Log.e("FUCK", "Exception while saving serialized object to file! " + e.getMessage());
		}
		MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
	}
}
