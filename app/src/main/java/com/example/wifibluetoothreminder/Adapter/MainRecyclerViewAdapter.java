package com.example.wifibluetoothreminder.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifibluetoothreminder.R;
import com.example.wifibluetoothreminder.Room.WifiBluetoothList;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.CoustomViewHolder> {

    private List<WifiBluetoothList> itemList;
    private OnListItemClickInterface mClickListener;
    private OnListItemLongClickInterface mLongClickListener;

    public MainRecyclerViewAdapter(List<WifiBluetoothList> item, OnListItemLongClickInterface LongClickListener, OnListItemClickInterface ClickListener){
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
        public ImageView DeviceName;

        public CoustomViewHolder(View view){
            super(view);

            NickName = view.findViewById(R.id.NickName);
            ContentsCount = view.findViewById(R.id.ContentCount);
            DeviceName = view.findViewById(R.id.devicename);

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
        holder.ContentsCount.setText(String.valueOf(itemList.get(position).getCount()));
        if (itemList.get(position).getDevice_Type().equals("Wifi"))
            holder.DeviceName.setImageResource(R.drawable.ic_wifi_black_24dp);
        else if (itemList.get(position).getDevice_Type().equals("Bluetooth"))
            holder.DeviceName.setImageResource(R.drawable.ic_bluetooth_black_24dp);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
