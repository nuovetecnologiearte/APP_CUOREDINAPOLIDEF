package net.cuoredinapoli.battito.cuoredinapoli.ui.flusso;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.cuoredinapoli.R;

import java.util.Locale;

public class FlussoFragment extends Fragment {

    private FlussoViewModel flussoViewModel;

    public WebView myWebViewinsta;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        flussoViewModel =
                ViewModelProviders.of(this).get(FlussoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_flusso, container, false);

        myWebViewinsta = root.findViewById(R.id.cuoreinsta);
        WebSettings webSettings = myWebViewinsta.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if  (Locale.getDefault().getLanguage().contentEquals("it")){
            myWebViewinsta.loadUrl("https://cuoredinapoli.net/cdn/solocuore_app.html");
        }
        else {
            myWebViewinsta.loadUrl("https://cuoredinapoli.net/cdn/solocuore_app_en.html");
        }

        myWebViewinsta.setWebViewClient(new WebViewClient() {
            @Override public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                myWebViewinsta.loadUrl("file:///android_asset/error_flusso.html");
            } });

        Button shareInstagram = root.findViewById(R.id.shareInstagram);
        shareInstagram.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (launchIntent != null)
                {
                    try
                    {
                        startActivity(launchIntent);
                    }
                    catch (ActivityNotFoundException ex) // in case Instagram not installed in your device
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
        return root;
    }
}