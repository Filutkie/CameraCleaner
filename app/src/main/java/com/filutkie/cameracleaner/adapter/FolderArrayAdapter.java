package com.filutkie.cameracleaner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.model.Folder;
import com.filutkie.cameracleaner.utils.FileUtils;

import java.util.List;

public class FolderArrayAdapter extends ArrayAdapter<Folder> {

    private Context context;

    public FolderArrayAdapter(Context context, int resource, List<Folder> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_folder, null);
        }

        Folder folder = getItem(position);
        if (folder != null) {
            TextView nameTextView = (TextView) view
                    .findViewById(R.id.textview_folder_name);
            TextView sizeTextView = (TextView) view
                    .findViewById(R.id.textview_folder_size);
            nameTextView.setText(folder.getName());
            sizeTextView.setText(FileUtils.getHumanReadableByteCount(folder.getSize()));
        }

        return view;
    }
}
