package com.example.wordgame;


import java.util.ArrayList;

public class Shop {
    private float itemPrice;
    private String itemTitle;
    private int itemImg;

    public Shop(){}

    public Shop(float itemPrice, String itemTitle, int itemImg) {
        this.itemPrice = itemPrice;
        this.itemTitle = itemTitle;
        this.itemImg = itemImg;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public int getItemImg() {
        return itemImg;
    }

    public void setItemImg(int itemImg) {
        this.itemImg = itemImg;
    }

    static public ArrayList<Shop> getData(){
        ArrayList<Shop> shopList = new ArrayList<>();
        String[] itemTitleList = {"3 Adet Can", "15 Adet Can", "50 Adet Can", "90 Adet Can", "500 Adet Can"};
        int[] itemImgList = {R.drawable.heart, R.drawable.heart, R.drawable.shopheart1, R.drawable.shopheart2, R.drawable.shopheart2};
        float[] itemPriceList = {0.99f, 3.99f, 9.89f, 15.45f, 20.99f};

        for (int i = 0; i < itemTitleList.length; i++){
            Shop shop = new Shop();
            shop.setItemTitle(itemTitleList[i]);
            shop.setItemPrice(itemPriceList[i]);
            shop.setItemImg(itemImgList[i]);

            shopList.add(shop);
        }

        return shopList;
    }
}

