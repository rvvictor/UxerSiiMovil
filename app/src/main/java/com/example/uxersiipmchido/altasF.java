package com.example.uxersiipmchido;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.uxersiipmchido.ui.BD.Productos;
import com.example.uxersiipmchido.ui.BD.retroService;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class altasF extends DialogFragment {
    EditText nombAli;
    EditText precio;
    EditText cantidad;
    ImageView imgProduc;
    EditText fech;
    Calendar calendar;
    int REQUEST_IMAGE_CAPTURE = 1;
    String fechaFormateada;
    Retrofit retrofit;
    static final String BASE_URL = "https://781hhnms-8000.usw3.devtunnels.ms/uxersiiPruebas/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_altas, container, false);
        String idPunto = getArguments().getString("id_producto");
        Log.d("TAG", "el valor que se recibe: " + idPunto);
        nombAli = view.findViewById(R.id.nombF);
        precio = view.findViewById(R.id.presF);
        cantidad = view.findViewById(R.id.cantF);
        imgProduc = view.findViewById(R.id.imgF);
        imgProduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        fech = view.findViewById(R.id.fechCadF);
        calendar = Calendar.getInstance();

        fech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Button btnEnviar = view.findViewById(R.id.guardarF);
        ImageButton btnCerrar = view.findViewById(R.id.cerrarF);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    Productos producto = new Productos();
                    producto.setNomAlim(nombAli.getText().toString());
                    producto.setCantidad(Integer.parseInt(cantidad.getText().toString()));
                    producto.setPrecio(Integer.parseInt(precio.getText().toString()));
                    producto.setFechaCad(fechaFormateada);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = ((BitmapDrawable) imgProduc.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imagenBytes = stream.toByteArray();
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imagenBytes);
                    MultipartBody.Part imagenParte = MultipartBody.Part.createFormData("imagen", "alimento.jpg", requestFile);
                    retroService alimentoService = retrofit.create(retroService.class);
                    RequestBody nomAlimPart = RequestBody.create(MediaType.parse("text/plain"), producto.getNomAlim());
                    RequestBody cantidadPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(producto.getCantidad()));
                    RequestBody precioPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(producto.getPrecio()));
                    RequestBody fechaCadPart = RequestBody.create(MediaType.parse("text/plain"), producto.getFechaCad());
                    RequestBody idpunto = RequestBody.create(MediaType.parse("text/plain"), idPunto);
                    Call<Productos> call = alimentoService.crearProducto(nomAlimPart, cantidadPart, precioPart, fechaCadPart, idpunto, imagenParte);
                    call.enqueue(new Callback<Productos>() {
                        @Override
                        public void onResponse(Call<Productos> call, Response<Productos> response) {
                            if (response.isSuccessful()) {
                                Productos createdProducto = response.body();
                                Toast.makeText(requireContext(), "Producto agregado", Toast.LENGTH_SHORT).show();
                                dismiss();
                                limpito();
                            } else {
                                Toast.makeText(requireContext(), "El producto no se pudo agregar correctamente", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "el valor que se intento mandar: " + idPunto);
                            }
                        }

                        @Override
                        public void onFailure(Call<Productos> call, Throwable t) {
                            Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private boolean validarCampos() {
        if (nombAli.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el nombre del producto", Toast.LENGTH_SHORT).show();
            nombAli.requestFocus();
            return false;
        }
        if (precio.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el precio del producto", Toast.LENGTH_SHORT).show();
            precio.requestFocus();
            return false;
        }
        if (cantidad.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese la cantidad del producto", Toast.LENGTH_SHORT).show();
            cantidad.requestFocus();
            return false;
        }
        if (fech.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese la fecha de caducidad", Toast.LENGTH_SHORT).show();
            fech.requestFocus();
            return false;
        }
        Drawable fotodefault = requireContext().getDrawable(R.drawable.camara);
        Drawable fotoactual = imgProduc.getDrawable();
        if (fotoactual != null && fotodefault != null) {
            Bitmap bitmapDefault = ((BitmapDrawable) fotodefault).getBitmap();
            Bitmap bitmapActual = ((BitmapDrawable) fotoactual).getBitmap();

            if (bitmapDefault.sameAs(bitmapActual)) {
                Toast.makeText(requireContext(), "Debe tomar una foto del producto", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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
                        fech.setText(fechaFormateada);
                    }
                }, year, mes, dia);
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.containsKey("data")) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        Bitmap redimg = Bitmap.createScaledBitmap(imageBitmap, 300, 300, true);
                        imgProduc.setImageBitmap(redimg);
                    } else {
                        Toast.makeText(requireContext(), "No se pudo capturar la imagen", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener la imagen capturada", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(requireContext(), "Se canceló la operación", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(requireContext(), "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    public void limpito() {
        nombAli.setText("");
        precio.setText("");
        cantidad.setText("");
        fech.setText("");
        imgProduc.setImageResource(R.drawable.camara);
    }

    public static altasF newInstance(String idPunto) {
        altasF fragment = new altasF();
        Bundle args = new Bundle();
        args.putString("id_producto", idPunto);
        fragment.setArguments(args);
        return fragment;
    }
}
