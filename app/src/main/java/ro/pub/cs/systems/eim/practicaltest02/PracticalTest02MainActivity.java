package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText editTextPort;
    private Button btnStartServer;
    private EditText editTextCurrency;
    private Button btnSendRequest;
    private TextView textViewResult;

    private String serverUrl;
    private int serverPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        editTextPort = findViewById(R.id.editTextPort);
        btnStartServer = findViewById(R.id.btnStartServer);
        editTextCurrency = findViewById(R.id.editTextCurrency);
        btnSendRequest = findViewById(R.id.btnSendRequest);
        textViewResult = findViewById(R.id.textViewResult);

        btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
            }
        });

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currency = editTextCurrency.getText().toString();
                sendRequest(currency);
            }
        });
    }

    private void startServer() {
        serverPort = Integer.parseInt(editTextPort.getText().toString());
        serverUrl = "http://10.0.2.2:" + serverPort;
        // Implement server start logic here
        Toast.makeText(this, "Server started on port " + serverPort, Toast.LENGTH_SHORT).show();
    }

    private void sendRequest(String currency) {
        String requestUrl = "https://api.coindesk.com/v1/bpi/currentprice/" + currency + ".json";
        new RequestTask().execute(requestUrl);
    }

    private class RequestTask extends AsyncTask<String, Void, String> {
        @Override
        // daca se fac 2 requesturi in timp de 10 secunde doar se afiseaza din cache
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject bpiObject = jsonObject.getJSONObject("bpi");
                JSONObject currencyObject = bpiObject.getJSONObject("USD");
                String rate = currencyObject.getString("rate");
                rate = rate.replace(",", "");
                textViewResult.setText(rate);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(PracticalTest02MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
