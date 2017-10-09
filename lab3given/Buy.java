import java.util.ArrayList;

import java.util.List;

public class Buy{
    public final String productId;
    public int num_purchased;
    public final int cost;
    public ArrayList<String> views;
    public ArrayList<String> bought;
    public int num_viewed;

    public Buy(String productId, int cost) {
        this.productId = productId;
        this.cost = cost;
        this.num_purchased = 0;


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

    public String getProduct(){
        return productId;
    }

}