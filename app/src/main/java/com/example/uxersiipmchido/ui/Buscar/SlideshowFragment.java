package com.example.uxersiipmchido.ui.Buscar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.uxersiipmchido.ui.BD.Productos;
import com.example.uxersiipmchido.ui.BD.ProductosAdapter;
import com.example.uxersiipmchido.ui.BD.retroClient;
import com.example.uxersiipmchido.ui.BD.retroService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class SlideshowFragment extends Fragment {

        private ImageView scan, search;
        private EditText qrval;
        private ActivityResultLauncher<Intent> qrScanLauncher;
    retroService retro;
        String qrCodeValue;

    ListView listaCompra;
    ProductosAdapter adap;
    private FloatingActionButton fabBus;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

            qrval = view.findViewById(R.id.qrcode);
            scan = view.findViewById(R.id.escaner);
            search = view.findViewById(R.id.buscar);
            listaCompra=view.findViewById(R.id.compraL);
            fabBus = view.findViewById(R.id.fabBus);
            fabBus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSlideshowPopup();
                }
            });

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

                                    Bundle bundle = new Bundle();
                                    bundle.putString("qrCode", qrCodeValue);
                                    getParentFragmentManager().setFragmentResult("requestKey", bundle);
                                    Log.d("LOG", "el qr q se manda es: " + bundle);
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
            qrCodeValue = qrval.getText().toString();
            retro = retroClient.getRetrofitInstance().create(retroService.class);
            Call<JsonObject> call = retro.buscarQR(qrCodeValue);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.has("productos")) {
                                Gson gson = new Gson();
                                Type productListType = new TypeToken<List<Productos>>(){}.getType();
                                List<Productos> productos = gson.fromJson(jsonObject.get("productos"), productListType);
                                Log.d("TAG",productos.toString());

                                for (Productos producto : productos) {
                                    Log.d("TAG","Nombre del alimento: " +producto.getNomAlim());

                                    try {
                                        String fechaCadFormateada = producto.getFechaCad();  // La fecha ya está en formato String
                                        Log.d("DatosProducto", "Nombre: " + producto.getNomAlim() +
                                                ", Cantidad: " + producto.getCantidad() +
                                                ", Precio: " + producto.getPrecio() +
                                                ", Fecha de caducidad: " + fechaCadFormateada +  // Mostrar fecha directamente
                                                ", URL de imagen: " + producto.getUrlimg());;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("Fecha", "Error al obtener la fecha: " + producto.getFechaCad());
                                    }
                                }

                                adap = new ProductosAdapter(requireContext(), productos);
                                listaCompra.setAdapter(adap);
                            } else {
                                Toast.makeText(requireContext(), "Respuesta del servidor sin productos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }

            });
        }

    private void showSlideshowPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Finalizar Compra")
                .setMessage("¿Quieres finalizar la compra?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (qrCodeValue != null) {
                        Log.d("QRCode", "QR Code Value: " + qrCodeValue);
                        finalizarCompra(qrCodeValue);
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .create()
                .show();
    }
    private void finalizarCompra(String qrCode) {
        retro = retroClient.getRetrofitInstance().create(retroService.class);
        Call<JsonObject> call = retro.fcompra(qrCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("FinalizarCompra", "Respuesta del servidor: " + response.body().toString());
                    Toast.makeText(requireContext(), "Compra Finalizada", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("FinalizarCompra", "Error en la respuesta del servidor: " + response.errorBody());
                    Toast.makeText(requireContext(), "Error al finalizar la compra", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Fallo en la comunicación", Toast.LENGTH_SHORT).show();
            }
        });
    }
        private void escanearQR() {
            IntentIntegrator intentIntegrator = new IntentIntegrator(requireActivity());
            qrScanLauncher.launch(intentIntegrator.createScanIntent());
        }
    }