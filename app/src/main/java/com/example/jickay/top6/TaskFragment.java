package com.example.jickay.top6;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 7/2/2017.
 */

public class TaskFragment extends Fragment {

    RecyclerView rv;
    TaskAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TaskAdapter(getActivity(),MainActivity.getIncompleteTasks());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.content_main, container, false);
        rv = (RecyclerView) v.findViewById(R.id.recycler);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }
}
