package com.example.uxersiipmchido.ui.BD;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface retroService {

    @GET("api/v1/alimentos/{id_punto}/")
    Call<JsonObject> obtenerProductos(@Path("id_punto")String idPunto);
    @Multipart
    @POST("api/v1/alimentosp/")
    Call<Productos> crearProducto(
            @Part("nomAlim") RequestBody nomAlim,
            @Part("cantidad") RequestBody cantidad,
            @Part("costo") RequestBody precio,
            @Part("fechaCad") RequestBody fechaCad,
            @Part("id_punto") RequestBody idpunto,
            @Part MultipartBody.Part imagen
    );

    @GET("api/v1/valcod/{codigo}/")
    Call<JsonObject> validarCodigo(@Path("codigo") String codigo);

    @GET("api/v1/getalimqr/{qr}/")
    Call<JsonObject> buscarQR(@Path("qr") String qr);
    @GET("api/v1/getdonqr/{qr}/")
    Call<JsonObject> buscarQRDon(@Path("qr") String qr);

    @GET("api/v1/fcompra/{qr}/")
    Call<JsonObject> fcompra(@Path("qr") String qr);

    @GET("api/v1/fdona/{qr}/")
    Call<JsonObject> fdona(@Path("qr") String qr);

    @Multipart
    @POST("api/v1/postalimentosdon/{qr}/")
    Call<Productos> crearProductoDon(
            @Part("nomb_alim_dona") RequestBody nomb_alim_dona,
            @Part("catn_adon") RequestBody catn_adon,
            @Part("fecha_cad_dona") RequestBody fecha_cad_dona,
            @Part("id_punto") RequestBody id_punto,
            @Path("qr") String qr
    );
}
