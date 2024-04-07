package com.example.ticketonlineapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.ticketonlineapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class RoomAdapter extends BaseAdapter {
    private List<String> listRoom;
    private Context context;
    //private TextInputEditText textInputEditText;
    static public ViewHolder prevView;


    public RoomAdapter(Context context, List<String> listRoom) {
        this.context = context;
        this.listRoom = listRoom;
        //this.textInputEditText = textInputEditText;

    }

    @Override
    public int getCount() {
        return listRoom.size();
    }

    @Override
    public Object getItem(int position) {
        return listRoom.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_room_item, null);
            holder = new ViewHolder();
            holder.roomBtn = (Button) convertView.findViewById(R.id.roomBtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String price = this.listRoom.get(position);
        holder.roomBtn.setText(price);
        holder.roomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (prevView == null) {
                    holder.roomBtn.setSelected(true);
                    //textInputEditText.setText(holder.roomBtn.getText());
                } else {

                    if (prevView.roomBtn.getText() != holder.roomBtn.getText()) {
                        holder.roomBtn.setSelected(true);
                        //textInputEditText.setText(holder.roomBtn.getText());
                        prevView.roomBtn.setSelected(false);
                    } else {
                        if (holder.roomBtn.isSelected()) {
                            holder.roomBtn.setSelected(false);
                            //textInputEditText.setText("");

                        } else {
                            //textInputEditText.setText(holder.roomBtn.getText());
                            holder.roomBtn.setSelected(true);
                        }

                    }
                }
                prevView = holder;
            }
        });

        return convertView;
    }

    // Find Image ID corresponding to the name of the image (in the directory mipmap).


    static class ViewHolder {

        Button roomBtn;

    }
}