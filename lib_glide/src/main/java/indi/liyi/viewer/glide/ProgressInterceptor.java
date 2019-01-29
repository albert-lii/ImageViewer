package indi.liyi.viewer.glide;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProgressInterceptor implements Interceptor {
    public static final Map<Object, OnProgressListener> LISTENER_MAP = new HashMap<>();

    // 注册下载监听
    public static void addListener(Object src, OnProgressListener listener) {
        LISTENER_MAP.put(src, listener);
    }

    // 取消注册下载监听
    public static void removeListener(Object src) {
        LISTENER_MAP.remove(src);
    }


    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String url = request.url().toString();
        ResponseBody body = response.body();
        Response newResponse = response.newBuilder().body(new ProgressResponseBody(url, body)).build();
        return newResponse;
    }
}
