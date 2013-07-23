package org.servDroid.ui.fragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.servDroid.db.LogAdapter;
import org.servDroid.db.LogMessage;
import org.servDroid.db.ServdroidDbAdapter;
import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IServiceHelper;
import org.servDroid.ui.adapter.LogListAdapter;
import org.servDroid.ui.option.IMainOption;
import org.servDroid.ui.option.IMainOptionsList;
import org.servDroid.util.Logger;
import org.servDroid.web.R;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;

public class LogFragment extends RoboSherlockFragment {

	public static final int MENU_ID_LOG = 3554;

	private static final int REFRESH_TIME = 5000;

	@InjectView(R.id.listViewLogFragment)
	private ListView mListView;

	private LogListAdapter mLogAdapter;

	@Inject
	private LogAdapter mLogHelper;

	@Inject
	private Context mContext;

	@Inject
	protected IServiceHelper serviceHelper;

	@Inject
	private IMainOptionsList mOptions;

	@Inject
	private IPreferenceHelper mPreferenceHelper;

	private ProgressDialog mProgressDialog;

	private RefreshLogThread mRefreshThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLogAdapter = new LogListAdapter(mContext, R.layout.row_log);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.log_fragment, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView.setAdapter(mLogAdapter);
	}

	public void fillLogList() {
		int entriesToshow = mPreferenceHelper.getMaxLogEntries();
		List<LogMessage> locals = mLogHelper.fetchLogList(entriesToshow);

		mLogAdapter.clear();
		int size = locals.size();
		if (locals != null && size > 0) {
			for (int i = 0; i < size; i++)
				mLogAdapter.add(locals.get(i));
		}
//		Cursor c = mLogHelper.fetchAllLog();
//		int counter = c.getCount();
//		c.close();

		mLogAdapter.setItems(locals);
		mLogAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		fillLogList();
		startRefreshThread();
	}

	@Override
	public void onPause() {
		mRefreshThread.finishThread();
		super.onPause();
	}

	private void startRefreshThread() {
		if (mRefreshThread != null && mRefreshThread.isAlive()) {
			return;
		}
		mRefreshThread = new RefreshLogThread();
		mRefreshThread.start();
	}

	private class RefreshLogThread extends Thread {

		private boolean stop;

		RefreshLogThread() {
			stop = false;
		}

		public void finishThread() {
			stop = true;
		}

		@Override
		public void run() {
			while (!stop) {
				try {
					Activity activity = LogFragment.this.getActivity();
					if (activity == null) {
						return;
					}
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							fillLogList();
						}
					});
					Thread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int addLogMenu(Menu menu) {
		if (menu == null) {
			return -1;
		}
		SubMenu subMenuOptions = menu.addSubMenu(Menu.NONE, MENU_ID_LOG, Menu.NONE,
				R.string.main_menu_options);

		for (int i = 0; i < mOptions.getLogOptions().size(); i++) {
			IMainOption option = mOptions.getLogOptions().get(i);
			subMenuOptions.add(0, option.getId(), 1, option.getName());
		}

		MenuItem MenuOptions = subMenuOptions.getItem();
		MenuOptions.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
		MenuOptions.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return MENU_ID_LOG;

	}

	public void deleteLog() {
		mLogHelper.deleteTableLog();
		fillLogList();
	}

	public void saveLog() {
		Cursor c = mLogHelper.fetchAllLog();
		int counter = c.getCount();
		if (counter == 0) {
			Toast.makeText(mContext, this.getResources().getString(R.string.no_log_entries),
					Toast.LENGTH_LONG).show();
			return;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_Hmmss", Locale.getDefault());
		String dateChain = dateFormat.format(new Date((new java.util.Date().getTime())));

		String fileName = "/web_" + dateChain + ".log";
		String path = mPreferenceHelper.getLogPath();

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle(this.getResources().getString(R.string.saving_log_progress));
		mProgressDialog.setMax(counter - 1);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();

		SaveLogAsyncTask progressThread = new SaveLogAsyncTask();
		progressThread.execute(path, fileName);

	}

	private class SaveLogAsyncTask extends AsyncTask<String, Integer, Object> {

		private String path;
		private String fileName;

		@Override
		protected Object doInBackground(String... params) {

			path = params[0];
			fileName = params[1];

			Cursor c = mLogHelper.fetchAllLog();
			int counts = c.getCount();

			c.moveToFirst();
			int indexIp = c.getColumnIndex(ServdroidDbAdapter.KEY_HOSTS);
			int indexPath = c.getColumnIndex(ServdroidDbAdapter.KEY_PATH);
			int indexTimeStamp = c.getColumnIndex(ServdroidDbAdapter.KEY_TIME);
			int indexInfoBegining = c.getColumnIndex(ServdroidDbAdapter.KEY_INFOBEGINING);
			int indexInfoEnd = c.getColumnIndex(ServdroidDbAdapter.KEY_INFOEND);

			File folder = new File(path);
			if (!folder.exists() || (folder.exists() && !folder.isDirectory())) {
				folder.mkdir();
			}

			FileWriter fw;
			try {
				fw = new FileWriter(path + fileName);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw, false);
				for (int i = 0; i < counts; i++) {

					LogMessage tmp = new LogMessage(c.getString(indexIp), c.getString(indexPath),
							c.getString(indexInfoBegining), c.getString(indexInfoEnd),
							c.getLong(indexTimeStamp));

					pw.println(tmp.toString());
					c.moveToNext();

					publishProgress(i, counts);

					if (!mProgressDialog.isShowing()) {
						pw.close();
						return null;
					}
				}
				pw.close();
				publishProgress(counts, counts);
			} catch (IOException e) {
				Logger.e("Error saving log", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			Toast.makeText(
					mContext,
					mContext.getResources().getString(R.string.log_saved) + " "
							+ (path + fileName).replace("//", "/"), Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progress = values[0];
			int total = values[1];
			mProgressDialog.setProgress(total);
			mProgressDialog.setProgress(progress);
			super.onProgressUpdate(values);
		}

	}

}
