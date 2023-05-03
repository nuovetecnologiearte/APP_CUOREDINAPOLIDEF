package net.cuoredinapoli.battito.cuoredinapoli;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.cuoredinapoli.R;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.FrameTime;

import net.cuoredinapoli.battito.cuoredinapoli.tools.CameraPermissionHelper;
import net.cuoredinapoli.battito.cuoredinapoli.tools.FullScreenHelper;
import net.cuoredinapoli.battito.cuoredinapoli.tools.SnackbarHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class ArComponentActivity extends AppCompatActivity {

    private static final String TAG = ArComponentActivity.class.getSimpleName();

    //rendering
    private ArSceneView arSceneView;

    private boolean installRequested;

    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();

    private boolean shouldConfigureSession = false;
    private boolean enableAutoFocus;
    ImageView backArButton;
    ImageButton infobutton;
    Dialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_component);
        myDialog = new Dialog(this);
        arSceneView = findViewById(R.id.surfaceview);
        installRequested = false;

        initializeSceneView();
        backArButton = this.findViewById(R.id.backButtonAR);
        backArButton.setOnClickListener(v -> {
            finish();
        });
    }
        public void ShowPopUp (View v) {


            ImageButton closeinfo;
            myDialog.setContentView(R.layout.popupinfo);
            closeinfo = (ImageButton) myDialog.findViewById(R.id.closeinfo);
            closeinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            myDialog.show();
            //infobutton = this.findViewById(R.id.infoMultimedia);
            //infobutton.setOnClickListener(v -> {



        }

    @Override
    protected void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                session = new Session(/* context = */ this);
            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (Exception e) {
                message = "This device does not support AR";
                exception = e;
            }

            if (message != null) {
                messageSnackbarHelper.showError(this, message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }

            shouldConfigureSession = true;
        }

        if(shouldConfigureSession){
            configureSession();
            shouldConfigureSession = false;
            arSceneView.setupSession(session);
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session.resume();
            arSceneView.resume();
        } catch (CameraNotAvailableException e) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle this properly by showing a message and recreate the
            // session at the next iteration.
            messageSnackbarHelper.showError(this, "Camera not available. Please restart the app.");
            session = null;
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            arSceneView.pause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(
                    this, "Camera permissions are needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    private void initializeSceneView() {
        //arSceneView.getScene().setOnUpdateListener((this::onUpdateFrame));
        /*arSceneView.getScene().addOnUpdateListener(frameTime -> {
            Frame frame = arSceneView.getArFrame();
            Collection<AugmentedImage> updatedAugmentedImages =
                    frame.getUpdatedTrackables(AugmentedImage.class);

            for (AugmentedImage augmentedImage : updatedAugmentedImages) {
                if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
                    // Check camera image matches our reference image
                    if (augmentedImage.getName().equals("sintesi")) {
                        // AugmentedImageNode node = new AugmentedImageNode(this, "model.sfb");
                        // node.setImage(augmentedImage);
                        // arSceneView.getScene().addChild(node);
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=l_p_eh81YnQ");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }

                    if (augmentedImage.getName().equals("menhere")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=q8B2EPWz_Pc");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }

                    if (augmentedImage.getName().equals("tuttominuto")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=_qtQ4Lggxj8");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("conceptstore")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=u_9iGYjwMkM");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("scorie")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=ch-rYtE9C9c");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("uomoglobale")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=cpQ4n1qGhIo");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("chi")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=5UdU9edVKM4");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("uomodue")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=zB8nNUmODHI");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("uomoduedue")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=fSAnaJ6YYjk");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("festivalunoa")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=zykoYhN_KoI");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("festivalunob")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=stDeh7NEupM");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("festivaldue")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=j1F32UfS7Yg");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("festaincanto")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=aA0BzA81plw");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("hashtagcon")) {
                        Uri uriUrl = Uri.parse("http://www.nuovetecnologiedellarte.it/portfolio/con-futuro-remoto/");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("sdtnslsc")) {
                        Uri uriUrl = Uri.parse("https://www.YouTube.com/watch?v=y_INI7uKQD4");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("zzz")) {
                        Uri uriUrl = Uri.parse("https://www.YouTube.com/watch?v=JZVligbXDLs");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("cuoreanima")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=yoHYjZWSqWQ");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("ivoltideiquartieri")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=mIFuiTUbbM0");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("lafinedelmondo")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=pUyerXucWuA");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("tantieroi")) {
                        Uri uriUrl = Uri.parse("https://cuoredinapoli.net/beyond-the-lab/");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("cuoredinapoliuno")) {
                        Uri uriUrl = Uri.parse("https://cuoredinapoli.net/portfolio/festivaldelbacio-cuoredinapoli-2014/#fesbac2014");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("cuoredinapolidue")) {
                        Uri uriUrl = Uri.parse("https://cuoredinapoli.net/portfolio/festivaldelbacio-cuoredinapoli-2015/#fesbac2015");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("cuoredinapolitre")) {
                        Uri uriUrl = Uri.parse("https://cuoredinapoli.net/portfolio/cuore-di-napoli-porta-capuana-2017/#porcap2017");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("cuoredinapoliquattro")) {
                        Uri uriUrl = Uri.parse("https://cuoredinapoli.net/portfolio/cuoredinapoli-quartierispagnoli-2018/#quaspa2018");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("cuoredinapolicinque")) {
                        Uri uriUrl = Uri.parse("https://cuoredinapoli.net/portfolio/cuoredinapoli-2019-quartieri-spagnoli/#quaspa2019");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("tedx")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=2LBIn8IAems");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("magnificaastrazione")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=cgE9Eazby4w");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("pattoformativo")) {
                        Uri uriUrl = Uri.parse("https://www.napolistories.it/2016/11/30/nuove-tecnologie-larte/");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("mediaintegrati")) {
                        Uri uriUrl = Uri.parse("http://www.mediaintegrati.it/");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("artiseverywhere")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=1BxYPNCpRb4");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("medintcorna")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=fRRAL6yeo3Y");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("medintcavallo")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=3ohvMzFviLk");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("medintsciamano")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=BkA0YuMD2N8");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                    if (augmentedImage.getName().equals("medintlorenzo")) {
                        Uri uriUrl = Uri.parse("https://www.youtube.com/watch?v=4daMozDR8WY");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                }
            }l
        });*/
        arSceneView.getScene().addOnUpdateListener(this::onUpdateFrame);
    }


    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arSceneView.getArFrame();
        Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
                String video = "https://www.youtube.com/watch?v=_bKHxpzEKpI"; //dichiaro stringa per i link
                // Check camera image matches our reference image
                if (augmentedImage.getName().equals("sintesi")) {
                    video = "https://www.youtube.com/watch?v=l_p_eh81YnQ";
                }
                else if (augmentedImage.getName().equals("menhere")) {
                    video = "https://www.youtube.com/watch?v=q8B2EPWz_Pc";
                }
                else if (augmentedImage.getName().equals("tuttominuto")) {
                    video = "https://www.youtube.com/watch?v=_qtQ4Lggxj8";
                }
                else if (augmentedImage.getName().equals("conceptstore")) {
                    video = "https://www.youtube.com/watch?v=_qtQ4Lggxj8";
                }
                else if (augmentedImage.getName().equals("scorie")) {
                    video = "https://www.youtube.com/watch?v=ch-rYtE9C9c";
                }
                else if (augmentedImage.getName().equals("uomoglobale")) {
                    video = "https://www.youtube.com/watch?v=cpQ4n1qGhIo";
                }
                else if (augmentedImage.getName().equals("chi")) {
                    video = "https://www.youtube.com/watch?v=5UdU9edVKM4";
                }
                else if (augmentedImage.getName().equals("uomodue")) {
                    video = "https://www.youtube.com/watch?v=zB8nNUmODHI";
                }
                else if (augmentedImage.getName().equals("uomoduedue")) {
                    video = "https://www.youtube.com/watch?v=fSAnaJ6YYjk";
                }
                else if (augmentedImage.getName().equals("festivalunoa")) {
                    video = "https://www.youtube.com/watch?v=zykoYhN_KoI";
                }
                else if (augmentedImage.getName().equals("festivalunob")) {
                    video = "https://www.youtube.com/watch?v=stDeh7NEupM";
                }
                else if (augmentedImage.getName().equals("festivaldue")) {
                    video = "https://www.youtube.com/watch?v=j1F32UfS7Yg";
                }
                else if (augmentedImage.getName().equals("festaincanto")) {
                    video = "https://www.youtube.com/watch?v=aA0BzA81plw";
                }
                else if (augmentedImage.getName().equals("hashtagcon")) {
                    video = "http://www.nuovetecnologiedellarte.it/portfolio/con-futuro-remoto/";
                }
                else if (augmentedImage.getName().equals("sdtnslsc")) {
                    video = "https://www.YouTube.com/watch?v=y_INI7uKQD4";
                }
               else if (augmentedImage.getName().equals("zzz")) {
                    video = "https://www.YouTube.com/watch?v=JZVligbXDLs";
                }
                else if (augmentedImage.getName().equals("cuoreanima")) {
                    video = "https://www.youtube.com/watch?v=yoHYjZWSqWQ";
                }
               else if (augmentedImage.getName().equals("ivoltideiquartieri")) {
                    video = "https://www.youtube.com/watch?v=mIFuiTUbbM0";
                }
                else if (augmentedImage.getName().equals("lafinedelmondo")) {
                    video = "https://www.youtube.com/watch?v=pUyerXucWuA";
                }
                else if (augmentedImage.getName().equals("tantieroi")) {
                    video = "https://cuoredinapoli.net/beyond-the-lab/";
                }
                else if (augmentedImage.getName().equals("cuoredinapoliuno")) {
                    video = "https://cuoredinapoli.net/portfolio/festivaldelbacio-cuoredinapoli-2014/#fesbac2014";
                }
                else if (augmentedImage.getName().equals("cuoredinapolidue")) {
                    video = "https://cuoredinapoli.net/portfolio/festivaldelbacio-cuoredinapoli-2015/#fesbac2015";
                }
                else if (augmentedImage.getName().equals("cuoredinapolitre")) {
                    video = "https://cuoredinapoli.net/portfolio/cuore-di-napoli-porta-capuana-2017/#porcap2017";
                }
                else if (augmentedImage.getName().equals("cuoredinapoliquattro")) {
                    video = "https://cuoredinapoli.net/portfolio/cuoredinapoli-quartierispagnoli-2018/#quaspa2018";
                }
                else if (augmentedImage.getName().equals("cuoredinapolicinque")) {
                    video = "https://cuoredinapoli.net/portfolio/cuoredinapoli-2019-quartieri-spagnoli/#quaspa2019";
                }
               else if (augmentedImage.getName().equals("tedx")) {
                    video = "https://www.youtube.com/watch?v=2LBIn8IAems";
                }
                else if (augmentedImage.getName().equals("magnificaastrazione")) {
                    video = "https://www.youtube.com/watch?v=cgE9Eazby4w";
                }
                else if (augmentedImage.getName().equals("pattoformativo")) {
                    video = "https://www.napolistories.it/2016/11/30/nuove-tecnologie-larte/";
                }
                else if (augmentedImage.getName().equals("mediaintegrati")) {
                    video = "http://www.mediaintegrati.it/";
                }
                else if (augmentedImage.getName().equals("artiseverywhere")) {
                    video = "https://www.youtube.com/watch?v=1BxYPNCpRb4";
                }
                else if (augmentedImage.getName().equals("medintcorna")) {
                    video = "https://www.youtube.com/watch?v=fRRAL6yeo3Y";
                }
                else if (augmentedImage.getName().equals("medintcavallo")) {
                    video = "https://www.youtube.com/watch?v=3ohvMzFviLk";
                }
                else if (augmentedImage.getName().equals("medintsciamano")) {
                    video = "https://www.youtube.com/watch?v=BkA0YuMD2N8";
                }
                else if (augmentedImage.getName().equals("medintlorenzo")) {
                    video = "https://www.youtube.com/watch?v=4daMozDR8WY";
                }

                Uri uriUrl = Uri.parse(video);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                finish();
            }

        }

    }

    private void configureSession() {
        Config config = new Config(session);
        if (!setupAugmentedImageDb(config)) {
            messageSnackbarHelper.showError(this, "Could not setup augmented image database");
        }
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);

        if (enableAutoFocus) {
            config.setFocusMode(Config.FocusMode.AUTO);
        } else {
            config.setFocusMode(Config.FocusMode.AUTO);
        }

        session.configure(config);

    }

    public boolean setupAugmentedImageDb(Config config) {
        AugmentedImageDatabase augmentedImageDatabase;

        Bitmap sintesiAugmentedImageBitmap = loadAugmentedImage("sintesi.jpg");
        if (sintesiAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap menhereAugmentedImageBitmap = loadAugmentedImage("menhere.jpg");
        if (menhereAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap tuttominutoAugmentedImageBitmap = loadAugmentedImage("tuttominuto.jpg");
        if (tuttominutoAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap conceptstoreAugmentedImageBitmap = loadAugmentedImage("conceptstore.jpg");
        if (conceptstoreAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap scorieAugmentedImageBitmap = loadAugmentedImage("scorie.jpg");
        if (scorieAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap uomoglobaleAugmentedImageBitmap = loadAugmentedImage("uomoglobale.jpg");
        if (uomoglobaleAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap chiAugmentedImageBitmap = loadAugmentedImage("chi.jpg");
        if (chiAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap uomodueAugmentedImageBitmap = loadAugmentedImage("uomodue.jpg");
        if (uomodueAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap uomoduedueAugmentedImageBitmap = loadAugmentedImage("uomoduedue.jpg");
        if (uomoduedueAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap festivalunoaAugmentedImageBitmap = loadAugmentedImage("festivalunoa.jpg");
        if (festivalunoaAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap festivalunobAugmentedImageBitmap = loadAugmentedImage("festivalunob.jpg");
        if (festivalunobAugmentedImageBitmap == null) {
            return false;
        }
        Bitmap festivaldueAugmentedImageBitmap = loadAugmentedImage("festivaldue.jpg");
        if (festivaldueAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap festaincantoAugmentedImageBitmap = loadAugmentedImage("festaincanto.jpg");
        if (festaincantoAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap hashtagconAugmentedImageBitmap = loadAugmentedImage("hashtagcon.jpg");
        if (hashtagconAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap sdtnslscAugmentedImageBitmap = loadAugmentedImage("sdtnslsc.jpg");
        if (sdtnslscAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap zzzAugmentedImageBitmap = loadAugmentedImage("zzz.jpg");
        if (zzzAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap cuoreanimaAugmentedImageBitmap = loadAugmentedImage("cuoreanima.jpg");
        if (cuoreanimaAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap ivoltideiquartieriAugmentedImageBitmap = loadAugmentedImage("ivoltideiquartieri.jpg");
        if (ivoltideiquartieriAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap lafinedelmondoAugmentedImageBitmap = loadAugmentedImage("lafinedelmondo.jpg");
        if (lafinedelmondoAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap tantieroiAugmentedImageBitmap = loadAugmentedImage("tantieroi.jpg");
        if (tantieroiAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap cuoredinapoliunoAugmentedImageBitmap = loadAugmentedImage("cuoredinapoliuno.jpg");
        if (cuoredinapoliunoAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap cuoredinapolidueAugmentedImageBitmap = loadAugmentedImage("cuoredinapolidue.jpg");
        if (cuoredinapolidueAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap cuoredinapolitreAugmentedImageBitmap = loadAugmentedImage("cuoredinapolitre.jpg");
        if (cuoredinapolitreAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap cuoredinapoliquattroAugmentedImageBitmap = loadAugmentedImage("cuoredinapoliquattro.jpg");
        if (cuoredinapoliquattroAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap cuoredinapolicinqueAugmentedImageBitmap = loadAugmentedImage("cuoredinapolicinque.jpg");
        if (cuoredinapolicinqueAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap tedxAugmentedImageBitmap = loadAugmentedImage("tedx.jpg");
        if (tedxAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap magnificaastrazioneAugmentedImageBitmap = loadAugmentedImage("magnificaastrazione.jpg");
        if (magnificaastrazioneAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap pattoformativoAugmentedImageBitmap = loadAugmentedImage("pattoformativo.jpg");
        if (pattoformativoAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap mediaintegratiAugmentedImageBitmap = loadAugmentedImage("mediaintegrati.jpg");
        if (mediaintegratiAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap artiseverywhereAugmentedImageBitmap = loadAugmentedImage("artiseverywhere.jpg");
        if (artiseverywhereAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap medintcornaAugmentedImageBitmap = loadAugmentedImage("medintcorna.jpg");
        if (medintcornaAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap medintcavalloAugmentedImageBitmap = loadAugmentedImage("medintcavallo.jpg");
        if (medintcavalloAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap medintsciamanoAugmentedImageBitmap = loadAugmentedImage("medintsciamano.jpg");
        if (medintsciamanoAugmentedImageBitmap == null) {
            return false;
        }

        Bitmap medintlorenzoAugmentedImageBitmap = loadAugmentedImage("medintlorenzo.jpg");
        if (medintlorenzoAugmentedImageBitmap == null) {
            return false;
        }
     /*
        Bitmap ---AugmentedImageBitmap = loadAugmentedImage("---.jpg");
        if (---AugmentedImageBitmap == null) {
            return false;
        }
        */

        augmentedImageDatabase = new AugmentedImageDatabase(session);
        augmentedImageDatabase.addImage("sintesi", sintesiAugmentedImageBitmap);
        augmentedImageDatabase.addImage("menhere", menhereAugmentedImageBitmap);
        augmentedImageDatabase.addImage("tuttominuto", tuttominutoAugmentedImageBitmap);
        augmentedImageDatabase.addImage("conceptstore", conceptstoreAugmentedImageBitmap);
        augmentedImageDatabase.addImage("scorie", scorieAugmentedImageBitmap);
        augmentedImageDatabase.addImage("uomoglobale", uomoglobaleAugmentedImageBitmap);
        augmentedImageDatabase.addImage("chi", chiAugmentedImageBitmap);
        augmentedImageDatabase.addImage("uomodue", uomodueAugmentedImageBitmap);
        augmentedImageDatabase.addImage("uomoduedue", uomoduedueAugmentedImageBitmap);
        augmentedImageDatabase.addImage("festivalunoa", festivalunoaAugmentedImageBitmap);
        augmentedImageDatabase.addImage("festivalunob", festivalunobAugmentedImageBitmap);
        augmentedImageDatabase.addImage("festivaldue", festivaldueAugmentedImageBitmap);
        augmentedImageDatabase.addImage("festaincanto", festaincantoAugmentedImageBitmap);
        augmentedImageDatabase.addImage("hashtagcon", hashtagconAugmentedImageBitmap);
        augmentedImageDatabase.addImage("sdtnslsc", sdtnslscAugmentedImageBitmap);
        augmentedImageDatabase.addImage("zzz", zzzAugmentedImageBitmap);
        augmentedImageDatabase.addImage("cuoreanima", cuoreanimaAugmentedImageBitmap);
        augmentedImageDatabase.addImage("ivoltideiquartieri", ivoltideiquartieriAugmentedImageBitmap);
        augmentedImageDatabase.addImage("lafinedelmondo", lafinedelmondoAugmentedImageBitmap);
        augmentedImageDatabase.addImage("tantieroi", tantieroiAugmentedImageBitmap);
        augmentedImageDatabase.addImage("cuoredinapoliuno", cuoredinapoliunoAugmentedImageBitmap);
        augmentedImageDatabase.addImage("cuoredinapolidue", cuoredinapolidueAugmentedImageBitmap);
        augmentedImageDatabase.addImage("cuoredinapolitre", cuoredinapolitreAugmentedImageBitmap);
        augmentedImageDatabase.addImage("cuoredinapoliquattro", cuoredinapoliquattroAugmentedImageBitmap);
        augmentedImageDatabase.addImage("cuoredinapolicinque", cuoredinapolicinqueAugmentedImageBitmap);
        augmentedImageDatabase.addImage("tedx", tedxAugmentedImageBitmap);
        augmentedImageDatabase.addImage("magnificaastrazione", magnificaastrazioneAugmentedImageBitmap);
        augmentedImageDatabase.addImage("pattoformativo", pattoformativoAugmentedImageBitmap);
        augmentedImageDatabase.addImage("mediaintegrati", mediaintegratiAugmentedImageBitmap);
        augmentedImageDatabase.addImage("artiseverywhere", artiseverywhereAugmentedImageBitmap);
        augmentedImageDatabase.addImage("medintcorna", medintcornaAugmentedImageBitmap);
        augmentedImageDatabase.addImage("medintcavallo", medintcavalloAugmentedImageBitmap);
        augmentedImageDatabase.addImage("medintsciamano", medintsciamanoAugmentedImageBitmap);
        augmentedImageDatabase.addImage("medintlorenzo", medintlorenzoAugmentedImageBitmap);
        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;

        //  augmentedImageDatabase.addImage("---", ---AugmentedImageBitmap);

       /* Bitmap augmentedImageBitmap = loadAugmentedImage();
        if (augmentedImageBitmap == null) {
            return false;
        }

        augmentedImageDatabase = new AugmentedImageDatabase(session);
        augmentedImageDatabase.addImage("bottone", augmentedImageBitmap);
        augmentedImageDatabase.addImage("mano", augmentedImageBitmap);
        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true; */
    }

    private Bitmap loadAugmentedImage(String fileName) {
        try (InputStream is = getAssets().open(fileName)) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(TAG, "IO exception loading augmented image bitmap.", e);
        }
       /* try (InputStream is = getAssets().open("mano.jpg")) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(TAG, "IO exception loading augmented image bitmap.", e);
        } */
        return null;
    }
}