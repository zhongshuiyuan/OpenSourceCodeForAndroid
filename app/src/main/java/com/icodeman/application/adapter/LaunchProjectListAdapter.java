package com.icodeman.application.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.icodeman.application.R;
import com.icodeman.baselib.adapter.BaseHolder;
import com.icodeman.baselib.adapter.BaseListAdapter;

import java.util.List;

/**
 * @author lmw
 * @date 2019/3/11.
 */
public class LaunchProjectListAdapter extends BaseListAdapter<String, LaunchProjectListAdapter.ViewHolder> {

    static class ViewHolder extends BaseHolder {
        private TextView itemView;

        ViewHolder(View view) {
            super(view);
            itemView = get(R.id.textview);
        }
    }

    public LaunchProjectListAdapter(Activity mActivity, List<String> data) {
        super(mActivity, data);
    }

    @Override
    public int getItemLayout() {
        return R.layout.layout_launch_project_list;
    }

    @Override
    public ViewHolder getHolder(View baseView) {
        return new ViewHolder(baseView);
    }

    @Override
    public void buildHolder(ViewHolder holder, int position, String data) {
        holder.itemView.setText(data);
    }
}
