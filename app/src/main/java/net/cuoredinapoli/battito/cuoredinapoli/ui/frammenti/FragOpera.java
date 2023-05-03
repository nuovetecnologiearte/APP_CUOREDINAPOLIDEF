package net.cuoredinapoli.battito.cuoredinapoli.ui.frammenti;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.cuoredinapoli.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.cuoredinapoli.battito.cuoredinapoli.ArComponentActivity;

public class FragOpera extends Fragment {

    private FragOperaViewModel fragViewModel;
    FloatingActionButton mFabCamera;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fragViewModel =
                ViewModelProviders.of(this).get(FragOperaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_frag, container, false);

        mFabCamera = root.findViewById(R.id.fab_camera);

        mFabCamera.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Apertura AR", Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(getContext(), ArComponentActivity.class);
              startActivity(intent);
        });


        return root;
    }





}
