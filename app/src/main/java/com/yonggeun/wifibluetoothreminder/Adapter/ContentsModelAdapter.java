package com.yonggeun.wifibluetoothreminder.Adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yonggeun.wifibluetoothreminder.R;
import com.yonggeun.wifibluetoothreminder.Room.ContentList;

import java.util.ArrayList;
import java.util.List;

public class ContentsModelAdapter extends RecyclerView.Adapter<ContentsModelAdapter.CustomViewHolder> {

    private List<ContentList> list;
    private List<ContentList> checkedlist = new ArrayList<>();
    SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

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

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView ContentView;
        public CheckBox checkBox;

        public CustomViewHolder(@NonNull View itemView) {
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

        public void bind(int position) {
            if (!sparseBooleanArray.get(position, false))
                checkBox.setChecked(false);
            else
                checkBox.setChecked(true);

        }
    }

    @NonNull
    @Override
    public ContentsModelAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.contentslistitem, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentsModelAdapter.CustomViewHolder holder, final int position) {
        holder.ContentView.setText(list.get(position).getContent());
        if (getCount() % 2 == 0) {
            holder.checkBox.setVisibility(View.GONE);
        } else if (getCount() % 2 == 1) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.bind(position);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!sparseBooleanArray.get(position, false)) {
                        holder.checkBox.setChecked(true);
                        sparseBooleanArray.put(position, true);
                    } else {
                        holder.checkBox.setChecked(false);
                        sparseBooleanArray.put(position, false);
                    }
                }
            });
        }
    }

    public void ClearSparseBooleanArray() {
        sparseBooleanArray.clear();
    }

    public List<ContentList> getCheckedlist() {
        checkedlist.clear();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            if (sparseBooleanArray.get(i, true))
                checkedlist.add(list.get(i));
        }
        return checkedlist;
    }

    public void setCount(int Count) {
        count = Count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
