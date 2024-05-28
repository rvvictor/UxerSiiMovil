package com.example.uxersiipmchido.ui.index;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.uxersiipmchido.MainActivity;
import com.example.uxersiipmchido.R;
import com.example.uxersiipmchido.altasF;
import com.example.uxersiipmchido.ui.BD.Productos;
import com.example.uxersiipmchido.ui.BD.retroClient;
import com.example.uxersiipmchido.ui.BD.retroService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class index extends AppCompatActivity {

    Button iniciarB;
    EditText cod;

    String mensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);



        iniciarB = findViewById(R.id.iniciarSesion);
        cod=findViewById(R.id.cod);
        iniciarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = cod.getText().toString();
                validarcod(codigo);

            }
        });
    }
    private void validarcod(String codigo){
        retroService retro = retroClient.getRetrofitInstance().create(retroService.class);
        Call<JsonObject> call=retro.validarCodigo(codigo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject.has("id_punto")){
                        mensaje = jsonObject.get("id_punto").getAsString();
                    }
                    String idPunto = mensaje;
                    Intent intent = new Intent(index.this, MainActivity.class);
                    intent.putExtra("id_producto", idPunto);
                    startActivity(intent);
                    Log.d("TAG", "el valor que se manda: " + idPunto);
                    finish();
                } else {
                    Toast.makeText(index.this, "Código inválido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(index.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

}