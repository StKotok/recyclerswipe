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


import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class STDAdapterHelper<A extends RecyclerView.Adapter & STDInterface> implements STDAwaiting {

    private final int timeoutToDelete;
    private final int redBackground;
    private final A adapter;

    // logic for deleting
    private final Handler handler = new Handler();
    private final HashMap<Object, Runnable> awaitingRunnables = new HashMap<>();
    private final List<Object> itemsAwaitingRemoval = new ArrayList<>();

    public STDAdapterHelper(int millisToDelete, int redBackground, A adapter) {
        this.timeoutToDelete = millisToDelete;
        this.redBackground = redBackground;
        this.adapter = adapter;
    }

    public void onBindViewHolder(VHolder holder, int position) {

        final Object item = adapter.getItems().get(position);

        // set visibility
        boolean awaitingForDeleting = itemsAwaitingRemoval.contains(item);
        holder.mainLayout.setVisibility(awaitingForDeleting ? View.INVISIBLE : View.VISIBLE);
        holder.undoButton.setVisibility(awaitingForDeleting ? View.VISIBLE : View.GONE);
        holder.itemView.setBackgroundColor(redBackground);
//      holder.itemView.setBackgroundColor(awaitingForDeleting ? Color.RED : Color.WHITE);

        // set onClick listeners
        if (awaitingForDeleting) {
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelTaskAndRemoveFromItems(item);
                }
            });
        }
    }

    private void cancelTaskAndRemoveFromItems(Object item) {
        // cancel the awaiting task
        Runnable awaitingRemovalRunnable = awaitingRunnables.get(item);
        awaitingRunnables.remove(item);
        if (awaitingRemovalRunnable != null) {
            handler.removeCallbacks(awaitingRemovalRunnable);
        }
        itemsAwaitingRemoval.remove(item);
        // this will rebind the row in "normal" state
        adapter.notifyItemChanged(adapter.getItems().indexOf(item));
    }

    public void await(int position) {
        final Object item = adapter.getItems().get(position);
        if (!itemsAwaitingRemoval.contains(item)) {
            itemsAwaitingRemoval.add(item);
            // this will redraw row in "undo" state
            adapter.notifyItemChanged(position);
            // runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(item);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, timeoutToDelete);
            awaitingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public boolean isAwaiting(int position) {
        Object item = adapter.getItems().get(position);
        return itemsAwaitingRemoval.contains(item);
    }

    private void remove(Object item) {
        itemsAwaitingRemoval.remove(item);

        if (adapter.getItems().contains(item)) {
            int position = adapter.getItems().indexOf(item);
            adapter.removingFromItems(position);
            adapter.getItems().remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    public void cancelAllItemsAwaitingRemoval() {
        for (int i = itemsAwaitingRemoval.size() - 1; i >= 0; i--) {
            Object item = itemsAwaitingRemoval.get(i);
            cancelTaskAndRemoveFromItems(item);
        }
    }

    /**
     * ViewHolder, that extended from {@link RecyclerView.ViewHolder} and holds:
     * - layout, that will be shifted by swipe gesture;
     * - view, that represents the undo button.
     */
    public abstract static class VHolder extends RecyclerView.ViewHolder {
        public View mainLayout;
        public View undoButton;

        /**
         * @param itemView   Standard Android itemView param.
         * @param mainLayout This layout will be shifted by swipe gesture.
         * @param undoButton This view will be represent the UndoButton.
         */
        public VHolder(View itemView, View mainLayout, View undoButton) {
            super(itemView);
            this.mainLayout = mainLayout;
            this.undoButton = undoButton;
        }
    }

}
