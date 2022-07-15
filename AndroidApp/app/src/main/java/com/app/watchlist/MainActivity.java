package com.app.watchlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.app.watchlist.Configuration.SharedPreferences;
import com.app.watchlist.Fragments.AddWatchlist;
import com.app.watchlist.Fragments.Watchlist;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Various Layouts and Views
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FloatingActionButton floatingAddWatchlist;
    FrameLayout frameLayout;
    LinearLayout tabStrip,linearLayout;

    //To Save data locally using shared preferences
    SharedPreferences sharedPreferences;

    //ArrayList to parse data for tabs from shared preferences
    ArrayList<String> tabData = new ArrayList<>();
    ArrayList<String> watchlist = new ArrayList<>();
    ArrayList<String> symbols = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Function to initialise all the necessary components for the activity
        viewInitialization();
        //Function to get data from shared preferences
        getData();
        //Function to set data on to the screen
        setData();
        //Function to handle all button and tab clicks
        onClicks();

    }

    public void viewInitialization(){
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager2) findViewById(R.id.viewPager);
        floatingAddWatchlist = (FloatingActionButton) findViewById(R.id.floatingAddWatchlist);
        sharedPreferences = new SharedPreferences(getApplication());
        frameLayout = (FrameLayout) findViewById(R.id.frame_container);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        tabStrip = (LinearLayout) tabLayout.getChildAt(0);
    }

    public void getData(){
        //Getting the locally saved watchlist data
        watchlist = sharedPreferences.getItems("watchlist");
        //If no data is present by default adding the first watch list along with few symbols
        if(watchlist==null){
            watchlist = new ArrayList<>();
            watchlist.add("My First List");
            sharedPreferences.saveItems("watchlist",watchlist);

            symbols.add("AAPL");
            symbols.add("MSFT");
            symbols.add("GOOG");
            sharedPreferences.saveItems("My First List",symbols);
        }
    }

    public void setData(){
        //Settings tabs with watchlist names from shared preferences
        tabData.addAll(watchlist);

        //Setting up the view pager along with the tab names from the shared preferences
        setupViewPager(viewPager,tabData);

        //Setting the tab with exact names from shared preferences
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabData.get(position));
                    }
                }).attach();
    }

    public void onClicks(){
        //Add watchlist button on click function to navigate to add watch list page
        floatingAddWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.VISIBLE);
                floatingAddWatchlist.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                Fragment fragment = new AddWatchlist();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_container, fragment).commit();
            }
        });

        //Setting up listener for each tab to edit the watch list item along with the watchlist name
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            // Set LongClick listener to each Tab
            int finalI = i;
            tabStrip.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    frameLayout.setVisibility(View.VISIBLE);
                    floatingAddWatchlist.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    Fragment fragment = new AddWatchlist();
                    Bundle bundle = new Bundle();
                    bundle.putString("watchlist",watchlist.get(finalI));
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commit();

                    return true;
                }
            });
        }

    }

    private void setupViewPager(ViewPager2 viewPager,ArrayList<String> category) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        for(int i=0;i<category.size();i++)
             adapter.addFragment(category.get(i));
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment=null;
            Bundle bundle = new Bundle();
            fragment=new Watchlist();
            bundle.putString("watchlistName",  mFragmentTitleList.get(position));
            fragment.setArguments(bundle);
            return  fragment;
        }

        @Override
        public int getItemCount() {
            return mFragmentTitleList.size();
        }

        public void addFragment(String title) {
            mFragmentTitleList.add(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        frameLayout.setVisibility(View.GONE);
    }
}