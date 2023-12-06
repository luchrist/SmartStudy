package de.christcoding.smartstudy.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.christcoding.smartstudy.databinding.ItemContainerCouponBinding;
import de.christcoding.smartstudy.models.Coupon;
import de.christcoding.smartstudy.utilities.CouponSelectListener;

import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.CouponViewHolder> {

    private final List<Coupon> coupons;
    private final CouponSelectListener selectListener;

    public CouponsAdapter(List<Coupon> coupons, CouponSelectListener selectListener) {
        this.coupons = coupons;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public CouponsAdapter.CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerCouponBinding itemContainerCouponBinding = ItemContainerCouponBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CouponsAdapter.CouponViewHolder(itemContainerCouponBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponsAdapter.CouponViewHolder holder, int position) {
        holder.setCouponData(coupons.get(position));
        holder.setListener();
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    class CouponViewHolder extends RecyclerView.ViewHolder {

        ItemContainerCouponBinding binding;

        public CouponViewHolder(ItemContainerCouponBinding itemContainerCouponBinding) {
            super(itemContainerCouponBinding.getRoot());
            binding = itemContainerCouponBinding;
        }

        public void setListener() {
            binding.btnRedeem.setOnClickListener(v -> selectListener.onCouponSelected(coupons.get(getAdapterPosition())));
        }

        public void setCouponData(Coupon coupon) {
            binding.couponValue.setText(coupon.value);
            binding.couponPrice.setText(String.valueOf(coupon.price));
            if(coupon.redeemed) {
                binding.btnRedeem.setText(coupon.code);
            }
            if(coupon.logo != null){
                binding.companyLogo.setImageBitmap(getImage(coupon.logo));
            }
        }
    }

    private Bitmap getImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
