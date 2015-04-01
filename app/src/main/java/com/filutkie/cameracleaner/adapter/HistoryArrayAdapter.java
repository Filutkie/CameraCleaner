package com.filutkie.cameracleaner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.model.HistoryRecord;
import com.filutkie.cameracleaner.utils.FileUtils;

import java.util.List;

public class HistoryArrayAdapter extends ArrayAdapter<HistoryRecord> {

    private Context context;

    public HistoryArrayAdapter(Context context, int resource, List<HistoryRecord> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_history, null);
        }

        HistoryRecord historyRecord = getItem(position);
        if (historyRecord != null) {
            TextView dateTextView = (TextView) view.findViewById(R.id.textview_folder_name);
            TextView sizeTextView = (TextView) view.findViewById(R.id.textview_folder_size);
            dateTextView.setText(historyRecord.getDate() + "");
            sizeTextView.setText(FileUtils.getHumanReadableByteCount(historyRecord.getSize()));
        }

        return view;
    }
}
