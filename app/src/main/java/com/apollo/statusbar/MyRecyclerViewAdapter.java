package com.apollo.statusbar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lei.xiao on 2017/12/27.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private List<String> list;
    private Context mContext;

    public void setList(List<String> list) {
        this.list = list;
    }

    public MyRecyclerViewAdapter(Context context, List<String> list){
        this.mContext=context;
        this.list=list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder=new MyViewHolder(LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1,parent,false));
        if(viewType==0){
            myViewHolder=new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_head,parent,false));
        }
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "itemclick", Toast.LENGTH_SHORT).show();
//            }
//        });
        if(position==0){

        }else{
            holder.textView.setText(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position==0?0:1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView headView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView= (TextView) itemView.findViewById(android.R.id.text1);
            headView= (ImageView) itemView.findViewById(R.id.head_view);
        }
    }
}
