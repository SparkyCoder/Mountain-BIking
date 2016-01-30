package apps.sparky.dallasmountainbiking.BLL.Parsers;

import com.google.gson.Gson;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import apps.sparky.dallasmountainbiking.Objects.Trail;

/**
 * Created by david.kobuszewski on 1/18/2016.
 */
public class TrailsParser {

    public Trail[] FromJson(String json)
    {
        return new Gson().fromJson(json, Trail[].class);
    }

    public String GetRawData(String urlAddress) throws IOException
    {
        String rawData =  ReadAll(urlAddress);
        return FormatJson(rawData);
    }

    private String FormatJson(String json){
        json = RemoveBeginning(json);
        json = RemoveDelimeter(json);
        json = RemoveEnding(json);
        return json;
    }

    private String RemoveDelimeter(String json){
        return json.replace("},{\"trail\":", ",");
    }

    private String RemoveBeginning(String json){
        return json.replace("{\"trails\":[{\"trail\":", "[");
    }

    private String RemoveEnding(String json) {
        return json.replace("}}]}", "}]");
    }

    private String ReadAll(String url) throws IOException
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        return client.execute(request, responseHandler);
    }
}
