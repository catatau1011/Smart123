package com.example.smartufopa.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.smartufopa.R;
import com.example.smartufopa.Urbanismo.UrbanismoActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OpcoesActivity extends AppCompatActivity {
    Button btnSeguranca,btnUrnbanismo;
    FirebaseAuth mAuth;


    private final String TAG = this.getClass().getName();
    private long backexpressed;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcoes);




        toolbar = findViewById(R.id.toolbar);
        drawerLayout= findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.seguranca:
                        AlertDialog.Builder cancelar = new AlertDialog.Builder(OpcoesActivity.this);
                        cancelar.setTitle("Atenção!");
                        cancelar.setMessage("Deseja Continuar?");
                        cancelar.setCancelable(false);
                        cancelar.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(OpcoesActivity.this,AcionamentoActivity.class));
                            }
                        });
                        cancelar.setNegativeButton("Não",null);
                        cancelar.create().show();
                        break;
                    case R.id.saude:
                        Toast.makeText(OpcoesActivity.this,"Modulo em desenvolvimento",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        recreate();
                        break;
                    case R.id.educacao:
                        Toast.makeText(OpcoesActivity.this,"Modulo em desenvolvimento",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        recreate();
                        break;
                    case R.id.mAmbiente:
                        startActivity(new Intent(OpcoesActivity.this, UrbanismoActivity.class));
                        finish();
                        break;

                    case R.id.cultura:
                        Toast.makeText(OpcoesActivity.this,"Modulo em desenvolvimento",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        recreate();
                        break;
                    case R.id.turismo:
                        Toast.makeText(OpcoesActivity.this,"Modulo em desenvolvimento",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        recreate();
                        break;
                    case R.id.logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        mAuth.signOut();
                        Toast.makeText(OpcoesActivity.this,"Usuario delosgado com sucesso!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OpcoesActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

                return true;
            }
        });
        btnSeguranca = findViewById(R.id.btnSegurança);
        btnUrnbanismo = findViewById(R.id.btnUrbanismo);
        mAuth= FirebaseAuth.getInstance();

        btnUrnbanismo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OpcoesActivity.this, UrbanismoActivity.class));
                finish();
            }
        });
        btnSeguranca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder cancelar = new AlertDialog.Builder(OpcoesActivity.this);
                cancelar.setTitle("Atenção!");
                cancelar.setMessage("Deseja Continuar?");
                cancelar.setCancelable(false);
                cancelar.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(OpcoesActivity.this,AcionamentoActivity.class));
                    }
                });
                cancelar.setNegativeButton("Não",null);
                cancelar.create().show();
            }
        });
    }
    boolean duplo_click;
    @Override
    public void onBackPressed() {
        Log.d(TAG,"click");
        if (duplo_click==true){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }
        duplo_click = true;
        Log.d(TAG,"twice : "+duplo_click);

        Toast.makeText(OpcoesActivity.this,"por favor, pressione novamente para sair",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                duplo_click = false;
                Log.d(TAG,"twice : "+duplo_click);

            }
        },3000);
        duplo_click= true;
    }

/*
    @Override
    //public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();
        if (item_id==R.id.logout){
            mAuth.signOut();
            Toast.makeText(OpcoesActivity.this,"Usuario delosgado com sucesso!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OpcoesActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
*/

}