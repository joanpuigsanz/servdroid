package org.servDroid.ui.util;

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IStoreHelper;
import org.servDroid.web.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class DialogFactory {

	public static void showHelpDialog(Activity activity, IPreferenceHelper preferenceHelper,
			final IStoreHelper storeHelper) {
		showHelpDialog(activity, preferenceHelper, storeHelper, activity);
	}

	public static void showHelpDialog(final Context context, IPreferenceHelper preferenceHelper,
			final IStoreHelper storeHelper, final Activity activity) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(
				context.getResources().getString(R.string.info_dialog_1)
						+ " \""
						+ preferenceHelper.getWwwPath()
						+ "\" "
						+ context.getResources().getString(R.string.info_dialog_2)
						+ "\n\n"
						+ context.getResources().getString(R.string.info_dialog_3)
						+ "\n\n"
						+ context.getResources().getString(R.string.info_dialog_4)
						+ "\n\n"
						+ context.getResources().getString(R.string.info_dialog_5)
						+ "\n\n"
						+ context.getResources().getString(R.string.info_dialog_6)
						+ "\n\n"
						+ context
								.getResources()
								.getString(R.string.info_dialog_7)
								.replace("[[param]]",
										context.getResources().getString(R.string.url_project_page)))
				.setCancelable(true)
				.setPositiveButton(R.string.web_page, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						openWebBrowser(context, context.getString(R.string.url_project_page));
					}
				}).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		if (storeHelper == null || storeHelper.hasStoreInfo()) {
			builder.setNegativeButton(R.string.donate, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					ShowDonateDialog(activity, storeHelper);
				}
			});
		}

		builder.setIcon(R.drawable.icon);
		builder.create();
		builder.setTitle(R.string.information);
		builder.show();
	}

	private static void openWebBrowser(Context context, String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri.parse(url);
		i.setData(u);
		context.startActivity(i);
	}

	public static void showAboutDialog(final Activity activity, String version,
			final IStoreHelper storeHelper) {

		String authors = activity.getResources().getString(R.string.about_servdroid_web);
		authors = authors.replace("[[param]]",
				activity.getResources().getString(R.string.url_project_page));

		AlertDialog.Builder ab = new AlertDialog.Builder(activity);
		String message = activity.getResources().getString(R.string.app_name) + " v" + version
				+ "\n\n" + authors;
		ab.setMessage(message).setTitle(R.string.other_about);
		if (storeHelper != null && storeHelper.hasStoreInfo()) {
			ab.setPositiveButton(R.string.donate, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					ShowDonateDialog(activity, storeHelper);
				}
			});
		}
		ab.setNegativeButton(android.R.string.ok, null).setIcon(R.drawable.icon);

		ab.show();
	}

	public static void ShowDonateDialog(final Activity activity, final IStoreHelper storeHelper) {
		if (storeHelper == null || !storeHelper.hasStoreInfo()) {
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.donate_info).setCancelable(true)
				.setPositiveButton(R.string.donate, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						storeHelper.doDonationPurchase(activity);
					}
				});
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.create();
		builder.setTitle(R.string.donate);
		builder.show();
	}
}
