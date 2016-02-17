package apps.sparky.dallasmountainbiking.BLL.Parsers;

import com.google.gson.Gson;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import apps.sparky.dallasmountainbiking.Objects.CheckIns;

/**
 * Created by David.Kobuszewski on 2/9/2016.
 */
public class CheckInsParser {
    public CheckIns[] FromJson(String json)
    {
        return new Gson().fromJson(json, CheckIns[].class);
    }

    public String GetRawData(String urlAddress) throws IOException
    {
        return ReadAll(urlAddress);
    }

    private String ReadAll(String url) throws IOException
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        return client.execute(request, responseHandler);
    }
}
