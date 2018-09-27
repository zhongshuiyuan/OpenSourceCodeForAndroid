package com.icodeman.baselib.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.icodeman.baselib.utils.EmptyUtil;

import java.util.List;

/**
 * Created by lmw on 2018/5/5.
 */
public abstract class BaseListAdapter<D,H extends BaseHolder> extends BaseAdapter{
    private Activity mActivity;
    private List<D> data;

    public BaseListAdapter(Activity mActivity, List<D> data) {
        this.mActivity = mActivity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null?0:data.size();
    }

    @Override
    public D getItem(int position) {
        if(EmptyUtil.isEmpty(data)||data.size()<position+1){
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mActivity).inflate(getItemLayout(),null);
            holder = getHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (H)convertView.getTag();
        }
        buildHolder(holder,data.get(position));
        return convertView;
    }

    public abstract int getItemLayout();

    public abstract H getHolder(View baseView);

    public abstract void buildHolder(H holder,D data);
}
