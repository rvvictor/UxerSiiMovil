package com.example.uxersiipmchido.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uxersiipmchido.R;
import com.example.uxersiipmchido.databinding.FragmentHomeBinding;
import com.example.uxersiipmchido.ui.BD.Productos;
import com.example.uxersiipmchido.ui.BD.ProductosAdapter;
import com.example.uxersiipmchido.ui.BD.retroClient;
import com.example.uxersiipmchido.ui.BD.retroService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    ListView lista;
    ProductosAdapter adap;
    retroService retro;
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        Productos productos = new Productos();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        String idPunto = getArguments() != null ? getArguments().getString("id_producto") : "";
        Log.d("TAG","el valor que se recibe en la lista: " + idPunto);
        lista = view.findViewById(R.id.inv);
        retro = retroClient.getRetrofitInstance().create(retroService.class);
        Call<JsonObject> call = retro.obtenerProductos(idPunto);
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

                            for (Productos producto : productos) {
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
                            lista.setAdapter(adap);
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
        return view;
    }
    public static HomeFragment newInstance(String idPunto) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("id_producto", idPunto);
        fragment.setArguments(args);
        return fragment;
    }
}