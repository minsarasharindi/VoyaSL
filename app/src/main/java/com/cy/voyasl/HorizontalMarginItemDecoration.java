package com.cy.voyasl;

import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Rect;
import android.view.View;

public class HorizontalMarginItemDecoration extends RecyclerView.ItemDecoration {
    private final int horizontalMarginInPx;

    public HorizontalMarginItemDecoration(int horizontalMarginInPx) {
        this.horizontalMarginInPx = horizontalMarginInPx;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = horizontalMarginInPx;
        outRect.left = horizontalMarginInPx;
    }
}
