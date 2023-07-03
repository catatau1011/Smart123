package com.example.smartufopa.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartufopa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    //declarando varias para utilizar
    private final String TAG = this.getClass().getName();
    private static final String FILE_NAME =  "myFile";
    private TextInputEditText campoEmail, campoSenha;
    private Button buttonEntrar;
    private TextView txtcadastro,txtesqueciSenha;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    AlertDialog mDialog;

    String[]  mensagens = {"Preencha todos os campos","Login Realizado com sucesso!", "Email e Senha, são obrigatórios!","Email ou Senha, estão Incorretos"};

    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializando elas
        campoEmail = findViewById(R.id.editEmail1);
        campoSenha = findViewById(R.id.editSenha1);
        buttonEntrar = findViewById(R.id.btEntrar);
        txtcadastro = findViewById(R.id.txtTelaCadastro);
        txtesqueciSenha = findViewById(R.id.txtEsqueciSenha);
        checkBox = findViewById(R.id.remember_me_ckhd);

        //salvando as informações de login, para não precisar escrever de novo
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        String username= sharedPreferences.getString("email","");
        String password =sharedPreferences.getString("senha","");
        campoEmail.setText(username);
        campoSenha.setText(password);


        //Instanciando firebase para cadastro e login do usuario
        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // progressbar antes do usuario logar
        mDialog = new SpotsDialog.Builder().setContext(MainActivity.this).setMessage("Espere Um Momento").build();
        txtesqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RedefinirSenhaActivity.class));
            }
        });

        txtcadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CadastroActivity.class));
            }
        });

        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(v);
            }
        });

    }


    private void Login(View v) {
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();
        //metodo que guarda as informações do usuario
        if (checkBox.isChecked()){
            StoredDataUsingHarePref(textoEmail,textoSenha);
        }
        //verifica se o usuario é diferente de nulo
        if (textoEmail.isEmpty() || textoSenha.isEmpty()) {
            Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else {
            //metodo para logar o usuario
            LoginUsuario(v);
        }

    }

    private void StoredDataUsingHarePref(String textoEmail, String textoSenha) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME,MODE_PRIVATE).edit();
        editor.putString("email",textoEmail);
        editor.putString("senha",textoSenha);
        editor.apply();

    }

    private void LoginUsuario(View v) {
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();
        if (!textoEmail.isEmpty()||!textoSenha.isEmpty()) {
            mDialog.show();
            mAuth.signInWithEmailAndPassword(textoEmail,textoSenha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(getApplicationContext(),OpcoesActivity.class);
                        startActivity(intent);
                        Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        limpardados();
                    }else {
                        String erro;
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            erro = "Senha ou email estão incorretos!";
                        }
                        Snackbar snackbar = Snackbar.make(v,erro,Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                    mDialog.dismiss();
                }
            });
        }
    }

    private void limpardados() {
        campoEmail.setText("");
        campoSenha.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        try {
            Toast.makeText(getApplicationContext(),"usuario"+usuarioAtual.getEmail()+"Logado"
                    ,Toast.LENGTH_SHORT);
            Intent intent = new Intent(getApplicationContext(),OpcoesActivity.class);
            startActivity(intent);
        }catch (Exception e){

        }
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

        Toast.makeText(MainActivity.this,"por favor, pressione novamente para sair",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                duplo_click = false;
                Log.d(TAG,"twice : "+duplo_click);

            }
        },2000);
        duplo_click= true;
    }

}