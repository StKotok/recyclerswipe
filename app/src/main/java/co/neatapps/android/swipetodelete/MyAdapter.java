package co.neatapps.android.swipetodelete;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.neatapps.std.STDAdapterHelper;
import co.neatapps.std.STDAwaiting;
import co.neatapps.std.STDInterface;


class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> implements STDInterface {

    private final Context context;
    private final STDAdapterHelper<MyAdapter> stdAdapterHelper;

    private ArrayList<String> items;


    public MyAdapter(Context context, int deleteBackground) {
        this.context = context;
        stdAdapterHelper = new STDAdapterHelper<>(3000, deleteBackground, this);
    }

    /**
     * Android's {@link RecyclerView.Adapter} method.
     */
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MyHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    /**
     * Android's {@link RecyclerView.Adapter} method.
     */
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        // invoke the helper
        stdAdapterHelper.onBindViewHolder(holder, position);

        String itemLabelText = items.get(position);
        holder.label.setText(itemLabelText);
    }

    /**
     * Android's {@link RecyclerView.Adapter} method.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * This is inherited from {@link STDInterface} method.
     * Use it if you want to make some for after item deleting.
     *
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void removingFromItems(int position) {
        String itemLabel = items.get(position);
        Toast.makeText(context, itemLabel, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return the adapter's data set.
     */
    @Override
    public List getItems() {
        return items;
    }

    /**
     * Your own implementation of items setter.
     * Just don't forget to invoke the {@link RecyclerView.Adapter#notifyDataSetChanged()} at the end.
     *
     * @param items
     */
    public void setItems(ArrayList<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * @return the adapter's {@link STDAdapterHelper}.
     */
    @Override
    public STDAwaiting getSTDAdapterHelper() {
        return stdAdapterHelper;
    }

    /**
     * Holder extended from {@link STDAdapterHelper.VHolder} that extended from
     * {@link RecyclerView.ViewHolder}.
     */
    static class MyHolder extends STDAdapterHelper.VHolder {

        private final TextView label;

        /**
         * This constructor pass the itemView to
         *
         * @param itemView
         */
        public MyHolder(View itemView) {
            // Pass standard 'itemView' and two your layout elements:
            //      1) This layout will be shifted by swipe gesture.
            //      2) This view will be represent the UndoButton.
            super(itemView, itemView.findViewById(R.id.main_layout), itemView.findViewById(R.id.undo));

            label = itemView.findViewById(R.id.label);
        }

    }

}
