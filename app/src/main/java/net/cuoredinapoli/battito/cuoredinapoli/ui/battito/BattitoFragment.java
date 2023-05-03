package net.cuoredinapoli.battito.cuoredinapoli.ui.battito;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.cuoredinapoli.R;

import java.util.Locale;

public class BattitoFragment extends Fragment {

    private BattitoViewModel battitoViewModel;

    public WebView myWebView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        battitoViewModel =
                ViewModelProviders.of(this).get(BattitoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_battito, container, false);

        myWebView = root.findViewById(R.id.battitoWeb);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


       /* myWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView webView, WebResourceRequest request, WebResourceError error) {
                // if (finalCount == 0) {
                super.onReceivedError(webView, request, error);
                String errorHtml = "<html><body><h2>找不到网页</h2></body></html>";
                myWebView.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
                //}
            } });

*/




         if (isNetworkAvailable()) {

             if (Locale.getDefault().getLanguage().contentEquals("it")) {
                 myWebView.loadUrl("https://battito.cuoredinapoli.net");

             } else {
                 myWebView.loadUrl("https://battito.cuoredinapoli.net/en.html");

             }


        } else {
            myWebView.loadUrl("file:///android_asset/error.html");
        }


        return root;


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}