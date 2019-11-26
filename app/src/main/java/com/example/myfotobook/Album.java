package com.example.myfotobook;

import android.icu.text.DateFormat;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Album implements Serializable {

    private String idAlbum, chaveFoto, nomeFoto, descricaoFoto;
    private String criadoEm;
    //private Calendar cal =  Calendar.getInstance();

    public Album() {}

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Album(String idAlbum, String chaveFoto, String nomeFoto, String descricaoFoto) {
        this.idAlbum = idAlbum;
        this.chaveFoto = chaveFoto;
        this.nomeFoto = nomeFoto;
        this.descricaoFoto = descricaoFoto;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.criadoEm =
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRANCE).format(Calendar.getInstance().getTime());
        }
    }

    public String getidAlbum() {
        return idAlbum;
    }

    public void setidAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getChaveFoto() {
        return chaveFoto;
    }

    public void setChaveFoto(String chaveFoto) {
        this.chaveFoto = chaveFoto;
    }

    public String getNomeFoto() {
        return nomeFoto;
    }

    public void setNomeFoto(String nomeFoto) {
        this.nomeFoto = nomeFoto;
    }

    public String getDescricaoFoto() {
        return descricaoFoto;
    }

    public void setDescricaoFoto(String descricaoFoto) {
        this.descricaoFoto = descricaoFoto;
    }

    public String getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(String criadoEm) {
        this.criadoEm = criadoEm;
    }
}
