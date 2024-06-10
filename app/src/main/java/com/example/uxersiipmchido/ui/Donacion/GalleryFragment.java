package com.example.uxersiipmchido.ui.Donacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import com.example.uxersiipmchido.MainActivity;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GalleryFragment extends Fragment {
    ImageView scan, search;
    TextView txt;
    EditText qrval, nomD, cantD, fechD;
    Calendar calendar;
    String fechaFormateada;
    Button addDon, finD;
    private ActivityResultLauncher<Intent> qrScanLauncher;
    Retrofit retrofit;
    String qrCodeValue;
    retroService retro;
    static final String BASE_URL="https://781hhnms-8000.usw3.devtunnels.ms/uxersiiPruebas/" ;

    String idPunto;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        if (getActivity() != null){
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("id_producto")){
                idPunto = intent.getStringExtra("id_producto");
            }
            }
        qrval = view.findViewById(R.id.qrcode);
        txt=view.findViewById(R.id.altasDona);
        scan = view.findViewById(R.id.escaner);
        search = view.findViewById(R.id.buscar);
        nomD=view.findViewById(R.id.nombDona);
        cantD=view.findViewById(R.id.cantDon);
        fechD=view.findViewById(R.id.dateDon);
        addDon=view.findViewById(R.id.añadirdon);
        addDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                altasDon();
            }
        });

        calendar=Calendar.getInstance();

        fechD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

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

        finD=view.findViewById(R.id.finDon);
        finD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Finalizar Donación")
                        .setMessage("¿Quieres finalizar la Donación?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            if (qrCodeValue != null) {
                                Log.d("QRCode", "QR Code Value: " + qrCodeValue);
                                finalizarDon(qrCodeValue);
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });
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
        qrCodeValue=qrval.getText().toString();
        retro = retroClient.getRetrofitInstance().create(retroService.class);
        Call<JsonObject> call = retro.buscarQRDon(qrCodeValue);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    txt.setVisibility(View.VISIBLE);
                    nomD.setVisibility(View.VISIBLE);
                    cantD.setVisibility(View.VISIBLE);
                    fechD.setVisibility(View.VISIBLE);
                    addDon.setVisibility(View.VISIBLE);
                    finD.setVisibility(View.VISIBLE);
                } else {
                    // Aquí manejas el caso en que la respuesta no es exitosa
                    Toast.makeText(requireContext(), "Codigo Erroneo o inexistente" , Toast.LENGTH_SHORT).show();
                }
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
    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();
        int year = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        Date fechaSeleccionada = selectedDate.getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        fechaFormateada = sdf.format(fechaSeleccionada);
                        fechD.setText(fechaFormateada);
                    }
                }, year, mes, dia);
        datePickerDialog.show();
    }
    public void altasDon(){
        Productos producto = new Productos();
        producto.setNomAlimDona(nomD.getText().toString());
        producto.setCantidadDona(Integer.parseInt(cantD.getText().toString()));
        producto.setFechaCadDona(fechaFormateada);
        retroService donService = retrofit.create(retroService.class);
        retro = retroClient.getRetrofitInstance().create(retroService.class);
        RequestBody nomAlimPart = RequestBody.create(MediaType.parse("text/plain"), producto.getNomAlimDona());
        RequestBody cantidadPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(producto.getCantidadDona()));
        RequestBody fechaCadPart = RequestBody.create(MediaType.parse("text/plain"), producto.getFechaCadDona());
        RequestBody idpunto = RequestBody.create(MediaType.parse("text/plain"), idPunto);

        Log.d("altasDon", "nomAlim: " + producto.getNomAlimDona());
        Log.d("altasDon", "cantidad: " + producto.getCantidadDona());
        Log.d("altasDon", "fechaCad: " + producto.getFechaCadDona());
        Log.d("altasDon", "idPunto: " + idPunto);
        Log.d("altasDon", "qrCodeValue: " + qrCodeValue);


        Call<Productos> call = retro.crearProductoDon(nomAlimPart,cantidadPart,fechaCadPart,idpunto, qrCodeValue);
        call.enqueue(new Callback<Productos>() {
            @Override
            public void onResponse(Call<Productos> call, Response<Productos> response) {
                if (response.isSuccessful()) {
                    Productos createdProducto = response.body();
                    Toast.makeText(requireContext(), "Producto agregado", Toast.LENGTH_SHORT).show();
                    limpito();
                }else{
                    Toast.makeText(requireContext(), "El producto no se pudó agregar correctamente",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Productos> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void finalizarDon(String qrCode){
        retro = retroClient.getRetrofitInstance().create(retroService.class);
        Call<JsonObject> call = retro.fdona(qrCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    qrval.setText("");
                    nomD.setText("");
                    fechD.setText("");
                    cantD.setText("");
                    txt.setVisibility(View.GONE);
                    nomD.setVisibility(View.GONE);
                    cantD.setVisibility(View.GONE);
                    fechD.setVisibility(View.GONE);
                    addDon.setVisibility(View.GONE);
                    finD.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Donación Finalizada", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Fallo en la comunicación", Toast.LENGTH_SHORT).show();
            }

        });

    }
    public void limpito(){
        nomD.setText("");
        fechD.setText("");
        cantD.setText("");
    }
}