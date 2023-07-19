package com.example.smartufopa.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.smartufopa.R;

public class AcionamentoActivity extends AppCompatActivity {
    private ImageButton button_denucia,buton_Ocorrencia;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acionamento);


        imageView= findViewById(R.id.ic_voltar233);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),OpcoesActivity.class));
                finish();
            }
        });
        button_denucia= findViewById(R.id.btnDenuncia);
        buton_Ocorrencia = findViewById(R.id.btnOcorrencia);

        button_denucia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DenunciaActivity.class));

            }
        });

        buton_Ocorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),OcorrenciaActivity.class));
            }
        });
    }
}