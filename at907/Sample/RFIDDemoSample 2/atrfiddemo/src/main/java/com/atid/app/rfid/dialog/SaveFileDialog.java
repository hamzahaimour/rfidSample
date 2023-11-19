package com.atid.app.rfid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.atid.app.rfid.R;
import com.atid.lib.util.StringUtil;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

//import com.atid.lib.util.diagnotics.ATLog;

//import android.icu.text.DateFormat;		// samsung galaxy S4 not found class , using instead of java.text.DateFormat;

public class SaveFileDialog extends BaseDialog implements OnItemClickListener, OnClickListener {
	
	private static final String TAG = SaveFileDialog.class.getSimpleName();
	private static final int INFO = 4;
	
	public static final int TYPE_DIRECTORY = 1;
	public static final int TYPE_FILE = 2;
	
	private static final int KBYTE = 1024;
	private static final int MBYTE = KBYTE * 1024;
	private static final int GBYTE = MBYTE * 1024;
	
	private static final String DEFAULT_FILE_NAME="list.txt";
	
	private Button btnBack;
	private TextView txtPath;
	private ListView lstList;
	private EditText edtFile;
	private Button btnNewDirectory;
	
	private FileListAdapter mAdapter;
	
	private File mFile;
	private File mSelectFile;
	private String mPath;
	
	public SaveFileDialog() {
		mFile = null;
		
		String storage = Environment.getExternalStorageState();
		if (!storage.equals(Environment.MEDIA_MOUNTED)) {
			mPath = Environment.getRootDirectory().getAbsolutePath();
		} else {
			mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		
		mFile = new File(mPath);
		mSelectFile = null;
	}
	
	public File getFile() {
		return mSelectFile;
	}
	
	@Override
	public void display() {
		
		mAdapter.clear();
		
		File[] dirs = mFile.listFiles();
		String modifyDate;
		String items="";
		
		txtPath.setText(mFile.getName());

		if (dirs != null) {
			// sort			
			Arrays.sort(dirs, new Comparator<File>() {
				@Override
				public int compare(File file1, File file2) {
					return file1.getName().compareTo(file2.getName());
				}
			});
			
			for (File item : dirs) {
				Date date = new Date(item.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				modifyDate = formater.format(date);
				
				if (item.isDirectory()) {
					File[] fileBuffer = item.listFiles();
					int length = 0;
					if (fileBuffer != null) {
						length = fileBuffer.length;
					} 
					
					if (length == 0)
						items = String.format(Locale.US, "%d item",length);
					else
						items = String.format(Locale.US, "%d items",length);
					
					mAdapter.add(TYPE_DIRECTORY, item.getAbsolutePath(), item.getName(), items, modifyDate);
				} else {
					items = getFileSize(item.length());
					mAdapter.add(TYPE_FILE, item.getAbsolutePath(), item.getName(), items, modifyDate);				
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void showDialog(Context context, String title, final IValueChangedListener changedListener,
                           final ICancelListener cancelListener) {

		LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_save_file, null);
		
		btnBack = (Button) root.findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		btnBack.setEnabled(false);
		txtPath = (TextView) root.findViewById(R.id.path);
		btnNewDirectory = (Button) root.findViewById(R.id.new_directory);
		btnNewDirectory.setOnClickListener(this);
		
		lstList = (ListView) root.findViewById(R.id.list);
		mAdapter = new FileListAdapter(context);
		lstList.setAdapter(mAdapter);
		lstList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lstList.setOnItemClickListener(this);
		
		edtFile = (EditText) root.findViewById(R.id.file);
		edtFile.setText(DEFAULT_FILE_NAME);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
		builder.setTitle(StringUtil.isNullOrEmpty(title)? "" : title);
		builder.setView(root);
		builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				mSelectFile = new File(String.format("%s/%s", mFile.getAbsolutePath(), edtFile.getText().toString()));
				if (changedListener != null) {
					changedListener.onValueChanged(SaveFileDialog.this);
				}
				Log.i(TAG, "INFO. showDialog().$PositiveButton.onClick()");

			}
		});
		
		builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (cancelListener != null) {
					cancelListener.onCanceled(null);
				}
				Log.i(TAG,  "INFO. showDialog().$NegativeButton.onClick()");
			}
		});
		
		builder.setCancelable(true);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if (cancelListener != null) {
					cancelListener.onCanceled(null);
				}
				Log.i(TAG,  "INFO. showDialog().onCancel()");
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				
				if (isRootDirectory(mFile)) {
					btnBack.setEnabled(false);
				} else {
					btnBack.setEnabled(true);
				}

				if (mFile.canWrite()) {
					btnNewDirectory.setEnabled(true);
				} else {
					btnNewDirectory.setEnabled(false);
				}
				
				display();
				
