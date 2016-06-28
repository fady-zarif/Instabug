package com.example.foda_.instabugdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment  {

    SharedPreferences.Editor editor;
    ListView listView ;
    static int m = 0;
    int Page=0;

    public Button button;
    DataFromJson dataFromJson;
    public SwipeRefreshLayout swipeRefreshLayout;
    CustomAdapter customAdapter;
    ArrayList<DataFromJson> array_data=new ArrayList<>();

    ProgressDialog progressDialog ;
    public MainActivityFragment() {
    }

    @Override
    public void onStart() { // will execute the get repo when start
        super.onStart();
        Get_Repo get_repo=new Get_Repo();
        Page=1;
        get_repo.execute("https://api.github.com/users/square/repos?page=1&per_page=100&access_token=f73100f912cca071b610d9d598525291ae048a9b");
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo X=cm.getActiveNetworkInfo();
        return X!=null;
    }
////////////////////////////////////
    // Class that will run in the background thread
    public class Get_Repo extends AsyncTask <String ,Void,ArrayList<DataFromJson>>implements SwipeRefreshLayout.OnRefreshListener
    {
        HttpURLConnection urlConnection=null;
        BufferedReader bufferedReader=null;
        String line="";
        String jsonString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout=(SwipeRefreshLayout)getActivity().findViewById(R.id.swiperefresh);
            swipeRefreshLayout.setOnRefreshListener(this);
            button = new Button(getContext());
            button.setText("Load More");
        }
        @Override
        protected ArrayList<DataFromJson> doInBackground(String... params) {
            m =m+10;
            try {
                // first make connection
                if (isNetworkConnected()) {
                    URL url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    Log.e("aa",inputStream.toString());
                    StringBuffer buffer = new StringBuffer();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    if (bufferedReader == null) {
                        return null;
                    }
                    while ((line = bufferedReader.readLine()) != null ) // read lines that stored in buffer reader
                    {
                        buffer.append(line + "\n"); // but them in string buffer
                    }
                    if (buffer == null) {
                        return null;
                       }
                    jsonString = buffer.toString();
                    if (jsonString.toString() != null) {
                        // saving data into Shared Preferences
                        editor = getActivity().getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE).edit();
                        if (Page == 1)
                        {
                        editor.putString("jsonString", jsonString); // to store the first page
                        editor.commit();
                        }
                            else if(Page==2)
                            {
                                editor.putString("jsonString2", jsonString); // to store the second page
                                editor.commit();
                            }
                    }
                    Log.i("json is", jsonString);
                }else
                {   // if it is no connection to the internet so will load data that are stored in shared preferences
                    if (Page==1) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
                        jsonString = sharedPreferences.getString("jsonString", "No data").toString();
                    }
                    else {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
                        jsonString = sharedPreferences.getString("jsonString2", "No data").toString();
                    }
                }
                // parsing json
                JSONArray jsonArray =new JSONArray(jsonString);
                Log.i("json_length",String.valueOf(jsonArray.length()));
                for (int i=m-10; i<m ; i++)
                {// i will take all object from json array then will store them in variables and pass them to object from DataFromJson class

                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    String name=jsonObject.getString("name");
                    String desc=jsonObject.getString("description");
                    boolean fork=jsonObject.getBoolean("fork");
                    String repo_url=jsonObject.getString("html_url");
                    JSONObject jsonObject1=jsonObject.getJSONObject("owner");
                    String owner_Url=jsonObject1.getString("html_url");
                    String username=jsonObject1.getString("login");
                    dataFromJson=new DataFromJson(name,desc,username,fork,repo_url,owner_Url);
                    array_data.add(dataFromJson); // add them to array_data
                    if (m>=jsonArray.length())
                    {// if m variable >= array.length that is mean that first page has finished
                        Page++; // page ++
                        m=0;    // reset variable m to start from 0 with new page
                        Log.e("Page number", String .valueOf(Page));
                        return array_data;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return array_data;
        }
        @Override
        protected void onPostExecute(ArrayList strings) {

            swipeRefreshLayout.setRefreshing(false);
            listView = (ListView) getActivity().findViewById(R.id.MyListView);
            super.onPostExecute(strings);

            if (array_data.size() < 10) { // if array_data that contain json data < 10
                // so will display all item
                customAdapter = new CustomAdapter(getActivity(),array_data);
                listView.setAdapter(customAdapter);
            }
            else if (array_data.size()==10)
            { // array_data == 10 that is mean that it can be more data available so will create footer button "load more"
                customAdapter = new CustomAdapter(getActivity(),array_data);
                listView.removeFooterView(button);
                if (listView.getFooterViewsCount()==0)
                {
                    listView.addFooterView(button);
                }
                listView.setAdapter(customAdapter);
            }
            else  {

                if (listView.getFooterViewsCount()==0)
                {
                    listView.addFooterView(button);
                }
                progressDialog.dismiss();
                customAdapter.notifyDataSetChanged();
                }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog =new ProgressDialog(getContext());
                    progressDialog.setMessage("Loading ... "); // progressdialog to run until get the data from json
                    progressDialog.setCanceledOnTouchOutside(false);
                    Get_Repo get_repo=new Get_Repo(); //take object from class Get0Repo
                    listView.removeFooterView(button); // remove footer button


                    if (Page==1) { // if Page still 1 so will execute the page1json url
                        get_repo.execute("https://api.github.com/users/square/repos?page=1&per_page=100&access_token=f73100f912cca071b610d9d598525291ae048a9b") ;
                        progressDialog.show();
                             }

                    else if(Page==2) {
                            get_repo.execute("https://api.github.com/users/square/repos?page=2&per_page=100&access_token=f73100f912cca071b610d9d598525291ae048a9b");
                            progressDialog.show();
                           }
                    else{
                        Toast.makeText(getActivity(), "No More Data", Toast.LENGTH_SHORT).show();
                        listView.removeFooterView(button);
                       }
                    customAdapter.notifyDataSetChanged();

                }
            });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        // item long click
                        // build an alert Dialog with two buttons
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setPositiveButton("Repo Url", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // get the repo_html_url from json array_data
                                // and pass it to implicit intent to start Url in browser
                                String url = array_data.get(position).repo_url.toString();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("User Url", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // get the Owner_html_url from json array_data
                                // and pass it to implicit intent to start Url in browser
                                String url = array_data.get(position).user_url.toString();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                            }
                        });
                        builder.setTitle("Select:");
                        builder.show();
                        return true;
                    }
                });
        }
        @Override
        public void onRefresh() {
            m=0; // will reset variable m  to start from 0 again
            array_data.clear(); // clear all array data to store the new data
            onStart(); // call onstart to execute page1 json url
        }
    }


}

