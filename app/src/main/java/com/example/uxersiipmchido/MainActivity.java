package com.example.uxersiipmchido;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentResultListener;

import com.example.uxersiipmchido.ui.BD.retroClient;
import com.example.uxersiipmchido.ui.BD.retroService;
import com.example.uxersiipmchido.ui.index.index;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uxersiipmchido.databinding.ActivityMainBinding;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FloatingActionButton fab;
    private NavController navController;
    retroService retro;
    private String qrCode;
    Button botonia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        botonia = findViewById(R.id.button);
        botonia.setOnClickListener(this);

        fab = binding.appBarMain.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClick();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_lista, R.id.nav_donacion, R.id.nav_buscar)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_salir) {
                    Intent intent = new Intent(MainActivity.this, index.class);
                    startActivity(intent);
                    return true;
                } else {
                    boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                    if (handled) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    return handled;
                }
            }
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            if (destinationId == R.id.nav_buscar) {
                fab.hide();
            } else if (destinationId == R.id.nav_donacion) {
                fab.hide();
            } else {
                fab.setImageResource(R.drawable.anadir);
                fab.show();
                fab.setOnClickListener(view -> handleFabClick());
            }
        });

    }

    private void handleFabClick() {
        String idPunto = getIntent().getStringExtra("id_producto");
        altasF altasf = altasF.newInstance(idPunto);
        altasf.show(getSupportFragmentManager(), "formulario_dialog");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        String cadena = ((Button)v).getText().toString();

        if (cadena.equals("ia")){
            Intent intent = new Intent(this, iapruebas.class);
            startActivity(intent);
        }
    }
}
