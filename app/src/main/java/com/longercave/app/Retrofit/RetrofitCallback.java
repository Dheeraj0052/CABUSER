package com.longercave.app.Retrofit;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by CSS on 8/7/2017.
 */

public interface RetrofitCallback {
    public void Success(Response<ResponseBody> response);

    public void Failure(Throwable errorResponse);
}
