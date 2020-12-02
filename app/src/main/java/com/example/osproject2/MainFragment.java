package com.example.osproject2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1000;
    // Sets the Time Unit to Milliseconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
    Country c;
    State[] s;

    public MainFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //TextView textView = (TextView)view.findViewById(R.id.textView2);
        TableLayout tableLayout = (TableLayout)view.findViewById(R.id.table);
        tableLayout.setGravity(Gravity.TOP);
        tableLayout.setStretchAllColumns(true);
        Graph graph = (Graph)view.findViewById(R.id.graph);
        graph.hewwow();

        //Get country
        Bundle args = getArguments();
        c = (Country) args.getSerializable("Country");
        s = (State[]) args.getSerializable("States");
        if(c != null){
            graph.addValues(c.getxValues(), c.getCases());
        }
        graph.refreshDrawableState();
        graph.invalidate();

        //Add rows
        if(s != null){
            for(int i = s.length - 1; i >=0; i--){
                TableRow tableRow = new TableRow(container.getContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                lp.width = TableRow.LayoutParams.MATCH_PARENT;
                tableRow.setGravity(Gravity.TOP);
                tableRow.setLayoutParams(lp);

                TextView stateName = new TextView(container.getContext());
                stateName.setText(s[i].getName());
                stateName.setGravity(Gravity.LEFT);
                stateName.setTextSize(20);

                TextView numCases = new TextView(container.getContext());
                numCases.setText(String.valueOf(s[i].getNewCases()));
                numCases.setGravity(Gravity.RIGHT);
                numCases.setTextSize(20);

                tableRow.addView(stateName);
                tableRow.addView(numCases);
                tableLayout.addView(tableRow);
            }
        } else{
            TableRow tableRow = new TableRow(container.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            lp.width = TableRow.LayoutParams.MATCH_PARENT;
            tableRow.setGravity(Gravity.TOP);
            tableRow.setLayoutParams(lp);

            TextView stateName = new TextView(container.getContext());
            stateName.setText("Loading...");
            stateName.setGravity(Gravity.LEFT);
            stateName.setTextSize(20);

            tableRow.addView(stateName);
            tableLayout.addView(tableRow);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}