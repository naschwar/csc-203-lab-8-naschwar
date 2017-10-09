import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

public class View {
    public final String productId;
    public int num_purchased;
    public final int cost;
    public LinkedList<String> views;
    public ArrayList<String> bought;
    public int num_viewed;

    public View(String productId, int cost) {
        this.productId = productId;
        this.cost = cost;
        this.num_viewed = 0;
        this.views = new LinkedList<String>();


    }
    public String getProduct(){
        return productId;
    }

    public int productIDtoInt(){
        String stringID = this.productId;
        String intID = "";
        int size = stringID.length();
        for (int i =0; i < size; i ++){
            if (Character.isDigit(stringID.charAt(i))){
                intID += stringID.charAt(i);
            }
        }
        return  Integer.parseInt(intID);
    }
}
