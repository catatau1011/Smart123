package com.example.smartufopa.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartufopa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class RedefinirSenhaActivity extends AppCompatActivity {

    AlertDialog mDialog;

    private TextInputEditText txtEmail;
    Button btRedefinir;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redefinir_senha);
        getSupportActionBar().hide();


        mDialog = new SpotsDialog.Builder().setContext(RedefinirSenhaActivity.this).setMessage("Espere Um Monmento").build();

        auth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.edtEmail);
        btRedefinir = findViewById(R.id.btxEntrar);


        btRedefinir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassowrd(v);
            }
        });
    }

    private void resetPassowrd(View v) {
        if (!ValidarEmail(v)){
            return;
        }
        mDialog.show();
        auth.sendPasswordResetEmail(txtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(RedefinirSenhaActivity.this,MainActivity.class));
                    finish();
                    final Toast toast = Toast.makeText(RedefinirSenhaActivity.this, "Olhe seu email,ou " +
                            "seu spam caso o email não chegue!", Toast.LENGTH_LONG);
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
                    //Toast.makeText(RedefinirSenhaActivity.this,"Por favor, Olhe seu email,\nou " +
                    //"seu spam caso o email não chegue!",Toast.LENGTH_SHORT).show();

                }else {
                    mDialog.hide();
                    Snackbar snackbar = Snackbar.make(v,"Digite o email correto",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    // Toast.makeText(RedefinirSenhaActivity.this,"Digite o email correto",Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RedefinirSenhaActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean ValidarEmail(View v) {
        String email = txtEmail.getText().toString();
        String emailParent = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        mDialog.show();
        if (email.isEmpty()){
            mDialog.hide();
            Snackbar snackbar = Snackbar.make(v,"Insira um email, Por favor",Snackbar.LENGTH_SHORT);
            snackbar.show();
            //Toast.makeText(RedefinirSenhaActivity.this,"Insira um email, Por favor",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!email.matches(emailParent)){
            Snackbar snackbar = Snackbar.make(v,"Email inválido",Snackbar.LENGTH_SHORT);
            snackbar.show();
            //Toast.makeText(RedefinirSenhaActivity.this,"Email inválido",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}