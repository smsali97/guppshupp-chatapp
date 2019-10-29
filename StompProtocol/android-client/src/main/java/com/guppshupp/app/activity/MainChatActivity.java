package com.guppshupp.app.activity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.guppshupp.app.fragment.GroupChatFragment;
import com.guppshupp.app.fragment.SingleChatFragment;
import com.guppshupp.app.adapter.TabAdapter;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;



import ua.naiksoftware.R;
public class MainChatActivity extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.single_chat,
            R.drawable.group_chat
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new SingleChatFragment(), "One on One Chat");
        adapter.addFragment(new GroupChatFragment(),"Group Chat");

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);


        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);



    }
}