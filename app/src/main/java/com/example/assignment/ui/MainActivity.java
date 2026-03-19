package com.example.assignment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.assignment.DrinkApp;
import com.example.assignment.R;
import com.example.assignment.ui.auth.AuthActivity;
import com.example.assignment.ui.cart.CartFragment;
import com.example.assignment.ui.custom.BottomNav;
import com.example.assignment.ui.home.HomeFragment;
import com.example.assignment.ui.orders.OrderSuccessFragment;
import com.example.assignment.ui.orders.OrdersFragment;
import com.example.assignment.ui.orders.OrderDetailFragment;
import com.example.assignment.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements OrderSuccessFragment.OnOrderPlaced {

    private BottomNav bottomNav;
    private HomeFragment homeFragment;
    private CartFragment cartFragment;
    private ProfileFragment profileFragment;
    private OrdersFragment ordersFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!DrinkApp.instance.getSessionManager().isLoggedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        homeFragment = new HomeFragment();
        cartFragment = new CartFragment();
        ordersFragment = new OrdersFragment();
        profileFragment = new ProfileFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainContainer, profileFragment, "profile").hide(profileFragment)
                .add(R.id.mainContainer, ordersFragment, "orders").hide(ordersFragment)
                .add(R.id.mainContainer, cartFragment, "cart").hide(cartFragment)
                .add(R.id.mainContainer, homeFragment, "home")
                .commit();
        activeFragment = homeFragment;

        homeFragment.setOnCartClick(this::openCart);

        bottomNav.setOnNavSelected(new BottomNav.OnNavSelected() {
            @Override public void onHome() { switchFragment(homeFragment); }
            @Override public void onOrders() { switchFragment(ordersFragment); }
            @Override public void onProfile() { switchFragment(profileFragment); }
        });

        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    return;
                }
                if (activeFragment != homeFragment) {
                    switchFragment(homeFragment);
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            updateBottomNavVisibility();
        });
    }

    private void updateBottomNavVisibility() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (frag == null) frag = activeFragment;

        if (frag instanceof com.example.assignment.ui.cart.CartFragment || 
            frag instanceof com.example.assignment.ui.orders.OrderDetailFragment ||
            frag instanceof com.example.assignment.ui.orders.OrderSuccessFragment ||
            frag instanceof com.example.assignment.ui.product.ProductDetailFragment ||
            frag instanceof com.example.assignment.ui.checkout.CheckoutFragment) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    private void switchFragment(Fragment frag) {
        if (frag == activeFragment) return;
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment).show(frag).commit();
        activeFragment = frag;
        updateBottomNavVisibility();
        if (!(frag instanceof com.example.assignment.ui.cart.CartFragment)) {
            bottomNav.setSelected(
                frag == homeFragment ? 0 : frag == ordersFragment ? 1 : 2
            );
        }
    }

    public void openCart() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .hide(activeFragment)
                .add(R.id.mainContainer, new com.example.assignment.ui.cart.CartFragment(), "cart")
                .addToBackStack(null)
                .commit();
        // visibility handled by backstack listener
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activeFragment instanceof com.example.assignment.ui.cart.CartFragment) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void goToOrders(int orderId) {
        getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (orderId > 0) {
            OrderDetailFragment frag = new OrderDetailFragment();
            Bundle args = new Bundle();
            args.putInt("orderId", orderId);
            frag.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.mainContainer, frag)
                    .addToBackStack(null)
                    .commit();
        } else {
            switchFragment(ordersFragment);
        }
    }
}
