package com.example.smartufopa.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.example.smartufopa.network.APIClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OcorrenciaActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    ApiInterface apiInterface;
    Button btEnviar_dados;
    LocationManager locationManager;
    EditText edtNome;
    Location location;
    AutoCompleteTextView autoCompleteTextView, edtMessage;
    private ImageView imageView;


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String telefone,endereco;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocorrencia);

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

        imageView= findViewById(R.id.ic_voltar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AcionamentoActivity.class));
                finish();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Usuario");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ma =snapshot.getValue().toString();
                System.out.println(" O dados são :" + ma);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        minhaLocalizacao();

        // Aqui é lista zoada
        autoCompleteTextView = findViewById(R.id.autoCompleteOcorrido);
        autoCompleteTextView = findViewById(R.id.autoCompleteOcorrido);
        String[] Array = getResources().getStringArray(R.array.item_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(OcorrenciaActivity.this, android.R.layout.simple_list_item_1, Array);
        autoCompleteTextView.setAdapter(arrayAdapter);

        edtNome = findViewById(R.id.edtNome);
        autoCompleteTextView = findViewById(R.id.autoCompleteOcorrido);
        edtMessage = findViewById(R.id.edtMessage);
        //editCeleular = findViewById(R.id.editcelular);
        btEnviar_dados = findViewById(R.id.btEnviar_dado);





        btEnviar_dados.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {

                try {
                    ocorrencia(v);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        //Instancia para api
        apiInterface = APIClient.getClient().create(ApiInterface.class);
    }

    private void metodo1(String telefone) {
        System.out.println("telefone: " + telefone);
    }
    private void metodo2(String endereco) {
        System.out.println("endereco: " + endereco);

    }


    private void minhaLocalizacao() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
               // Toast.makeText(OcorrenciaActivity.this, "Permissão aceita", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(OcorrenciaActivity.this, "Permissão negada\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(OcorrenciaActivity.this,AcionamentoActivity.class));

            }

        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Se você rejeitar a permissão, não poderá usar este serviço\n\nAtive as permissões em [Configuração] > [Permissão]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    private void ocorrencia(View c) throws UnsupportedEncodingException {

        //Telefone

        //Data
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
        Date data = new Date();
        String dataFormatada = formataData.format(data);
        //Hora
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        String horaformatada = sdf.format(hora);
        String toNumber = "+55093991959446";

        String Nome = edtNome.getText().toString();
        String Ocorrido = autoCompleteTextView.getText().toString();
        String Descricao = edtMessage.getText().toString();

        Location location = getLastKnownLocation();
        String userLocation = "http://maps.google.com/maps?daddr="+location.getLatitude()+","+location.getLongitude();

        //Descrição
        String urlWhatsApp = "http://api.whatsapp.com/send?phone="+toNumber +"&text="+Nome+"\n"+"Ocorrido: "+Ocorrido+"\n"+"Descrição: "+Descricao
                +"\n"+"Localização : "+userLocation;

        ///Pedindo permissão do usuario
        if (ContextCompat.checkSelfPermission(OcorrenciaActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Locaiom();
        }else{
            ActivityCompat.requestPermissions(OcorrenciaActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        //estrutura pra pegar api
        try {
            if(!Nome.isEmpty()&&!Ocorrido.isEmpty()&&!Descricao.isEmpty()){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlWhatsApp));
                startActivity(intent);
                finish();
                //Localização
                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,
                        (LocationListener) OcorrenciaActivity.this);

                final Toast toast = Toast.makeText(OcorrenciaActivity.this, "Sua Solicitação será enviada para a policia,Aguarde no local!", Toast.LENGTH_LONG);
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
                // Exibe a mensagem
                toast.show();
                // Inicia a contagem do tempo
                toastCountDown.start();
                Dados_da_Denuncia dados_da_denuncia = new Dados_da_Denuncia(
                        dataFormatada,
                        horaformatada,
                        Ocorrido,
                        Nome,
                        telefone,
                        endereco,
                        userLocation,
                        Descricao);
                Call<Void> call1 = apiInterface.createUser(dados_da_denuncia);
                call1.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("Success", "200");
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
                //Toast.makeText(OcorrenciaActivity.this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void Locaiom() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);
        request.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
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

    @Override
    public void onLocationChanged( Location location) {
        try {
            //destino
            String whatsAppMessage = "http://maps.google.com/maps?daddr=" +location.getLatitude() + "," +location.getLongitude();
            //System.out.println("22222222222222222222222222222222222222222222222222222222222222222222222222222222222");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            //Eu ocultei aqui achando que essa era a solução.
            //startActivity(sendIntent);
            // Toast.makeText(this,""+location.getLatitude()+","+location.getLongitude(),Toast.LENGTH_SHORT).show();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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