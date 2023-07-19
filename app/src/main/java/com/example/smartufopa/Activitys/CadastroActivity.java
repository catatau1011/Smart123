package com.example.smartufopa.Activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartufopa.R;
import com.example.smartufopa.moldes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;


public class CadastroActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    Button btCadastrar;
    public static EditText editNome, editSobrenome, editEmail, editTelefone, editSenha, editConfirmaSenha, editEndereco, editBairro;

    AlertDialog mDialog;

    private TextView txtcadastro;


    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso!","Falha ao cadastrar usuario"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDialog = new SpotsDialog.Builder().setContext(CadastroActivity.this).setMessage("Espere Um Monmento").build();

        editNome = findViewById(R.id.editNome);
        editSobrenome = findViewById(R.id.editSobreNome);
        editEmail = findViewById(R.id.editEmail);
        editTelefone = findViewById(R.id.editTelefone);
        editEndereco = findViewById(R.id.editEndereço);
        editBairro = findViewById(R.id.editBairro);
        editSenha = findViewById(R.id.editSenha);
        editConfirmaSenha = findViewById(R.id.editConfirmarSenha);
        btCadastrar = findViewById(R.id.btCadastrar);
        txtcadastro = findViewById(R.id.txtTelaCadastro1);

        txtcadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CadastroActivity.this,MainActivity.class));
            }
        });


        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editNome.getText().toString();
                String Sobrenome = editSobrenome.getText().toString();
                String email = editEmail.getText().toString();
                String telefone = editTelefone.getText().toString();
                String Endereco = editEndereco.getText().toString();
                String bairro = editBairro.getText().toString();
                String senha = editSenha.getText().toString();
                String Corfirmasenha = editConfirmaSenha.getText().toString();

                if (nome.isEmpty() || Sobrenome.isEmpty() || email.isEmpty() || telefone.isEmpty() || Endereco.isEmpty()
                        || bairro.isEmpty() || senha.isEmpty() || Corfirmasenha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    cadastrarUsuario(v);
                }

            }
        });
    }

    private void cadastrarUsuario(View v) {
        String nome = editNome.getText().toString();
        String Sobrenome = editSobrenome.getText().toString();
        String email = editEmail.getText().toString();
        String telefone = editTelefone.getText().toString();
        String Endereco = editEndereco.getText().toString();
        String bairro = editBairro.getText().toString();
        String senha = editSenha.getText().toString();
        String Corfirmasenha = editConfirmaSenha.getText().toString();

        if (!nome.isEmpty() || Sobrenome.isEmpty() || !email.isEmpty() || !telefone.isEmpty() || !senha.isEmpty()
                ||bairro.isEmpty()|| !Endereco.isEmpty()||Corfirmasenha.isEmpty()) {
            if(senha.equals(Corfirmasenha)){
                Snackbar snackbar = Snackbar.make(v,mensagens[1],Snackbar.LENGTH_SHORT);
                snackbar.show();
                mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.hide();
                        if (task.isSuccessful()) {
                            mDialog.show();
                            Snackbar snackbar = Snackbar.make(v,mensagens[1],Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            String id = mAuth.getCurrentUser().getUid();
                            saveUsuario(v,id, nome,Sobrenome, email, telefone, Endereco,bairro, senha,Corfirmasenha);
                        } else {
                            String erro;
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e) {
                                erro = "Digite uma senha com no minimi 6 caracteres";
                            }catch (FirebaseAuthUserCollisionException e) {
                                erro = "Esta conta já esta cadastrada";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erro = "Email Invalido";
                            }catch (Exception e){
                                erro = "Erro ao cadastrar usuario";
                            }
                            Snackbar snackbar = Snackbar.make(v,erro,Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
            }else{
                Snackbar snackbar = Snackbar.make(v,"Por Favor,Confirme a senha corretamente",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    private void saveUsuario(View v, String id, String nome, String sobrenome, String email, String telefone, String endereco, String bairro, String senha, String corfirmasenha) {
            User user = new User();
            user.setNome(nome);
            user.setSobrenome(sobrenome);
            user.setEmail(email);
            user.setTelefone(telefone);
            user.setEndereco(endereco);
            user.setBairro(bairro);
            user.setSenha(senha);
            user.setCorfirmasenha(corfirmasenha);

            mDatabase.child("Usuario").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }else {
                        Snackbar snackbar = Snackbar.make(v, mensagens[2], Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            });
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
