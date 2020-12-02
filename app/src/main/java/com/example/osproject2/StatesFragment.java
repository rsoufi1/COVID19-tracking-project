package com.example.osproject2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatesFragment extends Fragment {

    //Variables
    State[] stateList;

    public StatesFragment() {
        // Required empty public constructor
    }

    public static StatesFragment newInstance(String param1, String param2) {
        StatesFragment fragment = new StatesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_states, container, false);

        TableLayout tableLayout = (TableLayout)view.findViewById(R.id.table2);
        tableLayout.setGravity(Gravity.TOP);
        tableLayout.setStretchAllColumns(true);

        //Get states
        Bundle args = getArguments();
        stateList = (State[]) args.getSerializable("States");

        //Add rows
        for(int i = 0; i < stateList.length; i++){
            TableRow tableRow = new TableRow(container.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            lp.width = TableRow.LayoutParams.MATCH_PARENT;
            tableRow.setGravity(Gravity.TOP);
            tableRow.setLayoutParams(lp);

            TextView stateName = new TextView(container.getContext());
            stateName.setText(stateList[i].getName());
            stateName.setGravity(Gravity.LEFT);
            stateName.setTextSize(24);

            int finalI = i;
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {

                    PopUpState popUpClass = new PopUpState();
                    popUpClass.PopUpWindow(v, stateList[finalI]);
                }
            } );

            tableRow.addView(stateName);
            tableLayout.addView(tableRow);
        }

        return view;
    }
}