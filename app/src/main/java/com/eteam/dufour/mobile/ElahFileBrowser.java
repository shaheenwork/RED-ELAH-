package com.eteam.dufour.mobile;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eteam.dufour.objects.Locations;
import com.eteam.utils.StorageOptions;

public class ElahFileBrowser extends Activity implements OnItemClickListener, OnClickListener{
// ===========================================================
// Constants
// ===========================================================
// ===========================================================
// Fields
// ===========================================================
	private GridView mGrid;
	private ListView mList;
	private LocationAdapter mListAdapter;
	private File mCurrentDir;
	private ArrayList<File> mFiles;
	private String[] mFilters = {"xls"};
	private Locations[] fileLocations;
	private View closebtn;
	
// ===========================================================
// Constructors
// ===========================================================

// ===========================================================
// Getter & Setter
// ===========================================================

// ===========================================================
// Methods for/from SuperClass/Interfaces
// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initFileLocations();
		setContentView(R.layout.filebrowser);
		initialize();
		setListView();
		setGridView();
		setListeners();
		browseTo(fileLocations[0]);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id)
	{
		File file = mFiles.get((int)id);
		
		if(file.isDirectory())
		{
			browseTo(file);
		}
		else
		{	// Send back the file that was selected
			Uri path = Uri.fromFile(file);
			Intent intent = new Intent(Intent.ACTION_PICK, path);
			setResult(RESULT_OK, intent);			
			finish();
		}
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==closebtn){
			finish();
		}
	}

// ===========================================================
// Methods
// ===========================================================

	private void setListeners() {
		// TODO Auto-generated method stub
		closebtn.setOnClickListener(this);
	}

	private void initFileLocations() {
		// TODO Auto-generated method stub
		StorageOptions.determineStorageOptions();
		fileLocations = new Locations[StorageOptions.count+1];
		
		for(int i=0;i<StorageOptions.count;i++){		
			Locations location = new Locations();
			location.key = StorageOptions.labels[i];
			location.path = StorageOptions.paths[i];
			fileLocations[i] = location;
		}
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.ECLAIR_MR1){
			Locations location = new Locations();
			location.key = "Downloads";
			location.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			fileLocations[StorageOptions.count] = location;
		}
	}
	
	private void browseTo(Locations location){
		browseTo(new File(location.path));
	}

	private void initialize() {
		// TODO Auto-generated method stub
		mGrid = (GridView) findViewById(R.id.filebrowser_grid);
		mList = (ListView) findViewById(R.id.filebrowser_list);
		closebtn = findViewById(R.id.filebrowser_close);
		mFiles = new ArrayList<File>();
		
		
	}
	
	public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

	private void setListView() {
		// TODO Auto-generated method stub
		mListAdapter = new LocationAdapter();
		mList.setAdapter(mListAdapter);
		mList.setOnItemClickListener(listItemClick);
	}
	
	@SuppressWarnings("deprecation")
	private void setGridView() {
		// TODO Auto-generated method stub
		Display display = getWindowManager().getDefaultDisplay();
		mGrid.setNumColumns(display.getWidth()/140);
		mGrid.setOnItemClickListener(this);
	}
	
	private synchronized void browseTo(final File location)
	{
		mCurrentDir = location;
		
		mFiles.clear();
		
		this.setTitle(mCurrentDir.getName().compareTo("") == 0 ? mCurrentDir.getPath() : mCurrentDir.getName());
		
		if(location.getParentFile() != null) mFiles.add(mCurrentDir.getParentFile());
		if(mCurrentDir.listFiles()!=null){
			for(File file : mCurrentDir.listFiles())
			{
				if(file.isDirectory())
				{
					mFiles.add(file);
				}
				else if(mFilters != null)
				{
					for(String ext : mFilters)
					{
						if(file.getName().endsWith(ext))
						{
							mFiles.add(file);
							continue;
						}
					}
				}
				else
				{
					mFiles.add(file);
				}
			}
		}
		
		if(mGrid != null) mGrid.setAdapter(new IconAdapter());
	}

// ===========================================================
// Inner and Anonymous Classes
// ===========================================================	
	
	private class IconAdapter extends BaseAdapter
	{	ImageView image;
		TextView text_field;
		@Override
		public int getCount()
		{
			return mFiles.size();
		}

		@Override
		public Object getItem(int index)
		{
			return mFiles.get(index);
		}

		@Override
		public long getItemId(int index)
		{
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent)
		{
		
			File currentFile = mFiles.get(index);
			
			int iconId;
			String filename;
			
			if(index == 0 && (currentFile.getParentFile() == null || currentFile.getParentFile().getAbsolutePath().compareTo(mCurrentDir.getAbsolutePath()) != 0))
			{
				iconId = R.drawable.updirectory;
				filename = new String("..");
			}
			else
			{
				iconId = getIconId(index);
				filename = currentFile.getName();
			}

			if(convertView == null)
			{	LayoutInflater inflater = LayoutInflater.from(ElahFileBrowser.this);           
				convertView = inflater.inflate(R.layout.gridrow, null);
			}
			
			image = (ImageView) convertView.findViewById(R.id.grid_image);
			text_field = (TextView) convertView.findViewById(R.id.grid_filename);
			
			image.setImageResource(iconId);
			text_field.setText(filename);
			
			return convertView;
		}
		
		private int getIconId(int index)
		{
			File file = mFiles.get(index);
			
			if(file.isDirectory()) return R.drawable.directory;
			
			if(file.getName().endsWith("xls")) return R.drawable.xl_icon;
			
			return R.drawable.unknown;
		}
	}
	
	private class LocationAdapter extends BaseAdapter{
		TextView text_field ;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fileLocations.length;
		}

		@Override
		public Locations getItem(int position) {
			// TODO Auto-generated method stub
			return fileLocations[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.list_row, null);
			}
			text_field = (TextView) convertView.findViewById(R.id.list_row_label);
			text_field.setText(fileLocations[position].key);
			return convertView;
		}
		
	}
	
// ===========================================================
// Inner and Anonymous Interfaces
// ===========================================================	
	private OnItemClickListener listItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			browseTo(fileLocations[position]);
		}
	};
}
