package com.example.currencyconvert;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    String fromCurrency;
    String toCurrency;
    double currencyRate = 1;

//    Select From Currency
    List<Currency> currencyList;
    TextView fromCurrencyName;
    TextView fromCurrencyCode;
    TextView toCurrencyName;
    TextView toCurrencyCode;
    RecyclerView selectFromCurrency;
    RecyclerView selectToCurrency;

    TextInputEditText inputCurrency;
    TextInputEditText resultCurrency;

    Button convertButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_version_1);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // GET ALL COMPONENTS IN VIEW
        initSetup();

        // CREATE CURRENCY LIST
        CreateCurrencyList();

        // ADD CURRENCY LIST TO SELECT & ON SELECT ITEM
        SetupSelectCurrency();

        convertButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if( fromCurrency != toCurrency) {
                    new FetchFeedTask().execute((Void) null);
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn tiền tệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initSetup () {
        fromCurrencyName = (TextView) findViewById(R.id.from_currency_name);
        fromCurrencyCode = (TextView) findViewById(R.id.from_currency_code);

        toCurrencyName = (TextView) findViewById(R.id.to_currency_name);
        toCurrencyCode = (TextView) findViewById(R.id.to_currency_code);

        inputCurrency = (TextInputEditText) findViewById(R.id.inputCurrency);
        inputCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch ((int)(s.length()/5)) {
                    case 1:
                        inputCurrency.setTextSize(30f);
                        break;
                    case 2:
                        inputCurrency.setTextSize(20f);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        resultCurrency = (TextInputEditText) findViewById(R.id.resultCurrency);
        resultCurrency.setEnabled(false);

        convertButton = (Button) findViewById(R.id.convertButton);

    }

    private void SetupSelectCurrency () {

//        Default From To Currency
        Currency defaultCurrency = currencyList.get(0);
        fromCurrency = defaultCurrency.getCode();
        fromCurrencyName.setText(defaultCurrency.getName());
        fromCurrencyCode.setText(defaultCurrency.getCode());

        toCurrency = defaultCurrency.getCode();
        toCurrencyName.setText(defaultCurrency.getName());
        toCurrencyCode.setText(defaultCurrency.getCode());

//        Get Select Currency Swipe
        selectFromCurrency = (RecyclerView) findViewById(R.id.selectFromCurrency);
        selectToCurrency = (RecyclerView) findViewById(R.id.selectToCurrency);

//        Set Value to Select Currency Swipe
        selectFromCurrency.setAdapter(new CurrencyAdapter(currencyList, new CurrencyAdapter.Callback() {
            @Override
            public void onItemClick(View v) {
                selectFromCurrency.smoothScrollToPosition(selectFromCurrency.getChildLayoutPosition(v));
            }
        }));
        selectToCurrency.setAdapter(new CurrencyAdapter(currencyList, new CurrencyAdapter.Callback() {
            @Override
            public void onItemClick(View v) {
                selectToCurrency.smoothScrollToPosition(selectToCurrency.getChildLayoutPosition(v));
            }
        }));

//        Style Select Currency Swipe
        Integer padding = (int) ScreenUtils.getScreenWidth(this)/2 - ScreenUtils.dpToPx(this,35);
        selectFromCurrency.setPadding(padding, 0, padding, 0);
        selectToCurrency.setPadding(padding, 0, padding, 0);

        // Setting layout manager
        selectFromCurrency.setLayoutManager(new SliderLayoutManager(this, LinearLayoutManager.HORIZONTAL, false, new SliderLayoutManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Integer layoutPosition) {
                Currency currency = currencyList.get(layoutPosition);
//                Show Currency to View
                fromCurrency = currency.getCode();
                fromCurrencyName.setText(currency.getName());
                fromCurrencyCode.setText(currency.getCode());
                Log.i("From Currency ===>", currency.getName()+" - "+currency.getCode());
            }
        }));
        selectToCurrency.setLayoutManager(new SliderLayoutManager(this, LinearLayoutManager.HORIZONTAL, false, new SliderLayoutManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Integer layoutPosition) {
                Currency currency = currencyList.get(layoutPosition);
//                Show Currency to View
                toCurrency = currency.getCode();
                toCurrencyName.setText(currency.getName());
                toCurrencyCode.setText(currency.getCode());
                Log.i("To Currency ===>", currency.getName()+" - "+currency.getCode());
            }
        }));
    }

    private void CreateCurrencyList () {
        currencyList = new ArrayList<>();
        try {
            JSONArray currencies = new JSONArray(loadJSONFromAsset(getApplicationContext()));
            for (int i = 0; i < currencies.length(); i++) {
                JSONObject jsonobject = currencies.getJSONObject(i);
                String name = jsonobject.getString("name");
                String code = jsonobject.getString("code");
                currencyList.add(new Currency(name, code));
            }
        } catch (Exception e) {
            Log.i("Error", e.getMessage());
        }
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("currency.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

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
            if(urlLink.isEmpty()){
                return false;
            }
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

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected  void onPostExecute (Boolean success) {
//            mSwipeLayout.setRefreshing(false);

            if(success) {
                Double currency = Double.parseDouble(inputCurrency.getText().toString());
                double result = (currency*currencyRate);
                switch ((int)(String.valueOf(result).length()/5)) {
                    case 1:
                        resultCurrency.setTextSize(30f);
                        break;
                    case 2:
                        resultCurrency.setTextSize(20f);
                        break;
                    default:
                        break;
                }
                resultCurrency.setText(String.format(Locale.ENGLISH, "%,f" , result));
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
