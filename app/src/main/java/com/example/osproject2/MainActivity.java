package com.example.osproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //Variables
    Country c;
    State[] stateList;
    String[] states = {"al", "ak", "az", "ar",
            "ca", "co", "ct", "de", "fl", "ga",
            "hi", "id", "il", "in", "ia", "ks",
            "ky", "la", "me", "md", "ma", "mi",
            "mn", "ms", "mo", "mt", "ne", "nv",
            "nh", "nj", "nm", "ny", "nc", "nd",
            "oh", "ok", "or", "pa", "ri", "sc",
            "sd", "tn", "tx", "ut", "vt", "va",
            "wa", "wv", "wi", "wy"};
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1000;
    // Sets the Time Unit to Milliseconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
    int stateCount;
    int currentLoaded;
    int activeFragment;

    int[][] top5States = new int[5][2]; //Each index has a pair of two ints. The first int is the index of the state, the second is the number of cases

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BackgroundFragment f = (BackgroundFragment) this.getSupportFragmentManager().findFragmentById(R.id.fragment);

        //Get data
        Handler myHandler = new Handler() {
            @Override
            public void handleMessage (Message msg) {
                if(currentLoaded == 49){
                    //Determine the top 5 states
                    //fill the array with the first 5 states in the list
                    for(int i = 0; i < 5; i++){
                        top5States[i] = new int[]{i, stateList[i].getNewCases()};
                    }

                    //Go through the rest of the states, and add/remove states as necessary
                    for(int i = 5; i < stateList.length; i++){
                        //go through the list
                        boolean greaterValue = false;
                        for(int j = 0; j < top5States.length; j++){
                            if(top5States[j][1] < stateList[i].getNewCases()){
                                greaterValue = true;
                            }
                        }

                        if(greaterValue){
                            //Select a victim state
                            int minStateIndex = 0;
                            int minStateValue = top5States[0][1];
                            for(int j = 1; j < top5States.length; j++){
                                if(top5States[j][1] < minStateValue){
                                    minStateIndex = j;
                                    minStateValue = top5States[j][1];
                                }
                            }
                            //Replace victim with current state
                            top5States[minStateIndex] = new int[]{i, stateList[i].getNewCases()};
                        }
                    }

                    //Sort list
                    for(int i = 1; i < top5States.length; i++){
                        int currentIndex = i;
                        int previous = i-1;
                        while(previous >=0 && top5States[currentIndex][1] < top5States[previous][1]){
                            //Swap previous and current
                            int tempIndex = top5States[currentIndex][0];
                            int tempValue = top5States[currentIndex][1];

                            top5States[currentIndex][0] = top5States[previous][0];
                            top5States[currentIndex][1] = top5States[previous][1];

                            top5States[previous][0] = tempIndex;
                            top5States[previous][1] = tempValue;

                            //decrement
                            currentIndex--;
                            previous--;
                        }
                    }

                    //Update the main page if necessary
                    if(activeFragment == 0){
                        // Reload current fragment
                        getSupportFragmentManager().popBackStack();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Country", (Serializable) c);

                        if(currentLoaded == 49){
                            //Get top 5 states
                            State[] top5 = new State[5];
                            for(int i = 0; i < top5.length; i++){
                                top5[i] = stateList[top5States[i][0]];
                            }

                            bundle.putSerializable("States", top5);
                        }

                        MainFragment tabFragment = new MainFragment();
                        tabFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().remove(f);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, tabFragment).addToBackStack(null).commit();
                    }
                } else{
                    currentLoaded++;
                }
            }
        };

        Handler myHandler2 = new Handler() {
            @Override
            public void handleMessage (Message msg) {
                if(activeFragment == 0){
                    // Reload current fragment
                    getSupportFragmentManager().popBackStack();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Country", (Serializable) c);

                    if(currentLoaded == 49){
                        //Get top 5 states
                        State[] top5 = new State[5];
                        for(int i = 0; i < top5.length; i++){
                            top5[i] = stateList[top5States[i][0]];
                        }

                        bundle.putSerializable("States", top5);
                    }

                    MainFragment tabFragment = new MainFragment();
                    tabFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().remove(f);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, tabFragment).addToBackStack(null).commit();
                }
            }
        };

        ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,   // Initial pool size
                NUMBER_OF_CORES,   // Max pool size
                KEEP_ALIVE_TIME,       // Time idle thread waits before terminating
                KEEP_ALIVE_TIME_UNIT,  // Sets the Time Unit for KEEP_ALIVE_TIME
                new LinkedBlockingDeque<Runnable>());  // Work Queue

        c = new Country(this, myHandler2);
        mThreadPoolExecutor.execute(c);

        currentLoaded = 0;
        stateList = new State[states.length];
        stateCount = stateList.length;

        for(int i = 0; i < stateList.length; i++){
            stateList[i] = new State(states[i], this, myHandler);
            mThreadPoolExecutor.execute(stateList[i]);
        }

        mThreadPoolExecutor.shutdown();

        TabLayout tabs = (TabLayout)findViewById(R.id.tab_layout);

        //Add main fragment

        getSupportFragmentManager().popBackStack();
        Bundle bundle = new Bundle();
        MainFragment tabFragment = new MainFragment();
        tabFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().remove(f);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, tabFragment).addToBackStack(null).commit();
        activeFragment = 0;


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()  {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    activeFragment = 0;
                    Log.d("tabClick", "0");
                    //getFragmentManager().popBackStackImmediate();
                    getSupportFragmentManager().popBackStack();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Country", (Serializable) c);

                    if(currentLoaded == stateCount){
                        //Get top 5 states
                        State[] top5 = new State[5];
                        for(int i = 0; i < top5.length; i++){
                            top5[i] = stateList[top5States[i][0]];
                        }

                        bundle.putSerializable("States", top5);
                    }

                    MainFragment tabFragment = new MainFragment();
                    tabFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().remove(f);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, tabFragment).addToBackStack(null).commit();
                } else{
                    activeFragment = 1;
                    Log.d("tabClick", "1");
                    getSupportFragmentManager().popBackStack();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("States", (Serializable) stateList);
                    StatesFragment tabFragment = new StatesFragment();
                    tabFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().remove(f);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, tabFragment).addToBackStack(null).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}