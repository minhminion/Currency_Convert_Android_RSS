package com.example.currencyconvert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner selectFromCurrency;
    Spinner selectToCurrency;
    EditText inputCurrency;
    Button btnChangeCurrency;
    TextView mResult;
    String fromCurrency;
    String toCurrency;
    double currencyRate = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectFromCurrency = (Spinner) findViewById(R.id.from_currencies_spinner);
        selectToCurrency = (Spinner) findViewById(R.id.to_currencies_spinner);

        inputCurrency = (EditText) findViewById(R.id.inputCurrency) ;
        btnChangeCurrency = (Button) findViewById(R.id.btnChangeCurrency);

        mResult = (TextView) findViewById(R.id.mResult);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        selectFromCurrency.setAdapter(adapter);
        selectToCurrency.setAdapter(adapter);

        selectFromCurrency.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCurrency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectToCurrency.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCurrency = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnChangeCurrency.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new FetchFeedTask().execute((Void) null);
            }
        });
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {
        private String urlLink;

        @Override
        protected void onPreExecute () {
//            mSwipeLayout.setRefreshing(true);
            urlLink = "https://"+fromCurrency.toLowerCase()+".fxexchangerate.com/"+toCurrency.toLowerCase()+".xml";
        }
        @Override
        protected Boolean doInBackground(Void ... voids) {
//            if("", e);Toast.makeText(gTextUtils.isEmpty(urlLink)){
//                return false;
//            }
            try {
                if(!urlLink.startsWith("http://") &&
                        !urlLink.startsWith("https://"))
                    urlLink = "http://" + urlLink;
                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                RssFeedModel mFeedModel = parseFeed(inputStream);
                String rate = TextUtils.split(mFeedModel.description, "<br/>")[0];
                rate = TextUtils.split(rate, "=")[1];
                rate = TextUtils.split(rate, " ")[1];
                currencyRate = Double.parseDouble(rate);
                return true;
            } catch (IOException e) {
                Log.e("Error", e.toString());
//                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            } catch (XmlPullParserException e) {
//                   Log.e(TAG, "ErroretApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        protected  void onPostExecute (Boolean success) {
//            mSwipeLayout.setRefreshing(false);

            if(success) {
                Double currency = Double.parseDouble(inputCurrency.getText().toString());
                Double result = currency*currencyRate;
                Log.i("Result", result.toString());
            } else {
                Toast.makeText(MainActivity.this,
                        "Enter a valid Rss feed url",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public RssFeedModel parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String link = null;
        String description = null;
        RssFeedModel item = null;
        boolean isItem = false;
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);
//            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();
                String name = xmlPullParser.getName();
                if (name == null)
                    continue;
                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }
                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }
                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT){
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }
                if (name.equalsIgnoreCase("title")){
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        item = new RssFeedModel(title, link, description);
                    }
                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }}
            return item;
        } finally {
            inputStream.close();
        }}
}
