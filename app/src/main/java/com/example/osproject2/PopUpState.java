package com.example.osproject2;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PopUpState {
    public void PopUpWindow(final View view, State state){
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.state_popup, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Set up graph
        Graph graph = (Graph)popupView.findViewById(R.id.graph3);
        if(state.getxValues() != null){
            graph.hewwow();
            graph.addValues(state.getxValues(), state.getCases());
            graph.refreshDrawableState();
            graph.invalidate();

            //Set up table
            TableLayout tableLayout = (TableLayout)popupView.findViewById(R.id.table_popup);
            tableLayout.setGravity(Gravity.TOP);
            tableLayout.setStretchAllColumns(true);

            //Add rows
            //Name of state
            TableRow tableRow = new TableRow(popupView.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setGravity(Gravity.TOP);
            tableRow.setLayoutParams(lp);

            TextView text = new TextView(view.getContext());
            text.setText("State: " + state.getName());
            //Log.d("popUp", state.getName());
            text.setPadding(0, 5, 0, 5);
            text.setGravity(Gravity.LEFT);
            text.setTextSize(20);

            tableRow.addView(text);
            tableLayout.addView(tableRow);

            //Date
            TableRow tableDate = new TableRow(popupView.getContext());
            tableDate.setLayoutParams(lp);

            TextView dateText = new TextView(view.getContext());
            dateText.setText("Date: " + state.getDate());
            dateText.setPadding(0, 5, 0, 5);
            dateText.setGravity(Gravity.LEFT);
            dateText.setTextSize(20);

            tableDate.addView(dateText);
            tableLayout.addView(tableDate);


            //New cases
            TableRow newCasesRow = new TableRow(popupView.getContext());
            newCasesRow.setLayoutParams(lp);

            TextView newCasesText = new TextView(view.getContext());
            newCasesText.setText("New Cases: " + state.getNewCases());
            newCasesText.setPadding(0, 5, 0, 5);
            newCasesText.setGravity(Gravity.LEFT);
            newCasesText.setTextSize(20);

            newCasesRow.addView(newCasesText);
            tableLayout.addView(newCasesRow);


            //Death increase
            TableRow deathIncreaseRow = new TableRow(popupView.getContext());
            deathIncreaseRow.setLayoutParams(lp);

            TextView deathIncreaseText = new TextView(view.getContext());
            deathIncreaseText.setText("Death Increase: " + state.getDeathIncrease());
            deathIncreaseText.setPadding(0, 5, 0, 5);
            deathIncreaseText.setGravity(Gravity.LEFT);
            deathIncreaseText.setTextSize(20);

            deathIncreaseRow.addView(deathIncreaseText);
            tableLayout.addView(deathIncreaseRow);

            //Hospitalized currently
            TableRow hospitalizedCurrentlyRow = new TableRow(popupView.getContext());
            hospitalizedCurrentlyRow.setLayoutParams(lp);

            TextView hospCurrentText = new TextView(view.getContext());
            hospCurrentText.setText("Hospitalized Currently: " + state.getHospitalizedCurrently());
            hospCurrentText.setPadding(0, 5, 0, 5);
            hospCurrentText.setGravity(Gravity.LEFT);
            hospCurrentText.setTextSize(20);

            hospitalizedCurrentlyRow.addView(hospCurrentText);
            tableLayout.addView(hospitalizedCurrentlyRow);

            //on ventilator currently
            TableRow onVentCurrently = new TableRow(popupView.getContext());
            onVentCurrently.setLayoutParams(lp);

            TextView onVentText = new TextView(view.getContext());
            onVentText.setText("On Ventilator Currently: " + state.getOnVentilatorCurrently());
            onVentText.setPadding(0, 5, 0, 5);
            onVentText.setGravity(Gravity.LEFT);
            onVentText.setTextSize(20);

            onVentCurrently.addView(onVentText);
            tableLayout.addView(onVentCurrently);
        } else{
            graph.refreshDrawableState();
            graph.invalidate();

            //Set up table
            TableLayout tableLayout = (TableLayout)popupView.findViewById(R.id.table_popup);
            tableLayout.setGravity(Gravity.TOP);
            tableLayout.setStretchAllColumns(true);

            //Add rows
            TableRow tableRow = new TableRow(popupView.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setGravity(Gravity.TOP);
            tableRow.setLayoutParams(lp);

            TextView text = new TextView(view.getContext());
            text.setText("Loading...");
            text.setPadding(0, 5, 0, 5);
            text.setGravity(Gravity.LEFT);
            text.setTextSize(20);

            tableRow.addView(text);
            tableLayout.addView(tableRow);
        }


        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });

    }
}
