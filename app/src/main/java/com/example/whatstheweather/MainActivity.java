package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView weather;
/*----------------------------------------------------------------------*/
    public void findWeather(View view) throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        Log.i("cityName",cityName.getText().toString());

        InputMethodManager mgr= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);


       // task.execute("https://openweathermap.org/data/2.5/weather?q=Mumbai&appid=b6907d289e10d714a6e88b30761fae22").get();
        try{
            String encodedCityName= URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            DownloadTask task= new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q="+cityName.getText().toString()+"&appid=b6907d289e10d714a6e88b30761fae22").get();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_SHORT);
        }




}
/*---------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName=(EditText)findViewById(R.id.cityEditText);
        weather=(TextView)findViewById(R.id.resultTextView);
    }

/*-------------------------------------------------------------------*/
    public class DownloadTask extends AsyncTask<String , Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String result="";
            URL url = null;
            HttpURLConnection urlConnection=null;

            try{
                url=new URL(strings[0]);
                urlConnection=(HttpURLConnection)url.openConnection();

                InputStream in=urlConnection.getInputStream();

                InputStreamReader reader= new InputStreamReader(in);

                int data=reader.read();
                while(data!=-1){
                    char current= (char) data;
                    result+=current;
                    data=reader.read();

                }
                return result;

            }catch(Exception e){
                Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_SHORT);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            try{
                String message="";

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray arr= new JSONArray(weatherInfo);
                for(int i=0;i<arr.length();i++){
                    JSONObject JSONPart=arr.getJSONObject(i);

                    String main="";
                    String description="";
                    main=JSONPart.getString("main");
                    description=JSONPart.getString("description");

                    if(main!="" && description!=""){
                        message ="description : "+description+ "\n";
                    }
                }

                String tempInfo = jsonObject.getString("main");

                Log.i("RES","done");
                Log.i("temp content",tempInfo);

                String temp="";
                String humidity="";
                JSONObject mainPart=new JSONObject((tempInfo));
                Log.i("temp",mainPart.getString("temp"));
                Log.i("humidity",mainPart.getString("humidity"));
                temp=mainPart.getString("temp");
                humidity=mainPart.getString("humidity");

                if(temp!="" && humidity!=""){
                    message +="humidity : "+humidity+ "\n" + "temperature : "+temp+"°C\n";}

                if(message!=""){
                    weather.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_SHORT);
                }

            }catch(Exception e){
                Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_SHORT);
            }
        }
    }
}
