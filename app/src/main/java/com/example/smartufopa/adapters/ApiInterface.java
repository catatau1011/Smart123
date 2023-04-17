package com.example.smartufopa.adapters;

import com.example.smartufopa.moldes.Dados_da_Denuncia;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("/macros/s/AKfycbxuIZhVlnPQ8rBfjMxaLV55irtLSAI_4Z5YEwXgxRkVe8QU5pEChVUMnVm4hFNl1STs/exec?action=postData")
    Call<Void> createUser(@Body Dados_da_Denuncia user); //, @Query("action") String actionQ"postData");

}