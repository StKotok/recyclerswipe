/*
   Copyright 2018 Stepan Kotok.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */


package co.stkotok.swipetodelete;


import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;


public class STDItemCallback<A extends RecyclerView.Adapter & STDInterface> extends ItemTouchHelper.SimpleCallback {

    private final int deleteIconResId;
    private final int deleteIconMargin;
    private final int backgroundColor;
    private final A adapter;

    private Drawable background;
    private Drawable iconDrawable;

    public STDItemCallback(int deleteIconResId, int deleteIconMargin, int backgroundColor, A adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.deleteIconResId = deleteIconResId;
        this.deleteIconMargin = deleteIconMargin;
        this.backgroundColor = backgroundColor;
        this.adapter = adapter;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (adapter.getSTDAdapterHelper().isAwaiting(viewHolder.getAdapterPosition())) {
            return 0;
        }
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        adapter.getSTDAdapterHelper().await(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas canvas,
                            RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (viewHolder.getAdapterPosition() == -1) return;

        View itemView = viewHolder.itemView;
        drawBackground(canvas, itemView);
        drawIcon(canvas, recyclerView, itemView, dX);

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void drawBackground(Canvas c, View v) {
        if (background == null) {
            background = new ColorDrawable(backgroundColor);
        }

        background.setBounds(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());

        background.draw(c);
    }

    private void drawIcon(Canvas c, RecyclerView recyclerView, View v, float dX) {
        if (iconDrawable == null) {
            iconDrawable = ContextCompat.getDrawable(recyclerView.getContext(), deleteIconResId);
            // iconDrawable.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
        }

        int viewHeight = v.getBottom() - v.getTop();
        int iconWidth = iconDrawable.getIntrinsicWidth();
        int iconHeight = iconDrawable.getIntrinsicHeight();

        int iconTop = v.getTop() + (viewHeight - iconHeight) / 2;
        int iconBottom = iconTop + iconHeight;

        int iconLeft;
        int iconRight;

        if (dX > 0) {
            iconLeft = v.getLeft() + deleteIconMargin;
            iconRight = v.getLeft() + deleteIconMargin + iconWidth;
        } else {
            iconLeft = v.getRight() - deleteIconMargin - iconWidth;
            iconRight = v.getRight() - deleteIconMargin;
        }

        iconDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);

        iconDrawable.draw(c);
    }

}
