package com.example.osproject2;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class Country implements Runnable, Serializable {
    //Variables
    private LinkedList<Integer> casesPerDay;
    private LinkedList<Integer> dates;
    private LinkedList<Integer> xValues;
    private Context context;
    private Handler handler;

    //Initializers
    public Country(Context con, Handler handle){
        this.casesPerDay = new LinkedList<>();
        this.dates = new LinkedList<>();
        this.xValues = new LinkedList<>();
        this.context = con.getApplicationContext();
        this.handler = handle;
    }

    public Country(){
        this.casesPerDay = new LinkedList<>();
        this.dates = new LinkedList<>();
        this.xValues = new LinkedList<>();
    }


    @Override public void run() {
        // Do some work that takes 50 milliseconds
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            getInfo();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getInfo() throws IOException, JSONException {
        String url2 = "https://api.covidtracking.com/v1/us/daily.json";
        JSONArray a = getJSONArrayFromURL(url2);
        addGraph(a);
        /*JsonArrayRequest request2 = new JsonArrayRequest
                (Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //addGraph(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        //error.getMessage()

                        //print the error message
                        for(int i = 0; i < error.getMessage().length(); i+=100){
                            Log.d("error", error.getMessage().substring(i));
                        }
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this.context).addToRequestQueue(request2);*/

    }

    public void addGraph(JSONArray response){
        int length = response.length();
        //Initialize the rest of the casesPerDay
        for(int i = 0; i < length; i++){
            try{
                JSONObject o = response.getJSONObject(i);
                JSONArray names = o.names();
                int j = 0;
                boolean found = false;
                boolean foundDate = false;
                while(j < names.length() && (!found || !foundDate)){
                    if(names.getString(j).equals("positiveIncrease")){
                        this.casesPerDay.addFirst(o.getInt(names.getString(j)));
                        found = true;
                    }
                    if(names.getString(j).equals("date")){
                        this.dates.addFirst(o.getInt(names.getString(j)));
                        foundDate = true;
                    }
                    j++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        createGraph();

        Log.d("addGraph", "done");
        //Log.d("results", toString());
        this.handler.sendEmptyMessage(0);
    }

    @Override public String toString(){
        String s = "";
        s+= "cases per day and xValue: ";
        for(int i = 0; i < this.casesPerDay.size(); i++){
            s+= "day: " + this.xValues.get(i) + " cases: " + casesPerDay.get(i) + ", \n";
        }
        return s;
    }

    public void createGraph(){
        int firstDay = this.dates.getFirst();
        int lastDay = this.dates.getLast();

        //Calculate the first day in number of days
        int dayFirst = firstDay%100;
        int monthFirst = (firstDay%10000 - dayFirst)/100;
        int yearFirst = firstDay/10000;
        firstDay = getDays(yearFirst, monthFirst, dayFirst);

        //Calculate each day into the number of day it is in the year
        for(int i = 0; i < this.dates.size(); i++){
            int date = this.dates.get(i);
            int day = date%100;
            int month = (date%10000 - day)/100;
            int year = date/10000;
            int totalDays = getDays(year, month, day);
            int xValue = totalDays - firstDay + 1;
            xValues.add(xValue);
        }
    }

    public int getDays(int year, int month, int day){
        int currentMonth = 1;
        int numberOfDays = 0;
        while(currentMonth < month){
            if(currentMonth == 1) numberOfDays+= 31; //Janurary
            else if(currentMonth == 2){ //Feburary
                if(year%4 == 0) numberOfDays+= 29;//leap year
                else numberOfDays+=28; //normal year
            } else if(currentMonth == 3) numberOfDays+= 31; //March
            else if(currentMonth == 4) numberOfDays+= 30; //April
            else if(currentMonth == 5) numberOfDays+= 31; //May
            else if(currentMonth == 6) numberOfDays+= 30; //June
            else if(currentMonth == 7) numberOfDays+= 31; //July
            else if(currentMonth == 8) numberOfDays+= 31; //August
            else if(currentMonth == 9) numberOfDays+= 30; //September
            else if(currentMonth == 10) numberOfDays+= 31; //October
            else if(currentMonth == 11) numberOfDays+= 30; //November
            else if(currentMonth == 12) numberOfDays+= 31; //December

            currentMonth++;
        }
        numberOfDays+= day;
        return numberOfDays;
    }

    public LinkedList<Integer> getCases(){return casesPerDay;}
    public LinkedList<Integer> getxValues(){return xValues;}

    public static JSONArray getJSONArrayFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();

        return new JSONArray(jsonString);
    }
}
