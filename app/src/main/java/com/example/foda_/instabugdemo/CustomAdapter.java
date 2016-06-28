package com.example.foda_.instabugdemo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by foda_ on 2016-06-25.
 */
public class CustomAdapter extends BaseAdapter {
    public Context mycontext;
    public ArrayList<DataFromJson>repo_info;

    public LayoutInflater inflater;
    public CustomAdapter(Context mycontext,ArrayList<DataFromJson> repo_info)
    {
        this.mycontext=mycontext;
        this.repo_info=repo_info;
        inflater=(LayoutInflater)mycontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return repo_info.size();
    }

    @Override
    public Object getItem(int position) {
        return repo_info.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public static class  Holder
{
    TextView RepoName,RepoDesc,RepoUsename;
}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;
     if (convertView==null) {
         convertView = inflater.inflate(R.layout.custom_layout, parent, false);
         holder = new Holder();
         holder.RepoName = (TextView) convertView.findViewById(R.id.repoName_TextView);
         holder.RepoDesc = (TextView) convertView.findViewById(R.id.repoDesc_TextView);
         holder.RepoUsename = (TextView) convertView.findViewById(R.id.repoUsername_TextView);
         convertView.setTag(holder);
     }
        else {
         holder=(Holder)convertView.getTag();
        }
        Object o=getItem(position);
        holder.RepoName.setText("Repo Name :" + repo_info.get(position).repo_name.toString());
        holder.RepoDesc.setText("Description :" + repo_info.get(position).repo_description.toString());
        holder.RepoUsename.setText("UserName : " + repo_info.get(position).repo_username.toString());
         if (repo_info.get(position).fork==false)
        {
          convertView.setBackgroundColor(Color.parseColor("#9ACD32"));
        }
        else {
             convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
         }
        return convertView;
    }
}
