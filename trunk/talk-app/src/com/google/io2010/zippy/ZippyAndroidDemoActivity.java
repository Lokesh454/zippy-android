//; -*- mode: Java; c-basic-offset: 4;  -*-

/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.google.io2010.zippy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * This is the sample code to accompany the Google I/O 2010 talk
 * on "Writing Zippy Android Apps".  It mostly shows what _not_ to do,
 * so I wouldn't use this code for anything.... :)
 *
 * @author Brad Fitzpatrick, bradfitz@android.com
 */
public class ZippyAndroidDemoActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);

        Button janky = (Button) findViewById(R.id.janky_button);
        janky.setOnClickListener(onClickDelay(600));

        Button anr1 = (Button) findViewById(R.id.anr1);
        anr1.setOnClickListener(onClickDelay(6000));

        final Button asyncTask = (Button) findViewById(R.id.asynctask);
        asyncTask.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    asyncTask.setEnabled(false);
                    ZippyAndroidDemoActivity.this.setProgressBarIndeterminateVisibility(true);

                    new AsyncTask<Void, Void, Void>() {
                        @Override protected Void doInBackground(Void... unused) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {}
                            return null;
                        }

                        @Override protected void onPostExecute(Void result) {
                            ZippyAndroidDemoActivity.this.setProgressBarIndeterminateVisibility(false);
                            asyncTask.setEnabled(true);
                        }
                    }.execute();
                }
            });

        final Button smoothListView = (Button) findViewById(R.id.start_smoothlistview_button);
        smoothListView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), JankyListViewActivity.class);
                    intent.putExtra("latency", 0);
                    startActivity(intent);
                }
            });

        final Button jankyListView = (Button) findViewById(R.id.start_jankylistview_button);
        jankyListView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), JankyListViewActivity.class);
                    intent.putExtra("latency", 100);
                    startActivity(intent);
                }
            });


        final Button lockFightButton = (Button) findViewById(R.id.start_lock_fight_button);
        lockFightButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    lockFightButton.setEnabled(false);
		    final ProgressDialog progress =
			ProgressDialog.show(
			    ZippyAndroidDemoActivity.this,
			    "Please wait",
			    "Mutexes are fighting...",
			    false /* indeterminate */,
			    false /* cancelable */);

                    new AsyncTask<Void, Void, Void>() {
                        @Override protected Void doInBackground(Void... unused) {
                            final Object someLock = new Object();
                            Thread t1 = new LockFighterThread(someLock);
                            Thread t2 = new LockFighterThread(someLock);
                            t1.start();
                            t2.start();
                            try {
                                t1.join();
                                t2.join();
                            } catch (InterruptedException e) {}
                            return null;  // Void
                        }

                        @Override protected void onPostExecute(Void result) {
                            ZippyAndroidDemoActivity.this.setProgressBarIndeterminateVisibility(false);
			    progress.dismiss();
			    lockFightButton.setEnabled(true);
                        }
                    }.execute();
                }
            });
    }

    private View.OnClickListener onClickDelay(final int millis) {
            return new View.OnClickListener() {
                public void onClick(View view) {
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {}
                }
            };
    }

    private static class LockFighterThread extends Thread {
        private final Object mLock;

        public LockFighterThread(Object lockToFightOver) {
            mLock = lockToFightOver;
        }
        
        @Override public void run() {
            for (int i = 0; i < 10; ++i) {
                synchronized (mLock) {
                    randomSleep(500);
                }
                randomSleep(100);
            }
        }

        private void randomSleep(int max) {
            try {
                Thread.sleep((int) (Math.random() * max));
            } catch (InterruptedException e) {}
        }
    }
}
