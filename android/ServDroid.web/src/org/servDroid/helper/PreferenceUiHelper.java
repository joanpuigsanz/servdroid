/*
 * Copyright (C) 2013 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.servDroid.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import org.servDroid.ui.util.DialogFactory;
import org.servDroid.util.FilesChecker;
import org.servDroid.util.Logger;
import org.servDroid.util.ZipUtils;
import org.servDroid.util.shell.ShellCommands;
import org.servDroid.web.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceGroup;
import android.widget.Toast;

public class PreferenceUiHelper {

	private EditTextPreference mPreferencePort;
	private EditTextPreference mPreferenceMaxClients;
	private EditTextPreference mPreferenceWwwPath;
	private EditTextPreference mPreferenceErrorPath;
	private EditTextPreference mPreferenceLogPath;
	private EditTextPreference mPreferenceExpirationCache;
	private Preference mPreferenceResetPref;
	private Preference mPreferenceAbout;
	private Preference mPreferenceFileIndexingGetTemplate;
	private Preference mPreferenceReleaseNotes;

	private PreferenceGroup mPrefGroup;
	private Context mContext;
	private Activity mActivity;
	private IPreferenceHelper mPreferenceHelper;
	private String mVersion;

	private ProgressDialog mProgressDialog;

	private Runnable mInvalidateUi;

	private IStoreHelper mStoreHelper;

	public PreferenceUiHelper(PreferenceGroup prefGroup, Activity activity,
			IPreferenceHelper preferenceHelper, String version, Runnable invalidateUi,
			IStoreHelper storeHelper) {

		mPrefGroup = prefGroup;
		mContext = activity;
		mActivity = activity;
		mVersion = version;
		mPreferenceHelper = preferenceHelper;
		mInvalidateUi = invalidateUi;
		mStoreHelper = storeHelper;
		initializeFields();
		initializeListeners();
	}

	private Preference getPreference(int resStringKey) {
		return getPreference(mContext.getResources().getString(resStringKey));
	}

	private Preference getPreference(String prefKey) {
		return mPrefGroup.findPreference(prefKey);
	}

	private void initializeFields() {
		mPreferencePort = (EditTextPreference) getPreference(R.string.pref_port_key);

		mPreferenceMaxClients = (EditTextPreference) getPreference(R.string.pref_max_clients_key);
		mPreferenceExpirationCache = (EditTextPreference) getPreference(R.string.pref_expiration_cache_key);
		mPreferenceWwwPath = (EditTextPreference) getPreference(R.string.pref_www_path_key);
		mPreferenceErrorPath = (EditTextPreference) getPreference(R.string.pref_error_path_key);
		mPreferenceLogPath = (EditTextPreference) getPreference(R.string.pref_log_path_key);
		mPreferenceResetPref = (Preference) getPreference(R.string.pref_reset_config_key);
		mPreferenceAbout = (Preference) getPreference(R.string.pref_about_key);
		mPreferenceFileIndexingGetTemplate = (Preference) getPreference(R.string.pref_directory_indexing_get_template_key);
		mPreferenceReleaseNotes = (Preference) getPreference(R.string.pref_release_notes_key);

	}

	private void initializeListeners() {

		mPreferenceWwwPath.setText(mPreferenceHelper.getWwwPath());
		mPreferenceErrorPath.setText(mPreferenceHelper.getErrorPath());
		mPreferenceLogPath.setText(mPreferenceHelper.getLogPath());

		// Check the port
		mPreferencePort.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				try {
					int port = Integer.parseInt((String) newValue);
					// If you are not root, you only can until 1024
					if (port >= 65535 || port < 1) {
						return false;
					}
					// check if it is rooted
					if (port > 1 && port < 1024) {
						if (ShellCommands.isIptablesExist()) {
							if (!ShellCommands.isDeviceRooted()) {
								Toast.makeText(mContext, R.string.no_su_permissions,
										Toast.LENGTH_LONG).show();
								return false;
							}
						} else {
							Toast.makeText(mContext, R.string.no_iptables, Toast.LENGTH_LONG)
									.show();
							return false;
						}
					}

				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}
		});

		mPreferenceMaxClients.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object newValue) {
				try {
					Integer.parseInt((String) newValue);
				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}
		});

		mPreferenceExpirationCache.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object newValue) {
				try {
					Integer.parseInt((String) newValue);
				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}
		});

		mPreferenceResetPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				showResetDialog();

				return true;
			}
		});

		mPreferenceAbout.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				DialogFactory.showAboutDialog(mActivity, mVersion, mStoreHelper);
				return true;
			}
		});

		mPreferenceFileIndexingGetTemplate
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						showDownloadTemplateDialog();
						return true;
					}
				});

		mPreferenceReleaseNotes.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				showReleaseNotesDialog();

				return true;
			}
		});

	}

	private void showResetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(R.string.reset_configurations_message).setCancelable(false)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						mPreferenceHelper.restorePreferences();
						FilesChecker.checkErrorPath(mPreferenceHelper.getErrorPath(), mContext);
						FilesChecker.checkLogPath(mPreferenceHelper.getLogPath(), mContext);
						FilesChecker.checkWwwPath(mPreferenceHelper.getWwwPath(), mContext);
						if (mInvalidateUi != null) {
							mInvalidateUi.run();
						}

					}
				}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).setTitle(R.string.other_reset).setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(true);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showReleaseNotesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(R.string.release_notes_info).setCancelable(true)
				.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.create();
		builder.setTitle(R.string.release_notes);
		builder.show();
	}

	private void showDownloadTemplateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(R.string.directory_indexing_question)
				.setTitle(R.string.directory_indexing).setIcon(R.drawable.icon)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						if (mProgressDialog != null) {
							mProgressDialog.dismiss();
						}

						mProgressDialog = new ProgressDialog(mContext);
						mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						mProgressDialog.setTitle(R.string.installing_template);
						mProgressDialog.setCancelable(true);
						mProgressDialog.setMessage(mContext.getResources().getString(
								R.string.connecting));
						mProgressDialog.show();

						String url = mContext.getString(R.string.url_download_template);
						ProgressThreadTask progressThread = new ProgressThreadTask();
						progressThread.execute(url, mPreferenceHelper.getWwwPath());
					}
				}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	private void showErrorDownloadMessage() {
		AlertDialog.Builder ab = new AlertDialog.Builder(mContext);

		String errorMesage = mContext.getResources().getString(
				R.string.error_finish_downloading_extracting,
				mContext.getResources().getString(R.string.url_project_page));
		ab.setMessage(errorMesage).setTitle(R.string.error);
		ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				openWebBrowser();
			}
		}).setIcon(R.drawable.icon)
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		ab.show();
	}

	private void openWebBrowser() {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri.parse(mContext.getString(R.string.url_download_list));
		i.setData(u);
		mContext.startActivity(i);
	}

	private class ProgressThreadTask extends AsyncTask<String, Integer, Void> {

		private static final int ERROR_DOWNLOADING = -100;
		private static final int DOWNLOADING_FINISHED = -1;
		private static final int TASK_FINISHED = -2;

		@Override
		protected Void doInBackground(String... params) {
			String mUrlFile = params[0];
			String mPath = params[1];

			try {
				URL url = new URL(mUrlFile);
				URLConnection urlC = url.openConnection();

				int contentLenght = urlC.getContentLength();

				publishProgress(contentLenght, 0);

				// Copy resource to local file, use remote file
				// if no local file name specified
				InputStream is = url.openStream();

				System.out.flush();
				FileOutputStream fos = null;

				String localFile = null;
				// Get only file name
				StringTokenizer st = new StringTokenizer(url.getFile(), "/");
				while (st.hasMoreTokens())
					localFile = st.nextToken();

				// Check if the file exist
				File folder = new File(mPath);
				if (!folder.exists() | (folder.exists() & folder.isDirectory())) {
					folder.mkdir();
				}
				File file = new File(mPath + "/" + localFile);
				if (file.exists()) {
					file.delete();
				}
				fos = new FileOutputStream(file);

				publishProgress(contentLenght, 1);

				int oneChar, count = 0;
				while ((oneChar = is.read()) != -1 && mProgressDialog.isShowing()) {
					fos.write(oneChar);
					count++;

					if (count % 550 == 0) {
						publishProgress(contentLenght, count);
					}
				}
				is.close();
				fos.close();
			} catch (MalformedURLException e) {
				Logger.e(e.getMessage());
				publishProgress(0, ERROR_DOWNLOADING);
				return null;
			} catch (IOException e) {
				Logger.e(e.getMessage());
				publishProgress(0, ERROR_DOWNLOADING);
				return null;
			}

			publishProgress(0, DOWNLOADING_FINISHED);

			ZipUtils unzip = new ZipUtils();
			if (!unzip.unzipArchive(new File(mPreferenceHelper.getWwwPath()
					+ "/servdroid-file-indexing-template.zip"),
					new File(mPreferenceHelper.getWwwPath()))) {
				publishProgress(0, ERROR_DOWNLOADING);
				return null;
			}

			publishProgress(0, TASK_FINISHED);
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

			int max = values[0];
			int progress = values[1];

			mProgressDialog.setMax(max);

			if (progress >= 0) {
				mProgressDialog.setMessage(mContext.getResources().getString(R.string.downloading));
				mProgressDialog.setProgress(progress);
			} else if (progress == DOWNLOADING_FINISHED) {
				mProgressDialog.setMessage(mContext.getResources().getString(R.string.extracting));
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			} else if (progress == TASK_FINISHED) {
				mProgressDialog.dismiss();
				Toast.makeText(mContext, R.string.finish_downloading_extracting, Toast.LENGTH_LONG)
						.show();
			} else if (progress == ERROR_DOWNLOADING) {
				mProgressDialog.dismiss();
				showErrorDownloadMessage();
			}
		}
	}
}
