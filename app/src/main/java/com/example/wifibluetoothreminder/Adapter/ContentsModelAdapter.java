package com.example.wifibluetoothreminder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifibluetoothreminder.R;
import com.example.wifibluetoothreminder.Room.ContentList;

import java.util.ArrayList;
import java.util.List;

public class ContentsModelAdapter extends RecyclerView.Adapter<ContentsModelAdapter.CustomViewHoler> {

    private List<ContentList> list;
    private List<ContentList> checkedlist;

    private int count;

    private ContentsModelAdapter.OnListItemClickInterface mClickListener;
    private ContentsModelAdapter.OnListItemLongClickInterface mLongClickListener;

    public ContentsModelAdapter(List<ContentList> item, OnListItemClickInterface onListItemClickInterface, OnListItemLongClickInterface onListItemLongClickInterface) {
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
        public CheckBox checkBox;

        public CustomViewHoler(@NonNull View itemView) {
            super(itemView);

            ContentView = itemView.findViewById(R.id.contents);
            checkBox = itemView.findViewById(R.id.checkbox);

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
    public void onBindViewHolder(@NonNull ContentsModelAdapter.CustomViewHoler holder, final int position) {
        holder.ContentView.setText(list.get(position).getContent());
        checkedlist = new ArrayList<>();
        if (getCount() % 2 == 0)
            holder.checkBox.setVisibility(View.GONE);
        else if (getCount() % 2 == 1) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        checkedlist.add(list.get(position));
                    else
                        checkedlist.remove(list.get(position));
                }
            });
        }
    }

    public List<ContentList> getCheckedlist(){
        return checkedlist;
    }

    public void setCount(int Count){
        count = Count;
    }

    public int getCount(){
        return count;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
