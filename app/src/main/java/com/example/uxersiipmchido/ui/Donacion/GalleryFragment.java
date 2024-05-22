package com.example.uxersiipmchido.ui.Donacion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uxersiipmchido.R;
import com.example.uxersiipmchido.databinding.FragmentGalleryBinding;
import com.example.uxersiipmchido.ui.BD.Productos;
import com.example.uxersiipmchido.ui.BD.ProductosAdapter;
import com.example.uxersiipmchido.ui.BD.retroClient;
import com.example.uxersiipmchido.ui.BD.retroService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryFragment extends Fragment {
    private ImageView scan, search;
    private EditText qrval;
    private ActivityResultLauncher<Intent> qrScanLauncher;
    retroService retro;
    String qrCodeValue;

    ListView listaCompra;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        qrval = view.findViewById(R.id.qrcode);
        scan = view.findViewById(R.id.escaner);
        search = view.findViewById(R.id.buscar);
        listaCompra=view.findViewById(R.id.compraL);


        // Initialize the ActivityResultLauncher
        qrScanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                        if (intentResult != null) {
                            if (intentResult.getContents() == null) {
                                Toast.makeText(requireContext(), "Operación cancelada", Toast.LENGTH_SHORT).show();
                            } else {
                                qrCodeValue = intentResult.getContents();
                                Toast.makeText(requireContext(), qrCodeValue, Toast.LENGTH_SHORT).show();
                                qrval.setText(qrCodeValue);
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error en escaneo", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanearQR();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarCode();
            }
        });

        return view;
    }
    private void buscarCode() {
        retro = retroClient.getRetrofitInstance().create(retroService.class);
        Call<JsonObject> call = retro.buscarQRDon(qrCodeValue);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }

        });
    }
    private void escanearQR() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(requireActivity());
        qrScanLauncher.launch(intentIntegrator.createScanIntent());
    }

}