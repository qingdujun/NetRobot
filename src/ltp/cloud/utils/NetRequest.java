package ltp.cloud.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class NetRequest {

	/**
     * doGet∑¢ÀÕÕ¯¬Á«Î«Û
     * @param url
     * @return
     */
    public static String doGet(String url) {

        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream inputStream = httpEntity.getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String line = null;
                StringBuffer stringBuffer = new StringBuffer();
                while (null != (line = bufferedReader.readLine())) {
                    stringBuffer.append(line);
                }
                String json = stringBuffer.toString();
                System.out.println(json);
                return json;
            }
        }catch (Exception e){e.printStackTrace();}

        return null;
    }
}
