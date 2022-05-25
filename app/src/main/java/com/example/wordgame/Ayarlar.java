package com.example.wordgame;

import java.io.Serializable;

public class Ayarlar implements Serializable {
    private String key;
    private String k_adi;
    private String k_heart;
    private byte k_image;

    public Ayarlar(String s, String toString){}

    public Ayarlar(String k_adi, String k_heart, byte k_image) {
        this.k_adi = k_adi;
        this.k_image = k_image;
        this.k_heart = k_heart;
    }

    public String getK_adi() {
        return k_adi;
    }

    public void setK_adi(String k_adi) {
        this.k_adi = k_adi;
    }

    public String getK_heart() {
        return k_heart;
    }

    public void setK_heart(String k_heart) {
        this.k_heart = k_heart;
    }

    public String getkey() {
        return key;
    }

    public void setkey(String key) {this.key = key;
    }
    public byte getK_image() { return k_image; }

    public void setK_image(byte k_image) { this.k_image = k_image; }

  /*  public byte getK_image() {
        return k_image;
    }

    public void setK_image(byte k_image) {
        this.k_image = k_image;
    }*/
}