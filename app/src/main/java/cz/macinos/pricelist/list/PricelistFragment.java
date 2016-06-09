package cz.macinos.pricelist.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.macinos.pricelist.R;
import cz.macinos.pricelist.model.PricelistItem;

/**
 * Fragment containing filterable pricelist.
 */
public class PricelistFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "PricelistFragment";

    private RecyclerView mRecyclerView;
    private PricelistItemAdapter mAdapter;
    private List<PricelistItem> pricelistItems;

    public PricelistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_pricelist, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_pricelist);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(TAG,"*** Pricelist fragment view created...");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pricelistItems = generatePriceListItems();

        mAdapter = new PricelistItemAdapter(getActivity(), pricelistItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(Integer.MAX_VALUE); //search field should take the whole width
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<PricelistItem> filteredModelList = filter(pricelistItems, query);
        mAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    /**
     * Filtering the pricelist by string entered in the text filter.
     * Items are filtered by name only.
     * @param models All price list items.
     * @param query String witch which to filter the list.
     * @return Filtered pricelist.
     */
    private List<PricelistItem> filter(List<PricelistItem> models, String query) {
        query = query.toLowerCase();

        final List<PricelistItem> filteredModelList = new ArrayList<>();
        for (PricelistItem model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Parse and convert raw pricelist to list of price list item objects.
     * @return Pricelist items ArrayList.
     */
    private List<PricelistItem> generatePriceListItems() {
        List<PricelistItem> items = new ArrayList<>();
        String rawPricelist = PricelistActivity.getRawPricelist();

        String[] rows = rawPricelist.split("\\r?\\n");

        for (int i = 1; i < rows.length; i++) { //start from second line, first is header
            if (rows[i].trim().isEmpty()) { //check if row is not empty
                continue;
            }
            String[] rowData = rows[i].split(";");

            PricelistItem item = new PricelistItem();
            item.setName(rowData[0]);
            item.setPrice(rowData[1]);
            item.setUnit(rowData[2]);
            items.add(item);
        }

        return items;
    }

}
