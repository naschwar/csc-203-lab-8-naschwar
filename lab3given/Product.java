import java.util.ArrayList;

import java.util.List;

public class Product {
    public final String productId;
    public int num_purchased;
    public final int cost;
    public ArrayList<String> views;
    public ArrayList<String> bought;
    public int num_viewed;

    public Product(String productId, int cost) {
        this.productId = productId;
        this.cost = cost;
        this.num_purchased = 0;
        this.num_viewed = 0;
        this.views = new ArrayList<String>();
        this.bought = (ArrayList<String>) bought;

    }

}

