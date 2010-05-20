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
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 *
 * @author Brad Fitzpatrick, bradfitz@android.com
 */
public class JankableListAdapter implements ListAdapter
{
    private final String[] CODE_NAMES = new String[] {
        "1.0",
        "Petit Four",
        "Cupcake",
        "Donut",
        "Eclair",
        "Froyo",
        "Gingerbread",
        "Haggis",
        "Icelandic Icing",
        "Jalape\u00f1o",
        "Koala Krisps",
        "Liver",
        "Minced Meat",
        "Nuts",
        "Otter",
        "Penguin",
        "Quail",
        "Rabbit",
        "Salad",
        "Taco",
        "Umbilical Cord",
        "Vodka",
        "Wurst",
        "Xiaodianxin",
        "Yoghurt",
        "Zatar",
    };

    private final int mJankMillis;
    private final LayoutInflater mInflater;

    public JankableListAdapter(LayoutInflater inflater, int jankMillis) {
        super();
        mInflater = inflater;
        mJankMillis = jankMillis;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null
                ? convertView
                : mInflater.inflate(R.layout.list_item, null);
        TextView tv = (TextView) view.findViewById(R.id.textview_in_list_item);
        tv.setText(CODE_NAMES[position]);

        try {
            Thread.sleep(mJankMillis);
        } catch (InterruptedException e) {}
        return view;
    }

    public boolean areAllItemsEnabled() { return true; }
    public boolean isEnabled(int position) { return true; }
    public int getCount() { return CODE_NAMES.length; }
    public boolean isEmpty() { return false; }
    public Object getItem(int position) { return CODE_NAMES[position]; }
    public long getItemId(int position) { return position; }
    public int getItemViewType(int position) { return 42; }
    public boolean hasStableIds() { return true; }
    public void registerDataSetObserver(DataSetObserver observer) {}
    public void unregisterDataSetObserver(DataSetObserver observer) {}
    public int getViewTypeCount() { return 1; }
}
