package max.popov;

import alex.taran.opengl.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	
	// references to our images
    private Integer[] mThumbIds = {
            R.drawable.blue1, R.drawable.blue2,
            R.drawable.blue3, R.drawable.blue4,
            R.drawable.blue5, R.drawable.blue6,
            R.drawable.blue7, R.drawable.blue8,
            R.drawable.blue9, R.drawable.blue10,
            R.drawable.blue11, R.drawable.blue12,
            R.drawable.blue13, R.drawable.blue14,
            R.drawable.blue15, R.drawable.blue16,
            R.drawable.blue17, R.drawable.blue18,
            R.drawable.blue19, R.drawable.blue20,
            R.drawable.blue21, R.drawable.blue22
    };
    
    
    public ImageAdapter(Context c) {
    	mContext = c;
    }
    
    public int getCount() {
    	return mThumbIds.length;
    }
    
    public Object getItem(int position) {
    	return mThumbIds[position];
    }
    
    public long getItemId(int position) {
    	return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
    	ImageView imageView;
    	if (convertView == null) {  // if it's not recycled, initialize some attributes
    		
    		imageView = new ImageView(mContext);
    		imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
    		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    		imageView.setPadding(8, 8, 8, 8);
    		
    	}else{
    			imageView = (ImageView) convertView;
    	}
    	
    	imageView.setImageResource(mThumbIds[position]);
    	
    	
    	return imageView;
    }

	    
}
