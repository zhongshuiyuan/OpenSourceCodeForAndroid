package com.icodeman.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.icodeman.application.R;
import com.icodeman.application.adapter.LaunchProjectListAdapter;
import com.icodeman.baselib.activity.BaseActivity;
import com.icodeman.calendars.CalendarMainActivity;
import com.icodeman.qrcodeprojects.activity.BarCodeMainActivity;

import java.util.Arrays;
import java.util.List;

/**
 * @author lmw
 * @date 2019/3/5.
 */
public class LaunchActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private LaunchProjectListAdapter adapter;

    private List<String> projectList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListView = (ListView) getView(R.id.project_list);

        initProjectsList();
        initAdapter();
    }

    private void initProjectsList() {
        String[] projects = getResources().getStringArray(R.array.project_list);
        projectList = Arrays.asList(projects);
    }

    private void initAdapter() {
        adapter = new LaunchProjectListAdapter(this, projectList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    public String getCenterTitle() {
        return "ICM开源库";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            startActivity(new Intent(this, BarCodeMainActivity.class));
        } else if (position == 1) {
            startActivity(new Intent(this, CalendarMainActivity.class));
        }
    }
}
