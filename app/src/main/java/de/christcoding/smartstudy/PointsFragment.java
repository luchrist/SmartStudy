package de.christcoding.smartstudy;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import de.christcoding.smartstudy.adapters.CouponsAdapter;
import de.christcoding.smartstudy.models.Coupon;
import de.christcoding.smartstudy.models.User;
import de.christcoding.smartstudy.utilities.Constants;
import de.christcoding.smartstudy.utilities.CouponSelectListener;
import de.christcoding.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PointsFragment extends Fragment implements CouponSelectListener {

    private ProgressBar yourProgressBar, availableProgressBar;
    private TextView noPossesedCoupons, noAvailableCoupons, points;
    private RecyclerView yourCouponsRecyclerView, availableCouponsRecyclerView;
    private List<Coupon> yourCoupons = new ArrayList<>();
    private List<Coupon> availableCoupons = new ArrayList<>();
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private CouponsAdapter yourCouponsAdapter, availableCouponsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_points, container, false);

        yourProgressBar = view.findViewById(R.id.progressBar);
        availableProgressBar = view.findViewById(R.id.progressBar2);
        noPossesedCoupons = view.findViewById(R.id.noPosessedCoupons);
        noAvailableCoupons = view.findViewById(R.id.noAvailableCoupons);
        yourCouponsRecyclerView = view.findViewById(R.id.yourCouponsRecyclerView);
        availableCouponsRecyclerView = view.findViewById(R.id.availableCouponsRecyclerView);

        points = getActivity().findViewById(R.id.points);

        preferenceManager = new PreferenceManager(requireContext());
        db = FirebaseFirestore.getInstance();

        showCoupons();

        return view;
    }

    private void showCoupons() {
        loading(true);
        db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_EMAIL)).get()
                .addOnCompleteListener(task -> {
                    List<String> couponIds = null;
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot documents = task.getResult();
                        User user = documents.toObject(User.class);
                        if (user != null){
                            couponIds = user.couponIds;
                        }
                        if (couponIds == null || couponIds.size() == 0) {
                            noPossesedCoupons.setVisibility(View.VISIBLE);
                            yourCouponsRecyclerView.setVisibility(View.GONE);
                            yourProgressBar.setVisibility(View.GONE);
                        }
                        if(couponIds == null){
                            couponIds = Collections.emptyList();
                        }
                        getAllCouponsAndSort(couponIds);
                    } else {
                        loading(false);
                        noPossesedCoupons.setVisibility(View.VISIBLE);
                        yourCouponsRecyclerView.setVisibility(View.GONE);
                        noAvailableCoupons.setVisibility(View.VISIBLE);
                        availableCouponsRecyclerView.setVisibility(View.GONE);
                    }
                });
    }

    private void getAllCouponsAndSort(List<String> couponIds) {
        db.collection(Constants.KEY_COLLECTION_COUPONS).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        List<Coupon> coupons = docs.stream().map(document -> document.toObject(Coupon.class)).collect(Collectors.toList());
                        if (coupons.size() == 0) {
                            noAvailableCoupons.setVisibility(View.VISIBLE);
                            availableCouponsRecyclerView.setVisibility(View.GONE);
                            loading(false);
                        } else {
                            for (Coupon coupon : coupons) {
                                if (couponIds.contains(coupon.id)) {
                                    coupon.redeemed = true;
                                    yourCoupons.add(coupon);
                                } else {
                                    availableCoupons.add(coupon);
                                }
                            }
                            if (yourCoupons.size() == 0) {
                                noPossesedCoupons.setVisibility(View.VISIBLE);
                                yourCouponsRecyclerView.setVisibility(View.GONE);
                            }
                            if (availableCoupons.size() == 0) {
                                noAvailableCoupons.setVisibility(View.VISIBLE);
                                availableCouponsRecyclerView.setVisibility(View.GONE);
                            }
                            yourCouponsAdapter = new CouponsAdapter(yourCoupons, this);
                            availableCouponsAdapter = new CouponsAdapter(availableCoupons, this);

                            yourCouponsRecyclerView.setAdapter(yourCouponsAdapter);
                            availableCouponsRecyclerView.setAdapter(availableCouponsAdapter);
                            loading(false);
                        }
                    } else {
                        loading(false);
                        noPossesedCoupons.setVisibility(View.VISIBLE);
                        yourCouponsRecyclerView.setVisibility(View.GONE);
                        noAvailableCoupons.setVisibility(View.VISIBLE);
                        availableCouponsRecyclerView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onCouponSelected(Coupon coupon) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("coupon code", coupon.code);
        if (coupon.redeemed) {
            //copy code to clipboard
            clipboard.setPrimaryClip(clip);
        } else {
            //redeem coupon if user has enough points
            if(Integer.parseInt(points.getText().toString()) >= coupon.price) {
                int index = availableCoupons.indexOf(coupon);
                coupon.redeemed = true;
                availableCouponsAdapter.notifyItemChanged(index);
                removePoints(coupon.price);
                addCouponIdToUser(coupon.id);
                //copy code to clipboard
                clipboard.setPrimaryClip(clip);
            } else {
                Toast.makeText(getContext(), "You don't have enough points", Toast.LENGTH_SHORT).show();
                //show error
            }
        }
    }

    private void addCouponIdToUser(String id) {
        String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserEmail).update(Constants.KEY_COUPON_IDS, FieldValue.arrayUnion(id));
    }

    private void removePoints(int price) {
        int currentPoints = Integer.parseInt(points.getText().toString().trim());
        currentPoints -= price;
        points.setText(String.valueOf(currentPoints));

        String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserEmail).update(Constants.KEY_POINTS, FieldValue.increment(-price));
    }
    private void loading(boolean isLoading) {
        if (isLoading) {
            availableProgressBar.setVisibility(View.VISIBLE);
            yourProgressBar.setVisibility(View.VISIBLE);
        }else {
            availableProgressBar.setVisibility(View.GONE);
            yourProgressBar.setVisibility(View.GONE);
        }
    }
}