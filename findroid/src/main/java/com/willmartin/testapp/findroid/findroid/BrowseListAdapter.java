package com.willmartin.testapp.findroid.findroid;

/**
 * Created by Noah on 4/4/14.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jcraft.jsch.ChannelSftp;

import java.util.List;


public class BrowseListAdapter extends ArrayAdapter<ChannelSftp.LsEntry> {

    public BrowseListAdapter(Context context, int resource, List<ChannelSftp.LsEntry> listItems) {
        super(context, resource, listItems);
    }

    public View getView(int position, View v, ViewGroup parent) {

        // Consider reusing view items the way Jeff does.

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ChannelSftp.LsEntry listItem = this.getItem(position);
        if (listItem.getAttrs().isDir()){
            v = inflater.inflate(R.layout.dir_list_item, null);
        }
        else {
            v = inflater.inflate(R.layout.file_list_item, null);
        }

        TextView textView = (TextView) v.findViewById(R.id.filenameTextView);
        textView.setText(this.getItem(position).getFilename());

        return v;
    }
}