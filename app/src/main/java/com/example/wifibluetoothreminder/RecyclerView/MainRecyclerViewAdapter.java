package com.example.wifibluetoothreminder.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifibluetoothreminder.R;

import java.util.ArrayList;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.CoustomViewHolder> {

    private ArrayList<MainListModel> itemList;
    private OnListItemClickInterface mClickListener;
    private OnListItemLongClickInterface mLongClickListener;

    public MainRecyclerViewAdapter(ArrayList<MainListModel> item, OnListItemLongClickInterface LongClickListener, OnListItemClickInterface ClickListener){
        this.itemList = item;
        this.mClickListener = ClickListener;
        this.mLongClickListener = LongClickListener;
    }

    public interface OnListItemLongClickInterface{
        void onItemLongClick(View v, int position);
    }

    public interface OnListItemClickInterface{
        void onItemClick(View v, int position);
    }


    public class CoustomViewHolder extends RecyclerView.ViewHolder{

        public TextView NickName;
        public TextView ContentsCount;

        public CoustomViewHolder(View view){
            super(view);

            NickName = view.findViewById(R.id.NickName);
            ContentsCount = view.findViewById(R.id.ContentCount);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mClickListener.onItemClick(view, position);

                    Log.e("Click :", "position" + position);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    mLongClickListener.onItemLongClick(view, position);
                    Log.e("LongClick :", "position" + position);
                    return false;
                }
            });
        }
    }




    @NonNull
    @Override
    public MainRecyclerViewAdapter.CoustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.mainlistitem, parent, false);
        CoustomViewHolder coustomViewHolder = new CoustomViewHolder(view);

        return coustomViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainRecyclerViewAdapter.CoustomViewHolder holder, int position) {
        holder.NickName.setText(itemList.get(position).getNickName());
        holder.ContentsCount.setText(itemList.get(position).getContentsCount());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
