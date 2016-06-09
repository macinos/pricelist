package cz.macinos.pricelist.model;

/**
 * Data model representing a price list item.
 * Item contains its name, price and unit.
 */
public class PricelistItem {

    private String name;
    private String price;
    private String unit;


    public PricelistItem() {
    }

    public PricelistItem(String name, String price, String unit) {
        this.price = price;
        this.name = name;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PricelistItem{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                '}';
    }
}
