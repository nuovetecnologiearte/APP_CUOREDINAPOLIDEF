package net.cuoredinapoli.battito.cuoredinapoli;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;


import com.example.cuoredinapoli.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.cuoredinapoli.battito.cuoredinapoli.ui.concept.ConceptFragment;

public class MainActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
//      ImageButton buttonOne = findViewById(R.id.imageButton2);
//      buttonOne.setOnClickListener(new View.OnClickListener() {
//          public void onClick(View v) {
//              Toast.makeText(getApplicationContext(), "Apertura AR", Toast.LENGTH_SHORT).show();
//              Intent intent = new Intent(getApplicationContext(), ArComponentActivity.class);
//              startActivity(intent);
//          }
//      });



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_flusso,R.id.navigation_concept,R.id.navigation_frag)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
//       navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//           if (destination.getId() == R.id.imageButton2){
//               Toast.makeText(this, "Apertura AR", Toast.LENGTH_SHORT).show();
//               Intent intent = new Intent(this, ArComponentActivity.class);
//               startActivity(intent);
//           }
//       });

        getSupportActionBar().hide();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Ginevra è grande!";
                        if (!task.isSuccessful()) {
                            msg ="Failed";
                        }
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });



    }





}



