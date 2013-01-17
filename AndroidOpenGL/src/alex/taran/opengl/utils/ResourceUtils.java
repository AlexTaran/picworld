package alex.taran.opengl.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import alex.taran.opengl.AndroidOpenGLActivity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class ResourceUtils {
	public static List<String> loadRawTextFile(Context context, String name) {
		Resources res = context.getResources();
		int id = res.getIdentifier(name, "raw", context.getPackageName());
		InputStream inputStream = res.openRawResource(id);

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
}
