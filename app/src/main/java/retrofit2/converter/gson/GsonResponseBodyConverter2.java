package retrofit2.converter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.huxq17.xprefs.LogUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter2<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter2(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        LogUtils.d("response=" + response);
        return adapter.fromJson(response);
//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
//
//        try {
//            return adapter.read(jsonReader);
//        } finally {
//            value.close();
//        }
    }
}