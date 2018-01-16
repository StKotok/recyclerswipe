package co.neatapps.android.swipetodelete;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;

import co.neatapps.std.STDItemCallback;
import co.neatapps.std.STDItemDecoration;


public class MainActivity extends AppCompatActivity {

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        int itemRemovalBackgroundColor = ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light);

        adapter = new MyAdapter(this, itemRemovalBackgroundColor);
        recyclerView.setAdapter(adapter);

        // this will draw delete icon on the red background while swipe gesture
        int iconResId = R.drawable.ic_delete;
        int iconMargin = (int) getResources().getDimension(R.dimen.margin);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new STDItemCallback<>(
                iconResId, iconMargin, itemRemovalBackgroundColor, adapter
        ));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        // item divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        // this will draw red background while deleting animation
        recyclerView.addItemDecoration(new STDItemDecoration(itemRemovalBackgroundColor));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ArrayList<String> items = getItems();
        adapter.setItems(items);
    }

    private ArrayList<String> getItems() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            list.add("item " + i);
        }
        return list;
    }

}