				Log.i(TAG,  "INFO. showDialog().onShow()");
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		WindowManager wm =
				((WindowManager)context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE)) ;
		
		LayoutParams params = dialog.getWindow().getAttributes();
		params.width = (int)( wm.getDefaultDisplay().getWidth() * 0.9 );
		params.height = (int)( wm.getDefaultDisplay().getHeight() * 0.9 );;
		
		dialog.getWindow().setAttributes(params);

		Log.i(TAG,  "INFO. showDialog()");
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.back:

			if (isRootDirectory(mFile.getParentFile())) {
				btnBack.setEnabled(false);
			} else {
				btnBack.setEnabled(true);
			}

			if( mFile.getParentFile() != null) {
				mFile = mFile.getParentFile();
			}

			if (mFile.getParentFile().canWrite()) {
				btnNewDirectory.setEnabled(true);
			} else {
				btnNewDirectory.setEnabled(false);
			}

			display();
			break;
			
		case R.id.new_directory:
			
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int type = mAdapter.getType(position);

		if (type == TYPE_DIRECTORY) {
			mFile = new File(mAdapter.getPath(position));
			display();
		} else if (type == TYPE_FILE) {
			edtFile.setText(mAdapter.getName(position));
		}

		if (isRootDirectory(mFile)) {
			btnBack.setEnabled(false);
		} else {
			btnBack.setEnabled(true);
		}
		
		if (mFile.canWrite()) {
			btnNewDirectory.setEnabled(true);
		} else {
			btnNewDirectory.setEnabled(false);
		}		
	}

	private boolean isRootDirectory(File file) {
		String storage = Environment.getExternalStorageState();
		String path = "";
		if (!storage.equals(Environment.MEDIA_MOUNTED)) {
			path = Environment.getRootDirectory().getAbsolutePath();
		} else {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		
		if( file.getParentFile() != null && (path.compareToIgnoreCase(file.getAbsolutePath()) == 0)) {
			return true;
		}
		return false;
	}
	
	private String getFileSize(long size) {
		if (size < KBYTE)
			return String.format(Locale.ENGLISH, "%d Bytes", size);
		else if (size < MBYTE)
			return String.format(Locale.ENGLISH, "%.2f KB", (float)size / KBYTE);
		else if (size < GBYTE)
			return String.format(Locale.ENGLISH, "%.2f MB", (float)size / MBYTE);
		else 
			return String.format(Locale.ENGLISH, "%.2f GB", (float)size / GBYTE);
	}
	// ------------------------------------------------------------------------
	// Declare class FileListAdapter
	// ------------------------------------------------------------------------
	public class FileListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private ArrayList<FileListItem> mList;
		private HashMap<String, FileListItem> mMap;
		
		public FileListAdapter(Context context) {
			super();
			
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mList = new ArrayList<FileListItem>();
			mMap = new HashMap<String, FileListItem>();
		}
		
		public synchronized void clear() {
			mList.clear();
			mMap.clear();
			
			notifyDataSetChanged();
		}
		
		public synchronized void add(int type, String path, String name, String item , String date) {
			FileListItem items = null;
			
			String key = String.format("%d%s%s", type, path, name);
			
			if ((items = mMap.get(key)) == null) {
				items = new FileListItem(type, path, name, item, date);
				mMap.put(key, items);
				mList.add(items);
			} else {

			}
			
			notifyDataSetChanged();
		}
		
		public synchronized int getType(int position) {
			return mList.get(position).getType();
		}
		
		public synchronized String getPath(int position) {
			return mList.get(position).getPath();
		}
		
		public synchronized String getName(int position) {
			return mList.get(position).getName();
		}
		
		@Override
		public boolean isEmpty() {
			return (mList.size() == 0);
		}
		
		@Override
		public int getCount() {
			int size = 0;
			synchronized(this) {
				size = mList.size();
			}
			return size;
		}
		
		@Override
		public Object getItem(int position) {
			Object item = null;
			synchronized(this) {
				item = mList.get(position);
			}
			return item;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FileListViewHolder holder = null;
			FileListItem item = null;
			
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.item_file_list, parent, false);
				holder = new FileListViewHolder(convertView);
			} else {
				holder = (FileListViewHolder) convertView.getTag();
			}
			
			synchronized(this) {
				item = mList.get(position);
			}
			holder.display(item);
			
			return convertView;
		}
		
		// ------------------------------------------------------------------------
		// Declare Class FileListViewHolder
		// ------------------------------------------------------------------------
		private class FileListViewHolder {
			private ImageView imgType;
			private TextView txtName;
			private TextView txtItem;
			private TextView txtDate;
			
			private FileListViewHolder(View parent) {
				
				imgType = (ImageView) parent.findViewById(R.id.type);
				txtName = (TextView) parent.findViewById(R.id.name);
				txtItem = (TextView) parent.findViewById(R.id.item);
				txtDate = (TextView) parent.findViewById(R.id.date);
				
				parent.setTag(this);
			}
			
			public void display(FileListItem item) {
				if ( null != item) {
					if (item.getType() == TYPE_DIRECTORY )
						imgType.setImageResource(R.drawable.ic_folder);
					else if (item.getType() == TYPE_FILE )
						imgType.setImageResource(R.drawable.ic_file);
					
//					try {
//						txtName.setText(URLEncoder.encode(item.getName(), "UTF-8"));
//					} catch (UnsupportedEncodingException e) {
//						ATLog.e(TAG, e, "ERROR. add() - failed to encode utf-8 for name");
//					}
					
					txtName.setText(item.getName());
					txtItem.setText(item.getItem());
					txtDate.setText(item.getDate());
					
				}
			}
		}
		
	}

	// ------------------------------------------------------------------------
	// Declare Class FileListItem
	// ------------------------------------------------------------------------

	private class FileListItem {
		private int mType;
		private String mPath;
		private String mName;
		private String mItem;
		private String mDate;
		
		private FileListItem(int type, String path, String name, String item , String date) {
			mType = type;
			mPath = path;
			mName = name;
			mItem = item;
			mDate = date;
		}
		
		public int getType() {
			return mType;
		}

		public String getPath() {
			return mPath;
		}
		
		public String getName() {
			return mName;
		}
		
		public String getItem() {
			return mItem;
		}
		
		public String getDate() {
			return mDate;
		}
	}


}
