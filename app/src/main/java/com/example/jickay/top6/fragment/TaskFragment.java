package com.example.jickay.top6.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jickay.top6.EditTask;
import com.example.jickay.top6.MainActivity;
import com.example.jickay.top6.R;
import com.example.jickay.top6.Task;
import com.example.jickay.top6.TaskRecyclerAdapter;

/**
 * Created by user on 7/2/2017.
 */

public class TaskFragment extends Fragment {

    RecyclerView recView;
    TaskRecyclerAdapter adapter;

    public TaskFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TaskRecyclerAdapter(getActivity(),MainActivity.getIncompleteTasks());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        recView = (RecyclerView) v.findViewById(R.id.recycler);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        recView.setAdapter(adapter);
        recView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(layoutManager);
    }
}
