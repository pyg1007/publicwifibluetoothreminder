package com.example.wifibluetoothreminder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifibluetoothreminder.R;

import java.util.ArrayList;

public class ContentsModelAdapter extends RecyclerView.Adapter<ContentsModelAdapter.CustomViewHoler> {

    ArrayList<ContentsModel> list;
    private ContentsModelAdapter.OnListItemClickInterface mClickListener;
    private ContentsModelAdapter.OnListItemLongClickInterface mLongClickListener;

    public ContentsModelAdapter(ArrayList<ContentsModel> item, OnListItemClickInterface onListItemClickInterface, OnListItemLongClickInterface onListItemLongClickInterface) {
        this.list = item;
        this.mClickListener = onListItemClickInterface;
        this.mLongClickListener = onListItemLongClickInterface;
    }

    public interface OnListItemLongClickInterface {
        void onItemLongClick(View v, int position);
    }

    public interface OnListItemClickInterface {
        void onItemClick(View v, int position);
    }

    public class CustomViewHoler extends RecyclerView.ViewHolder {

        public TextView ContentView;

        public CustomViewHoler(@NonNull View itemView) {
            super(itemView);

            ContentView = itemView.findViewById(R.id.contents);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mClickListener.onItemClick(view, position);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    mLongClickListener.onItemLongClick(view, position);
                    return false;
                }
            });

        }
    }

    @NonNull
    @Override
    public ContentsModelAdapter.CustomViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.contentslistitem, parent, false);
        CustomViewHoler customViewHoler = new CustomViewHoler(view);
        return customViewHoler;
    }

    @Override
    public void onBindViewHolder(@NonNull ContentsModelAdapter.CustomViewHoler holder, int position) {
        holder.ContentView.setText(list.get(position).getContents());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
