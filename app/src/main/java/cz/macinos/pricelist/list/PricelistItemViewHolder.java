package cz.macinos.pricelist.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cz.macinos.pricelist.R;
import cz.macinos.pricelist.model.PricelistItem;

/**
 * Creates recycler list items from PricelistItem model.
 */
public class PricelistItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;
    private final TextView price;
    private final TextView unit;

    public PricelistItemViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.name);
        price = (TextView) itemView.findViewById(R.id.price);
        unit = (TextView) itemView.findViewById(R.id.unit);
    }

    /**
     * Retrieves and formats the values from pricelist item.
     * @param model PricelistItem object.
     */
    public void bind(PricelistItem model) {
        name.setText(model.getName());
        price.setText(model.getPrice().toString().concat(" Kč"));
        unit.setText("(" + model.getUnit() + ")");
    }
}
