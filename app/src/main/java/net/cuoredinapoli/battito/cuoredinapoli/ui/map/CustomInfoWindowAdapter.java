package net.cuoredinapoli.battito.cuoredinapoli.ui.map;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cuoredinapoli.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


    private MapFragment context;
    private HashMap<Marker, Drawable> hashMarkImg;

    public CustomInfoWindowAdapter(MapFragment context, HashMap hashMarkImg){
        this.context = context;
        this.hashMarkImg = hashMarkImg;
    }

    @Override
    public View getInfoWindow (Marker marker) {
        /*final Dialog dialog1 = new Dialog(context.getContext());
        Window window = dialog1.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog1.setContentView(view);
        //dialog1.show();*/
        View view = context.getLayoutInflater().inflate(R.layout.fragment_infoview, null);


        TextView tvTitle = view.findViewById(R.id.intestazione);
        //TextView snippet = view.findViewById(R.id.snippet);
        ImageView anteprima = view.findViewById(R.id.anteprima);

         tvTitle.setText(marker.getTitle());
        //snippet.setText(marker.getSnippet());
        anteprima.setImageDrawable(hashMarkImg.get(marker));

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {

      /*  LayoutInflater inflater = LayoutInflater.from(MapFragment);
        final Dialog dialog1 = new Dialog(MapFragment);
        Window window = dialog1.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM; // Here you can set window top or bottom
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        View view1 = inflater.inflate(R.layout.fragment_infoview, null);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog1.setContentView(view1);
        dialog1.show();


        View view = context.getLayoutInflater().inflate(R.layout.fragment_infoview, null);
        TextView tvTitle = view.findViewById(R.id.intestazione);
        TextView snippet = view.findViewById(R.id.snippet);

        tvTitle.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());*/

        return null;
    }

}

