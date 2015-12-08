package com.yejia.listviewfreshload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yejiapc on 15/12/7.
 */
public class PostListAdapter extends BaseAdapter{
    ArrayList<PostEntity> post_list;
    LayoutInflater inflater;

    public PostListAdapter(Context context, ArrayList<PostEntity> post_list){
        this.post_list = post_list;
        this.inflater = LayoutInflater.from(context);
    }

    public void onDataChanged(ArrayList<PostEntity> post_list){
        this.post_list = post_list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return post_list.size();
    }

    @Override
    public Object getItem(int position) {
        return post_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PostEntity entity = post_list.get(position);
        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.post_cell,null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_title.setText(entity.getTitle());
        holder.tv_date.setText(entity.getDate());

        return convertView;
    }

    class ViewHolder{
        TextView tv_title;
        TextView tv_date;
    }
}
