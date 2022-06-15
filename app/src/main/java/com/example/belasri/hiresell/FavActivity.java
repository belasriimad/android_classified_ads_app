package com.example.belasri.hiresell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import helpers.FavListViewAdapter;
import models.AdInfo;
import models.DataBaseHandler;

import static com.example.belasri.hiresell.R.id.recyclerView;

public class FavActivity extends AppCompatActivity {
    private DataBaseHandler dba;
    private ArrayList<AdInfo> dbaAds = new ArrayList<>();
    private FavListViewAdapter adsAdapter;
    private RecyclerView liste;
    private Toolbar toolbar;
    private RecyclerViewItemClickSupport recyclerViewItemClickSupport;
    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        liste = (RecyclerView) findViewById(R.id.liste);
        liste.setLayoutManager(new GridLayoutManager(this,2));
        liste.setItemAnimator(new DefaultItemAnimator());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        relativeLayout = (RelativeLayout) findViewById(R.id.emptyFav);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void getData(){
        dbaAds.clear();
        dba = new DataBaseHandler(getApplicationContext());
        ArrayList<AdInfo> favsFromDb = dba.getFavs();
        for(int i = 0; i < favsFromDb.size() ; i++){
            String title = favsFromDb.get(i).getTitle();
            String city = favsFromDb.get(i).getCity();
            String image  = favsFromDb.get(i).getImageUrl();
            String ad_id = favsFromDb.get(i).getAdId();
            AdInfo adInfo = new AdInfo();
            adInfo.setTitle(title);
            adInfo.setCity(city);
            adInfo.setImageUrl(image);
            adInfo.setAdId(ad_id);
            dbaAds.add(adInfo);
        }
        dba.close();
        if(dbaAds.isEmpty()){
            liste.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }else {
            liste.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }
        //set the adapter
        adsAdapter = new FavListViewAdapter(FavActivity.this,dbaAds);
        liste.setAdapter(adsAdapter);
        adsAdapter.notifyDataSetChanged();
        recyclerViewItemClickSupport.addTo(liste).setOnItemClickListener(new RecyclerViewItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(FavActivity.this,AdDetails.class);
                intent.putExtra("ad_id",adsAdapter.getAd(position).getAdId());
                startActivity(intent);
            }
        });
        recyclerViewItemClickSupport.addTo(liste).setOnItemLongClickListener(new RecyclerViewItemClickSupport.OnItemLongClickListener() {
            @Override
            public void onItemLongClicked(RecyclerView recyclerView, int position, View v) {

            }
        });
    }
}
