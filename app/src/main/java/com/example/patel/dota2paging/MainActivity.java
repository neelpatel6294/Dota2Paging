package com.example.patel.dota2paging;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.patel.dota2paging.Adapter.CustomAdapter;
import com.example.patel.dota2paging.Model.Heroes;
import com.example.patel.dota2paging.ViewModel.HeroViewModel;

public class MainActivity extends AppCompatActivity {



    private HeroViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.customRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        mViewModel = ViewModelProviders.of(this).get(HeroViewModel.class);

        final CustomAdapter mAdapter = new CustomAdapter();

        mViewModel.mData.observe(this,pagedList -> {
            mAdapter.submitList((PagedList<Heroes>) pagedList);
        });

        mViewModel.networkState.observe(this, mAdapter::setNetworkState);
        recyclerView.setAdapter(mAdapter);
    }
}
