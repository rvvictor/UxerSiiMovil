package com.example.uxersiipmchido.ui.BD;

import com.google.gson.annotations.SerializedName;

public class Productos {
    @SerializedName("nomb_alim_dona")
    private String nomAlimDona;

    @SerializedName("catn_adon")
    private int cantidadDona;

    @SerializedName("fecha_cad_dona")
    private String fechaCadDona;

    public String getNomAlimDona() {
        return nomAlimDona;
    }

    public void setNomAlimDona(String nomAlimDona) {
        this.nomAlimDona = nomAlimDona;
    }

    public int getCantidadDona() {
        return cantidadDona;
    }

    public void setCantidadDona(int cantidadDona) {
        this.cantidadDona = cantidadDona;
    }

    public String getFechaCadDona() {
        return fechaCadDona;
    }

    public void setFechaCadDona(String fechaCadDona) {
        this.fechaCadDona = fechaCadDona;
    }

    @SerializedName("nomb_alim")
    private String nomAlim;
    @SerializedName("cantidad")
    private int cantidad;
    @SerializedName("id")
    private String idP;

    public String getIdP() {
        return idP;
    }

    public void setIdP(String idP) {
        this.idP = idP;
    }

    @SerializedName("fecha_cad")
    private String fechaCad;
    @SerializedName("costo")
    private double precio;
    @SerializedName("imagen")
    private String urlimg;

    @SerializedName("fruta")
    private String fruta;

    public String getFruta() {
        return fruta;
    }

    public void setFruta(String fruta) {
        this.fruta = fruta;
    }

    public String getUrlimg() {
        return urlimg;
    }

    public void setUrlimg(String urlimg) {
        this.urlimg = urlimg;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getNomAlim() {
        return nomAlim;
    }

    public void setNomAlim(String nomAlim) {
        this.nomAlim = nomAlim;
    }

    public String getFechaCad() {
        return fechaCad;
    }

    public void setFechaCad(String fechaCad) {
        this.fechaCad = fechaCad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Productos() {
    }


    public Productos(String nomAlim, int cantidad, String fechaCad, int precio, String urlimg) {
        this.nomAlim = nomAlim;
        this.cantidad = cantidad;
        this.fechaCad = fechaCad;
        this.precio = precio;
        this.urlimg = urlimg;
    }


}
