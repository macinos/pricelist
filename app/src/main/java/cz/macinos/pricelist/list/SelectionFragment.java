package cz.macinos.pricelist.list;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cz.macinos.pricelist.R;
import cz.macinos.pricelist.model.PricelistItem;

/**
 * Fragment for selection tab in pricelist.
 */
public class SelectionFragment extends Fragment {

    private static final String TAG = "SelectionFragment";

    private RecyclerView mRecyclerView;
    private PricelistItemAdapter mAdapter;
    private List<PricelistItem> pricelistItems;

    public SelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_selection, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_pricelist);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setHasOptionsMenu(true);
        Log.i(TAG,"*** Pricelist fragment view created...");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pricelistItems = PricelistActivity.getSelectedPricelistItems();

        mAdapter = new PricelistItemAdapter(getActivity(), pricelistItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    //fired when fragment becomes visible or invisible to user
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        //update selection list
        if (isVisibleToUser) {
            pricelistItems = PricelistActivity.getSelectedPricelistItems();

            mAdapter = new PricelistItemAdapter(getActivity(), pricelistItems);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

}
