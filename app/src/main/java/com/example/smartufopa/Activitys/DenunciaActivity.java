package com.example.smartufopa.Activitys;

import static android.content.ContentValues.TAG;
import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartufopa.R;
import com.example.smartufopa.adapters.ApiInterface;
import com.example.smartufopa.moldes.Dados_da_Denuncia;
import com.example.smartufopa.moldes.User;
import com.example.smartufopa.network.APIClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DenunciaActivity extends AppCompatActivity implements LocationListener{

    ApiInterface apiInterface;
    Button btEnviar_dados;
    LocationManager locationManager;
    Location location;
    AutoCompleteTextView autoCompleteTextView,autoCompleteTextDenuncia;

    private ImageView imageView;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String telefone,endereco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Usuario").child(userId).exists()) {
                    // O nó existe para o usuário atual
                    // Recupere os dados do nó
                    telefone = dataSnapshot.child("Usuario").child(userId).child("telefone").getValue().toString();
                    endereco = dataSnapshot.child("Usuario").child(userId).child("endereco").getValue().toString();
                    metodo1(telefone);
                    metodo2(endereco);

                    // ...
                } else {
                    // O nó não existe para o usuário atual
                    // Trate o caso adequadamente
                    System.out.println("Não existe");
                    // ...
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Trate quaisquer erros que ocorram durante a recuperação dos dados
                Log.e("Firebase", "Erro ao recuperar dados do Firebase", databaseError.toException());
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        //ouvinte();
        imageView= findViewById(R.id.ic_voltar1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AcionamentoActivity.class));
                finish();
            }
        });

        minhaLocalizacao();
        autoCompleteTextView = findViewById(R.id.autoCompleteOcorrido1);
        autoCompleteTextDenuncia = findViewById(R.id.autoCompleteDenuncia);

        String[] Array = getResources().getStringArray(R.array.item_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DenunciaActivity.this, android.R.layout.simple_list_item_1,Array);

        autoCompleteTextView.setAdapter(arrayAdapter);

        btEnviar_dados = findViewById(R.id.btEnviar_dado);
        btEnviar_dados.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)// <-----------novo
            @Override
            public void onClick(View v) {
                try {
                    ocorrencia(v);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        apiInterface = APIClient.getClient().create(ApiInterface.class);

    }




    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")

    private void ocorrencia(View c) throws UnsupportedEncodingException {


        //Data
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
        Date data = new Date();
        String dataFormatada = formataData.format(data);
        //Hora
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        String horaformatada = sdf.format(hora);

        String toNumber = "+55093991959446";

        String Ocorrido = autoCompleteTextView.getText().toString();
        String Descricao = autoCompleteTextDenuncia.getText().toString();
        //Localização do ususario
        Location location = getLastKnownLocation();
        System.out.println("77777777777777777777777777777777777777777777777777777777777777777");
        String userLocation = "http://maps.google.com/maps?daddr="+location.getLatitude()+","+location.getLongitude();

        //////////////////////////////////////////////////////////////////////////////////////////////////

        //Descrição
        String urlWhatsApp = "http://api.whatsapp.com/send?phone="+toNumber +"&text="+"Ocorrido: "+Ocorrido+"\n"+"Descrição: "+Descricao
                +"\n"+"Localização : "+userLocation;

        ///Pedindo permissão do usuario
        if (ContextCompat.checkSelfPermission(DenunciaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Locaiom();
            System.out.println("5555555555555555555555555555555555555555555555555555555555555");

        }else{
            ActivityCompat.requestPermissions(DenunciaActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        try {
            if(!Ocorrido.isEmpty()&&!Descricao.isEmpty()){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlWhatsApp));
                startActivity(intent);
                System.out.println("2222222222222222222222222222222222222222222222222222222222");
                finish();
                //Localização
                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,
                        (LocationListener) DenunciaActivity.this);
                final Toast toast = Toast.makeText(DenunciaActivity.this, "Sua Solicitação será enviada para a policia,Aguarde no local!", Toast.LENGTH_LONG);
                // Exibe a mensagem por 10 segundos
                CountDownTimer toastCountDown;
                toastCountDown = new CountDownTimer(10000, 2000) {
                    public void onTick(long millisUntilFinished) {
                        toast.show();
                    }
                    public void onFinish() {
                        toast.cancel();
                    }
                };
                toast.show();
                toastCountDown.start();
                Dados_da_Denuncia user = new Dados_da_Denuncia(
                        dataFormatada,
                        horaformatada,
                        Ocorrido,
                        "Anônimo",
                        telefone+"",
                        endereco+"",
                        userLocation,
                        Descricao);
                Call<Void> call1 = apiInterface.createUser(user);
                call1.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("Sucesso");
                        finish();
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("Failed", "500");
                        // Ocorreu uma falha na chamada de API
                    }
                });
            }else {
                Snackbar snackbar = Snackbar.make(c,"Preencha todos os campos",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void metodo1(String telefone) {
        System.out.println("telefone: " + telefone);
    }

    private void metodo2(String endereco) {
        System.out.println("endereco: " + endereco);
        
    }


    private void Locaiom() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);
        request.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private Location getLastKnownLocation() {
        Location l=null;
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void minhaLocalizacao() {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(DenunciaActivity.this, "Permissão aceita", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(DenunciaActivity.this, "Permissão negada\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(DenunciaActivity.this,AcionamentoActivity.class));
            }

        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Se você rejeitar a permissão, não poderá usar este serviço\n\nAtive as permissões em [Configuração] > [Permissão]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

    }


    @Override
    public void onLocationChanged( Location location) {

        try {
            //destino
            String whatsAppMessage = "http://maps.google.com/maps?daddr=" +location.getLatitude() + "," +location.getLongitude();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            //Eu ocultei aqui achando que essa era a solução.
            //startActivity(sendIntent);
            System.out.println("8888888888888888888888888888888888888888888888888888888888888888888");
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }
    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}