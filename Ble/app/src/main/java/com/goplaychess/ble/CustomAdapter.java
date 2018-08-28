package com.goplaychess.ble;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jonc on 10/30/2017.
 */

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mBluetoothDevices;
    private List<Integer> mImageIDs;

    public CustomAdapter(Context context, List<String> devices, List<Integer> imageIDs) {
        mContext = context;
        mBluetoothDevices = devices;
        mImageIDs = imageIDs;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return mBluetoothDevices.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void updateListView(String bluetoothDevices, Integer imageID)
    {
        if(!mBluetoothDevices.contains(bluetoothDevices)) {
            mBluetoothDevices.add(bluetoothDevices);
            mImageIDs.add(imageID);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = null;

        if(convertView == null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.row, parent, false);

        }else{
            row = convertView;
        }

        ImageView i1 = (ImageView) row.findViewById(R.id.imgIcon);
        TextView title = (TextView) row.findViewById(R.id.txtTitle);

        title.setText(mBluetoothDevices.get(position));
        i1.setImageResource(mImageIDs.get(position));

        return row;
    }
}
