/**
 * Sparse rss
 *
 * Copyright (c) 2010-2012 Stefan Handschuh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package org.tcai.rss.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import org.tcai.rss.R;
import org.tcai.rss.service.RefreshService;
import org.tcai.rss.utils.Strings;

public class ApplicationPreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

			setTheme(R.style.Theme_Light);

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		Preference preference = findPreference(Strings.SETTINGS_REFRESHENABLED);

		preference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (Boolean.TRUE.equals(newValue)) {
							new Thread() {
								@Override
								public void run() {
									startService(new Intent(
											ApplicationPreferencesActivity.this,
											RefreshService.class));
								}
							}.start();
						} else {
							getPreferences(MODE_PRIVATE)
									.edit()
									.putLong(
											Strings.PREFERENCE_LASTSCHEDULEDREFRESH,
											0).commit();
							stopService(new Intent(
									ApplicationPreferencesActivity.this,
									RefreshService.class));
						}
						return true;
					}
				});

		preference = findPreference(Strings.SETTINGS_EFFICIENTFEEDPARSING);
		preference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(
							final Preference preference, Object newValue) {
						if (newValue.equals(Boolean.FALSE)) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									ApplicationPreferencesActivity.this);

							builder.setIcon(android.R.drawable.ic_dialog_alert);
							builder.setTitle(android.R.string.dialog_alert_title);
							builder.setPositiveButton(android.R.string.ok,
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Editor editor = PreferenceManager
													.getDefaultSharedPreferences(
															ApplicationPreferencesActivity.this)
													.edit();

											editor.putBoolean(
													Strings.SETTINGS_EFFICIENTFEEDPARSING,
													Boolean.FALSE);
											editor.commit();
											((CheckBoxPreference) preference)
													.setChecked(false);
											dialog.dismiss();
										}
									});
							builder.setNegativeButton(android.R.string.cancel,
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
							builder.setMessage(R.string.warning_moretraffic);
							builder.show();
							return false;
						} else {
							return true;
						}
					}
				});
	}

}
