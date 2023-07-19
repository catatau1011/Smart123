package com.example.smartufopa.Urbanismo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartufopa.Activitys.OpcoesActivity;
import com.example.smartufopa.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrbanismoActivity extends AppCompatActivity implements LocationListener {
    private ImageView imageView;

    EditText edtNome;
    Button btEnviar_dados;

    Location location;
    LocationManager locationManager;

    private Button uploadBtn;
    private Button buttonSelecionar;
    private ImageView imageView13;


    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    private Uri imageUri;
    Model model = new Model();
    ProgressBar progressBar;
    String userName,ocorrido;

    AutoCompleteTextView autoCompleteTextView, edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urbanismo);

        minhaLocalizacao();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        buttonSelecionar = findViewById(R.id.buttonSelecionar);
        uploadBtn = findViewById(R.id.upload_btn);
        progressBar = findViewById(R.id.progressBar2);
        imageView13 = findViewById(R.id.imageView13);
        progressBar.setVisibility(View.INVISIBLE);

        buttonSelecionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,2);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUri !=null){
                    uploadToFirebase(imageUri);
                }else {
                    Toast.makeText(getApplicationContext(),"Selecione a imagem",Toast.LENGTH_SHORT).show();
                }
            }
        });


        imageView = findViewById(R.id.ic_voltar233);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OpcoesActivity.class));
                finish();
            }
        });

        autoCompleteTextView = findViewById(R.id.autoCompleteOcorrido1);
        edtMessage = findViewById(R.id.edtdescricao);
        String[] Array = getResources().getStringArray(R.array.item_list2);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UrbanismoActivity.this, android.R.layout.simple_list_item_1, Array);
        autoCompleteTextView.setAdapter(arrayAdapter);


        edtNome = findViewById(R.id.edtNome1);
        autoCompleteTextView = findViewById(R.id.autoCompleteOcorrido1);
        edtMessage = findViewById(R.id.edtdescricao);
        btEnviar_dados = findViewById(R.id.btEnviar_dados);


        btEnviar_dados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 userName = edtNome.getText().toString().trim();
                 ocorrido = autoCompleteTextView.getText().toString().trim();
                if (userName.isEmpty()||ocorrido.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Preencha todos os campos",Toast.LENGTH_SHORT).show();
                }else {
                    addItem();
                }
            }
        });

    }



   private void uploadToFirebase(Uri uri) {
    StorageReference fileRef = firebaseStorage.getReference().child("Images")
            .child(System.currentTimeMillis()+"");
    fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    model.setImageUrl(uri.toString());

                    firebaseDatabase.getReference().child("Usuario").child("Images")
                            .push()
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(),"Suecesso",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"falha",Toast.LENGTH_SHORT).show();

                                }
                            });

                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Foto salva com sucesso",Toast.LENGTH_SHORT).show();


                }
            });
        }
    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(),"Falha ao fazer upload",Toast.LENGTH_SHORT).show();
        }
    });
    }



    private void minhaLocalizacao() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // Toast.makeText(OcorrenciaActivity.this, "Permissão aceita", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(UrbanismoActivity.this, "Permissão negada\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(UrbanismoActivity.this, OpcoesActivity.class));

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
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
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

    private void addItem() {
        final ProgressDialog dialog = ProgressDialog.show(UrbanismoActivity.this,"Adicionando item","Por favor aguarde");

         String userName = edtNome.getText().toString().trim();
         String ocorrido = autoCompleteTextView.getText().toString().trim();
         String descricao = edtMessage.getText().toString().trim();

        Location location = getLastKnownLocation();
        String userLocation = "http://maps.google.com/maps?daddr="+location.getLatitude()+","+location.getLongitude();


        if (ContextCompat.checkSelfPermission(UrbanismoActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Locaiom();
        }else{
            ActivityCompat.requestPermissions(UrbanismoActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        //Localização
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,
                (LocationListener) UrbanismoActivity.this);
    StringRequest stringRequest = new StringRequest(Request.Method.POST,
            "https://script.google.com/macros/s/AKfycbym3nvhH4EJz4Ik0t5h3Z_XwbttmURBF1t3LJpw63BHpjN6zNO5O_kYgK0hKOYAL_dl/exec", new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
        dialog.dismiss();
            Toast.makeText(UrbanismoActivity.this,""+response,Toast.LENGTH_LONG).show();
            limpardados();
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        dialog.dismiss();
        }
    }){
        @Nullable
        @Override
        protected Map<String, String> getParams(){

            Map<String,String> parmas = new HashMap<>();
             parmas.put("action","addItem");
             parmas.put("userName",userName);
             parmas.put("address",ocorrido);
             parmas.put("password",descricao);
             parmas.put("localizacao",userLocation);
            parmas.put("foto",model.getImageUrl());
            return parmas;

        }
    };

    int timeOut = 5000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(timeOut,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(UrbanismoActivity.this);
        queue.add(stringRequest);

    }

    private void limpardados() {
        imageView13.setImageResource(R.drawable.log_atualizado);
        edtNome.setText("");
        autoCompleteTextView.setText("");
        edtMessage.setText("");
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
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==2 && resultCode ==RESULT_OK && data != null){
            imageUri = data.getData();
            imageView13.setImageURI(imageUri);

        }
    }

    @Override
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
    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }*/
}