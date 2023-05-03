package net.cuoredinapoli.battito.cuoredinapoli.ui.map;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Insegne {

    @SerializedName("insegne")
    public ArrayList<Insegna> insegne;

        static public class Insegna {
            @SerializedName("title")
            public String title;

            @SerializedName("lat")
            public double lat;

            @SerializedName("lon")
            public double lon;

            @SerializedName("snippet")
            public String snippet;

            @SerializedName("image")
            public String image;


        }



}