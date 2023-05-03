package net.cuoredinapoli.battito.cuoredinapoli.ui.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cuoredinapoli.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    HashMap<Marker, Drawable> hashMarkImg = new HashMap<>();
    public MapFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = mView.findViewById(R.id.navigation_map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

        }

    }

    public String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker_cuore);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(b, 84, 129, false);
        //rivenditori
        BitmapDrawable bitmapdrawr=(BitmapDrawable)getResources().getDrawable(R.drawable.marker_shop);
        Bitmap r=bitmapdrawr.getBitmap();
        Bitmap markerr = Bitmap.createScaledBitmap(r, 84, 129, false);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        String myJson = inputStreamToString(mMapView.getResources().openRawResource(R.raw.insegne));
        Insegne arrayInsegne = new Gson().fromJson(myJson, Insegne.class);

        for (int i = 0; i<arrayInsegne.insegne.size(); i++){
            Marker m = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(arrayInsegne.insegne.get(i)
                            .lat,arrayInsegne.insegne.get(i).lon))
                            .title(arrayInsegne.insegne.get(i).title)
                            .snippet(arrayInsegne.insegne.get(i).snippet)
                            .icon(BitmapDescriptorFactory.fromBitmap(marker)));

            int resourceId = getResources().getIdentifier(arrayInsegne.insegne.get(i).image, "drawable", getContext().getPackageName());
            Drawable drawable = getResources().getDrawable(resourceId);
            hashMarkImg.put(m, drawable);
        }



        CameraPosition NTA = CameraPosition.builder().target(new LatLng(40.855937,14.256437)).zoom(14).bearing(0).tilt(0).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(NTA));


        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapFragment.this, hashMarkImg );
        googleMap.setInfoWindowAdapter(adapter);

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

               String myLatitude = String.valueOf(marker.getPosition().latitude);
                String myLongitude = String.valueOf(marker.getPosition().longitude);
                String labelLocation = marker.getTitle();

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("geo:<" + myLatitude + ">,<" + myLongitude + ">?q=<" + myLatitude + ">,<" + myLongitude + ">(" + labelLocation + ")"));
                startActivity(intent);


            }
        });


    }

}
