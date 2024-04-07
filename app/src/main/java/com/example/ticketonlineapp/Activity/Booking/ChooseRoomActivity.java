package com.example.ticketonlineapp.Activity.Booking;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketonlineapp.Adapter.RoomAdapter;
import com.example.ticketonlineapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseRoomActivity extends AppCompatActivity {

    private ListView roomLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_room);

        roomLv = (ListView) findViewById(R.id.roomLv);

        List<String> list = new ArrayList<>();
        list.add("Room 1");
        list.add("Room 2");
        list.add("Room 3");
        list.add("Room 4");
        list.add("Room 5");

        RoomAdapter roomAdapter = new RoomAdapter(ChooseRoomActivity.this, list);
        roomLv.setAdapter(roomAdapter);
    }

}
