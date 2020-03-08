package com.yonggeun.wifibluetoothreminder;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DecorationItem extends RecyclerView.ItemDecoration {

    private final int Height;


    public DecorationItem(int height) {
        this.Height = height;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.top = Height;
            outRect.bottom = Height;
        }
    }
}