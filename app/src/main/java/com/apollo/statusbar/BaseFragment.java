package com.apollo.statusbar;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {
    private int id = -1;

    public static BaseFragment getInstance(int id) {
        BaseFragment baseFragment = new BaseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        baseFragment.setArguments(bundle);
        return baseFragment;
    }

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        id = arguments.getInt("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = new View(inflater.getContext());
        switch (id) {
            case 0:
                view = inflater.inflate(R.layout.fragment_home, container, false);
                initToolBar(view);
                initRecyclerView(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.fragment_home1, container, false);
                initToolBar(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_home2, container, false);
                break;
        }
        return view;
    }

    private void initToolBar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("消息");
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i + "");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new MyRecyclerViewAdapter(getActivity(), list));
    }

}
