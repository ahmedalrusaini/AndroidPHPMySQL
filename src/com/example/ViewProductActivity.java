package com.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewProductActivity extends Activity {

    Button btnEdit;
    Button btnDelete;
    String pid;
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // JSON node keys
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PID = "pid";

    private static final String url_delete_product = "http://mariusbob.ro/android/delete_product.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        Intent i = getIntent();
        pid = i.getStringExtra(TAG_PID);

        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                Intent in = new Intent(getApplicationContext(), EditProductActivity.class);
                startActivity(in);
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                new DeleteProduct().execute();
            }
        });


        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String name = in.getStringExtra(TAG_NAME);
        String description = in.getStringExtra(TAG_DESCRIPTION);
        String price = in.getStringExtra(TAG_PRICE);

        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.product_label);
        TextView lblPrice = (TextView) findViewById(R.id.price_label);
        TextView lblDesc = (TextView) findViewById(R.id.description_label);

        lblName.setText(name);
        lblDesc.setText(description);
        lblPrice.setText(price);
    }

    /*****************************************************************
     * Background Async Task to Delete Product
     * */
    class DeleteProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewProductActivity.this);
            pDialog.setMessage("Deleting Product...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_product, "POST", params);


                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }
    //Display the Option menu when pressing the Menu button from the device
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater blowUp = getMenuInflater();
        blowUp.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.view_products:
                Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                startActivity(i);
                break;
            case R.id.add_product:
                Intent intent = new Intent(getApplicationContext(), NewProductActivity.class);
                startActivity(intent);
                break;
            case R.id.home:
                Intent main = new Intent(getApplicationContext(), MainScreenActivity.class);
                startActivity(main);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}