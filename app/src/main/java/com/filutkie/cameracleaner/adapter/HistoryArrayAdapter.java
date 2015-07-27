package com.filutkie.cameracleaner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filutkie.cameracleaner.R;
import com.filutkie.cameracleaner.model.HistoryRecord;
import com.filutkie.cameracleaner.utils.FileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            TextView dateTextView = (TextView) view.findViewById(R.id.textview_history_date);
            TextView sizeTextView = (TextView) view.findViewById(R.id.textview_history_size);

            long milliseconds = historyRecord.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, HH:mm", Locale.ENGLISH);
            Date resultdate = new Date(milliseconds);

            dateTextView.setText(sdf.format(resultdate));
            sizeTextView.setText(FileUtils.getHumanReadableByteCount(historyRecord.getSize()));

            if (position == 0) {
                ImageView icon = (ImageView) view.findViewById(R.id.imageview_history);
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_history_grey600_24dp));
            }
        }

        return view;
    }
}
