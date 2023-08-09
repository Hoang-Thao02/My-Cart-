package hanu.a2_2001040188;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hanu.a2_2001040188.Adapter.ProductAdapter;
import hanu.a2_2001040188.models.Constants.Constants;
import hanu.a2_2001040188.models.Product.Product;

public class MainActivity extends AppCompatActivity {

    ProductAdapter adapter;
    List<Product> products = new ArrayList<>();
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.ProductList);
        //?Task Chung
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        //?Load Product api
        String url = "https://hanu-congnv.github.io/mpr-cart-api/products.json";
        //Getting api
        Constants.executor.execute(new Runnable() {
            @Override
            public void run() {
                String json = loadJSON(url);
                //?Manipulate Ui with object from api
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (json == null) {
                            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONArray root = new JSONArray(json);

                            for (int i = 0; i < root.length(); i++) {
                                JSONObject jsonProduct = root.getJSONObject(i);
                                int id = jsonProduct.getInt("id");
                                String name = jsonProduct.getString("name");
                                String thumbnail = jsonProduct.getString("thumbnail");
                                double unitPrice = jsonProduct.getDouble("unitPrice");
                                Product product = new Product(id, thumbnail, name, unitPrice);
                                products.add(product);
                            }
                            adapter = new ProductAdapter(products, MainActivity.this);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,2);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        ImageButton searchBtn = findViewById(R.id.search_button);
        SearchView searchView = findViewById(R.id.searchProduct);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,  searchView.getQuery().toString(), Toast.LENGTH_SHORT).show();
                adapter.getFilter().filter(searchView.getQuery().toString());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    Thread.sleep(5000);
                    adapter.getFilter().filter(newText);
               } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

    }

    public String loadJSON(String link) {
        URL url;
        HttpURLConnection urlConnection;
        try {
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = urlConnection.getInputStream();
            urlConnection.connect();
            Scanner sc = new Scanner(is);
            StringBuilder result = new StringBuilder();
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                result.append(line);
            }
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.btnCart) {
            Intent myIntent = new Intent(MainActivity.this,CartActivity.class);
            Log.i(" ","Clicked");
            MainActivity.this.startActivity(myIntent);
            myIntent.putExtra("key", products.toString());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

