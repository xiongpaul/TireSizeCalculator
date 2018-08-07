package io.github.xiongpaul.tiresizecalculator;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddCarActivity extends Activity {

    private int year = 1950;
    private int treadWidth = 135;
    private int aspectRatio = 20;
    private int rimDiameter = 12;

    private CarDB db;

    private List<String> year_array_list;
    private List<String> tread_width_array_list;
    private List<String> aspect_ratio_array_list;
    private List<String> rim_diameter_array_list;

    private Spinner yearSpinner;
    private Spinner treadWidthSpinner;
    private Spinner aspectRatioSpinner;
    private Spinner rimDiameterSpinner;

    private ArrayAdapter yearAdapter;
    private ArrayAdapter treadWidthAdapter;
    private ArrayAdapter aspectRatioAdapter;
    private ArrayAdapter rimDiameterAdapter;

    private EditText makeEditText;
    private EditText modelEditText;

    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        db = new CarDB(this);
        savedValues = getSharedPreferences("savedValues", MODE_PRIVATE);

        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        treadWidthSpinner = (Spinner) findViewById(R.id.treadWidthSpinner);
        aspectRatioSpinner = (Spinner) findViewById(R.id.aspectRatioSpinner);
        rimDiameterSpinner = (Spinner) findViewById(R.id.rimDiameterSpinner);

        makeEditText = (EditText) findViewById(R.id.makeEditText);
        modelEditText = (EditText) findViewById(R.id.modelEditText);

        year_array_list = new ArrayList<String>();
        tread_width_array_list = new ArrayList<String>();
        aspect_ratio_array_list = new ArrayList<String>();
        rim_diameter_array_list = new ArrayList<String>();

        // populate year spinner
        do {
            String yearString = String.valueOf(year);
            year_array_list.add(yearString);
            year = year + 1;
        } while (year <= 2017);
        yearAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                year_array_list);
        yearSpinner.setAdapter(yearAdapter);

        // populate tread width spinner
        do {
            String treadWidthString = String.valueOf(treadWidth);
            tread_width_array_list.add(treadWidthString);
            treadWidth = treadWidth + 10;
        } while (treadWidth <= 375);
        treadWidthAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                tread_width_array_list);
        treadWidthSpinner.setAdapter(treadWidthAdapter);

        // populate aspect ratio spinner
        do {
            String aspectRatioString = String.valueOf(aspectRatio);
            aspect_ratio_array_list.add(aspectRatioString);
            aspectRatio = aspectRatio + 5;
        } while (aspectRatio <= 85);
        aspectRatioAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                aspect_ratio_array_list);
        aspectRatioSpinner.setAdapter(aspectRatioAdapter);

        // populate rim diameter spinner
        do {
            String rimDiameterString = String.valueOf(rimDiameter);
            rim_diameter_array_list.add(rimDiameterString);
            rimDiameter = rimDiameter + 1;
        } while (rimDiameter <= 30);
        rimDiameterAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                rim_diameter_array_list);
        rimDiameterSpinner.setAdapter(rimDiameterAdapter);
    }

    @Override
    protected void onPause() {
        //declare variables for data storage and assign values
        String makeData, modelData;

        makeData = makeEditText.getText().toString();
        modelData = modelEditText.getText().toString();

        // open SharedPreferences editor and assign data then commit changes
        Editor editor = savedValues.edit();
        editor.putString("makeData", makeData);
        editor.putString("modelData", modelData);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // declare and define variables for data retrieval
        String makeData, modelData;

        // retrieve data
        makeData = savedValues.getString("makeData", "");
        modelData = savedValues.getString("modelData", "");

        // set widgets to same value as saved data
        makeEditText.setText(makeData);
        modelEditText.setText(modelData);

    }

    public void addToGarage(View v){
        String make, model;
        int year, treadWidth, aspectRatio, rimDiameter;

        make = makeEditText.getText().toString();
        model = modelEditText.getText().toString();

        year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
        treadWidth = Integer.parseInt(treadWidthSpinner.getSelectedItem().toString());
        aspectRatio = Integer.parseInt(aspectRatioSpinner.getSelectedItem().toString());
        rimDiameter = Integer.parseInt(rimDiameterSpinner.getSelectedItem().toString());

        Car car = new Car(year, make, model, treadWidth, aspectRatio, rimDiameter);
        db.insertCar(car);

        Toast toast = Toast.makeText(this, year + " " + make + " " + model + " added to Garage!",
                Toast.LENGTH_LONG);
        toast.show();

        Intent intent = new Intent(this, GarageActivity.class);
        this.startActivity(intent);

    }

}
