package com.example.osproject2;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;

public class State implements Runnable, Serializable {
    //Variables
    private String name;
    private int date;
    private String dateText;
    private int deathIncrease;
    private int hospitalizedCurrently;
    private int onVentilatorCurrently;
    private int newCases;
    private LinkedList<Integer> casesPerDay;
    private LinkedList<Integer> dates;
    private LinkedList<Integer> xValues;
    private Context context;
    private Handler handler;

    //
    public State(Context con, Handler handle){
        this.name = "not yet filled";
        this.date = -1;
        this.deathIncrease = -1;
        this.hospitalizedCurrently = -1;
        this.onVentilatorCurrently = -1;
        this.newCases = -1;
        this.casesPerDay = new LinkedList<>();
        this.dates = new LinkedList<>();
        this.xValues = new LinkedList<>();
        this.context = con.getApplicationContext();
        this.handler = handle;
    }

    public State(String n, Context con, Handler handle){
        this.name = n;
        this.date = -1;
        this.deathIncrease = -1;
        this.hospitalizedCurrently = -1;
        this.onVentilatorCurrently = -1;
        this.newCases = -1;
        this.casesPerDay = new LinkedList<>();
        this.dates = new LinkedList<>();
        this.xValues = new LinkedList<>();
        this.context = con.getApplicationContext();
        this.handler = handle;
    }

    @Override public void run() {
        // Do some work that takes 50 milliseconds
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("myTag", "the thread is done: " + name);
        getInfo();
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

    public void getInfo(){
        String url = "https://api.covidtracking.com/v1/states/" + this.name + "/current.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getFirstInfo(response);
                        Log.d("State", "created");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("error", error.getMessage());
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this.context).addToRequestQueue(jsonObjectRequest);

        String url2 = "https://api.covidtracking.com/v1/states/" + this.name + "/daily.json";
        JsonArrayRequest request2 = new JsonArrayRequest
                (Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        addGraph(response);
                        Log.d("State", "created");

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        //error.getMessage()
                        Log.d("error", error.getMessage());
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this.context).addToRequestQueue(request2);
    }

    public void getFirstInfo(JSONObject response){
        this.date = 0;
        this.deathIncrease = 0;
        this.hospitalizedCurrently = 0;
        this.onVentilatorCurrently = 0;
        this.newCases = 0;
        this.casesPerDay = new LinkedList<>();

        JSONArray r = response.names();

        for(int i = 0; i < r.length(); i++){
            try {
                if(r.getString(i).equals("date")){
                    this.date = response.getInt(r.getString(i));
                    int day = this.date%100;
                    int month = (this.date%10000 - day)/100;
                    int year = this.date/10000;
                    this.dateText = month + "/" + day + "/"  + year;
                }
                if(r.getString(i).equals("deathIncrease")){
                    this.deathIncrease = response.getInt(r.getString(i));
                }
                if(r.getString(i).equals("hospitalizedCurrently")){
                    this.hospitalizedCurrently = response.getInt(r.getString(i));
                }
                if(r.getString(i).equals("onVentilatorCurrently")){
                    this.onVentilatorCurrently = response.getInt(r.getString(i));
                }
                if(r.getString(i).equals("positiveIncrease")){
                    this.newCases = response.getInt(r.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override public String toString(){
        String s = "";
        s+= "name: " + this.name + ", ";
        s+= "date: " + this.date + ", ";
        s+= "dateText: " + this.dateText + ", ";
        s+= "deathIncrease: " + this.deathIncrease + ", ";
        s+= "hospitalizedCurrently: " + this.hospitalizedCurrently + ", ";
        s+= "onVentilatorCurrently: " + this.onVentilatorCurrently + ", ";
        s+= "newCases: " + this.newCases;

        s+= "cases per day: ";
        for(int i = 0; i < this.casesPerDay.size(); i++){
            s+= casesPerDay.get(i) + ", ";
        }
        return s;
    }

    public String getName(){return name;}

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
    public String getDate(){return dateText;}

    public int getDeathIncrease(){return deathIncrease;}
    public int getHospitalizedCurrently(){return hospitalizedCurrently;}
    public int getOnVentilatorCurrently(){return onVentilatorCurrently;}
    public int getNewCases(){return newCases;}


}
