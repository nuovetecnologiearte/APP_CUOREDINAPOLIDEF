package net.cuoredinapoli.battito.cuoredinapoli.ui.concept;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.cuoredinapoli.R;

import net.cuoredinapoli.battito.cuoredinapoli.ui.frammenti.FragOpera;

public class ConceptFragment extends Fragment {


    private ConceptViewModel conceptViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        conceptViewModel =
                ViewModelProviders.of(this).get(ConceptViewModel.class);
        View root = inflater.inflate(R.layout.fragment_concept, container, false);

            return root;

    }






}