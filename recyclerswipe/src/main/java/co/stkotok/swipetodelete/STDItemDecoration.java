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
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class STDItemDecoration extends RecyclerView.ItemDecoration {

    private final int backgroundColor;
    private Drawable background;

    public STDItemDecoration(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State rvState) {

        if (recyclerView.getItemAnimator().isRunning()) {

            if (background == null) background = new ColorDrawable(backgroundColor);

            // find nearby shifting views
            View topShiftingView = null;
            View bottomShiftingView = null;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            for (int i = 0; i < layoutManager.getChildCount(); i++) {
                View view = layoutManager.getChildAt(i);

                boolean isViewShiftingDown = view.getTranslationY() < 0;
                if (isViewShiftingDown) {
                    topShiftingView = view;
                }
                boolean isViewShiftingUp = view.getTranslationY() > 0;
                if (isViewShiftingUp) {
                    bottomShiftingView = view;
                    break;
                }
            }

            // find bounds for drawing background
            Rect bounds = new Rect();
            bounds.left = 0;
            bounds.right = recyclerView.getWidth();
            if (topShiftingView != null) {
                int bottomOfTopView = topShiftingView.getBottom();
                bounds.top = bottomOfTopView + (int) topShiftingView.getTranslationY();
                if (bottomShiftingView == null) {
                    bounds.bottom = bottomOfTopView;
                }
            }
            if (bottomShiftingView != null) {
                int topOfBottomView = bottomShiftingView.getTop();
                bounds.bottom = topOfBottomView + (int) bottomShiftingView.getTranslationY();
                if (topShiftingView == null) {
                    bounds.top = topOfBottomView;
                }
            }

            background.setBounds(bounds);

            background.draw(canvas);
        }

        super.onDraw(canvas, recyclerView, rvState);
    }

}
