package cz.macinos.pricelist.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.macinos.pricelist.R;
import cz.macinos.pricelist.model.PricelistItem;

/**
 * Adapter for all pricelist lists.
 * Featuring filtering animations.
 *
 * Inspired by:  http://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview
 */
public class PricelistItemAdapter extends RecyclerView.Adapter<PricelistItemViewHolder> {

    private final LayoutInflater mInflater;
    private List<PricelistItem> pricelistItems;
    private Context context;

    public PricelistItemAdapter(Context context, List<PricelistItem> models) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        pricelistItems = new ArrayList<>(models);

    }

    @Override
    public PricelistItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.list_item_pricelist, parent, false);
        return new PricelistItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PricelistItemViewHolder holder, final int position) {
        final PricelistItem model = pricelistItems.get(position);

        //add item to selection when clicked on
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PricelistActivity.getSelectedPricelistItems().add(model);
                Toast.makeText(context, model.getName() + " " + context.getString(R.string.item_added), Toast.LENGTH_SHORT).show();
            }
        });

        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return pricelistItems.size();
    }

    public void setModels(List<PricelistItem> models) {
        pricelistItems = new ArrayList<>(models);
    }

    /* Methods for list editing */

    public PricelistItem removeItem(int position) {
        final PricelistItem model = pricelistItems.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, PricelistItem model) {
        pricelistItems.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final PricelistItem model = pricelistItems.remove(fromPosition);
        pricelistItems.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    /* Methods for list animation */

    public void animateTo(List<PricelistItem> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<PricelistItem> newModels) {
        for (int i = pricelistItems.size() - 1; i >= 0; i--) {
            final PricelistItem model = pricelistItems.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<PricelistItem> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final PricelistItem model = newModels.get(i);
            if (!pricelistItems.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<PricelistItem> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final PricelistItem model = newModels.get(toPosition);
            final int fromPosition = pricelistItems.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

}
