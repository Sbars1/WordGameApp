package com.example.wordgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopHolder> {
    private ArrayList<Shop> shopArrayList;
    private Context context;
    private Shop mShop;
    private OnItemClickListener listener;

    public ShopAdapter(ArrayList<Shop> shopArrayList, Context context) {
        this.shopArrayList = shopArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false);
        return new ShopHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopHolder holder, int position) {
        mShop = shopArrayList.get(position);
        holder.setData(mShop);
    }

    @Override
    public int getItemCount() {
        return shopArrayList.size();
    }

    class ShopHolder extends RecyclerView.ViewHolder {
        TextView txtItemTitle;
        ImageView imgItem;
        Button btnItemPrice;

        public ShopHolder(@NonNull View itemView) {
            super(itemView);

            txtItemTitle = (TextView) itemView.findViewById(R.id.custom_dialog_shop_item_textViewItemTitle);
            imgItem = (ImageView) itemView.findViewById(R.id.custom_dialog_shop_item_imageViewItemImg);
            btnItemPrice = (Button) itemView.findViewById(R.id.custom_dialog_shop_item_btnItemPrice);

            btnItemPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(shopArrayList.get(position), position);
                }
            });
        }

        public void setData(Shop shop) {
            this.txtItemTitle.setText(shop.getItemTitle());
            this.imgItem.setBackgroundResource(shop.getItemImg());
            this.btnItemPrice.setText(shop.getItemPrice() + " TL");
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Shop mShop, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}

