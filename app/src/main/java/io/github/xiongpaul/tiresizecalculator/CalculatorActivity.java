package io.github.xiongpaul.tiresizecalculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CalculatorActivity extends Activity {

    private int treadWidth = 135;
    private int aspectRatio = 20;
    private int rimDiameter = 12;

    private int id;

    private int oemTreadWidth;
    private int oemAspectRatio;
    private int oemRimDiameter;

    private int aftTreadWidth;
    private int aftAspectRatio;
    private int aftRimDiameter;

    private String oemTireSize;
    private double oemSidewall;
    private double oemTireDiameter;
    private double oemCircumference;
    private double oemRevKm;

    private String aftTireSize;
    private double aftSidewall;
    private double aftTireDiameter;
    private double aftCircumference;
    private double aftRevKm;

    private CarDB db;
    private StringBuilder sb;
    private Button calculateButton;

    private List<String> tread_width_array_list;
    private List<String> aspect_ratio_array_list;
    private List<String> rim_diameter_array_list;

    private Spinner treadWidthSpinner;
    private Spinner aspectRatioSpinner;
    private Spinner rimDiameterSpinner;

    private ArrayAdapter treadWidthAdapter;
    private ArrayAdapter aspectRatioAdapter;
    private ArrayAdapter rimDiameterAdapter;

    private TextView selectedVehicleTextView;
    private TextView oemTireSizeTextView;
    private TextView oemSectionWidthTextView;
    private TextView oemSidewallTextView;
    private TextView oemTireDiameterTextView;
    private TextView oemCircumTextView;
    private TextView oemRevKmTextView;

    private TextView aftTireSizeTextView;
    private TextView aftSectionWidthTextView;
    private TextView aftSidewallTextView;
    private TextView aftTireDiameterTextView;
    private TextView aftCircumTextView;
    private TextView aftRevKmTextView;

    private TextView diffPercentTextView;
    private TextView speedoDiffTextView;

    private DecimalFormat decFor;

    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        db = new CarDB(this);
        sb = new StringBuilder();
        savedValues = getSharedPreferences("savedValues", MODE_PRIVATE);

        treadWidthSpinner = (Spinner) findViewById(R.id.treadWidthSpinner);
        aspectRatioSpinner = (Spinner) findViewById(R.id.aspectRatioSpinner);
        rimDiameterSpinner = (Spinner) findViewById(R.id.rimDiameterSpinner);

        selectedVehicleTextView = (TextView) findViewById(R.id.selectedVehicleTextView);

        oemTireSizeTextView = (TextView) findViewById(R.id.oemTireSizeTextView);
        oemSectionWidthTextView = (TextView) findViewById(R.id.oemSectionWidthTextView);
        oemSidewallTextView = (TextView) findViewById(R.id.oemSidewallTextView);
        oemTireDiameterTextView = (TextView) findViewById(R.id.oemTireDiameterTextView);
        oemCircumTextView = (TextView) findViewById(R.id.oemCircumTextView);
        oemRevKmTextView = (TextView) findViewById(R.id.oemRevKmTextView);

        aftTireSizeTextView = (TextView) findViewById(R.id.aftTireSizeTextView);
        aftSectionWidthTextView = (TextView) findViewById(R.id.aftSectionWidthTextView);
        aftSidewallTextView = (TextView) findViewById(R.id.aftSidewallTextView);
        aftTireDiameterTextView = (TextView) findViewById(R.id.aftTireDiameterTextView);
        aftCircumTextView = (TextView) findViewById(R.id.aftCircumTextView);
        aftRevKmTextView = (TextView) findViewById(R.id.aftRevKmTextView);

        diffPercentTextView = (TextView) findViewById(R.id.diffPercentTextView);
        speedoDiffTextView = (TextView) findViewById(R.id.speedoDiffTextView);

        calculateButton = (Button) findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTireSize();
            }
        });

        tread_width_array_list = new ArrayList<String>();
        aspect_ratio_array_list = new ArrayList<String>();
        rim_diameter_array_list = new ArrayList<String>();

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

        getSelectedCarInfo();

    }

    @Override
    protected void onPause() {
        //declare variables for data storage and assign values
        String oemTireData, oemSectionData, oemSideData, oemDiamData, oemCirData, oemRevData;
        String aftTireData, aftSectionData, aftSideData, aftDiamData, aftCirData, aftRevData;
        String diffData, speedoData;
        int posTread, posAspect, posDiameter;

        posTread = treadWidthSpinner.getSelectedItemPosition();
        posAspect = aspectRatioSpinner.getSelectedItemPosition();
        posDiameter = rimDiameterSpinner.getSelectedItemPosition();

        oemTireData = oemTireSizeTextView.getText().toString();
        oemSectionData = oemSectionWidthTextView.getText().toString();
        oemSideData = oemSidewallTextView.getText().toString();
        oemDiamData = oemTireDiameterTextView.getText().toString();
        oemCirData = oemCircumTextView.getText().toString();
        oemRevData = oemRevKmTextView.getText().toString();

        aftTireData = aftTireSizeTextView.getText().toString();
        aftSectionData = aftSectionWidthTextView.getText().toString();
        aftSideData = aftSidewallTextView.getText().toString();
        aftDiamData = aftTireDiameterTextView.getText().toString();
        aftCirData = aftCircumTextView.getText().toString();
        aftRevData = aftRevKmTextView.getText().toString();

        diffData = diffPercentTextView.getText().toString();
        speedoData = speedoDiffTextView.getText().toString();

        // open SharedPreferences editor and assign data then commit changes
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("oemTireData", oemTireData);
        editor.putString("oemSectionData", oemSectionData);
        editor.putString("oemSideData", oemSideData);
        editor.putString("oemDiamData", oemDiamData);
        editor.putString("oemCirData", oemCirData);
        editor.putString("oemRevData", oemRevData);
        editor.putString("aftTireData", aftTireData);
        editor.putString("aftSectionData", aftSectionData);
        editor.putString("aftSideData", aftSideData);
        editor.putString("aftDiamData", aftDiamData);
        editor.putString("aftCirData", aftCirData);
        editor.putString("aftRevData", aftRevData);
        editor.putString("diffData", diffData);
        editor.putString("speedoData", speedoData);
        editor.putInt("posTread", posTread);
        editor.putInt("posAspect", posAspect);
        editor.putInt("posDiameter", posDiameter);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // declare and define variables for data retrieval
        String oemTireData, oemSectionData, oemSideData, oemDiamData, oemCirData, oemRevData;
        String aftTireData, aftSectionData, aftSideData, aftDiamData, aftCirData, aftRevData;
        String diffData, speedoData;
        int posTread, posAspect, posDiameter;

        // retrieve data
        oemTireData = savedValues.getString("oemTireData", "");
        oemSectionData = savedValues.getString("oemSectionData", "");
        oemSideData = savedValues.getString("oemSideData", "");
        oemDiamData = savedValues.getString("oemDiamData", "");
        oemCirData = savedValues.getString("oemCirData", "");
        oemRevData = savedValues.getString("oemRevData", "");
        aftTireData = savedValues.getString("aftTireData", "");
        aftSectionData = savedValues.getString("aftSectionData", "");
        aftSideData = savedValues.getString("aftSideData", "");
        aftDiamData = savedValues.getString("aftDiamData", "");
        aftCirData = savedValues.getString("aftCirData", "");
        aftRevData = savedValues.getString("aftRevData", "");
        posTread = savedValues.getInt("posTread", 0);
        posAspect = savedValues.getInt("posAspect", 0);
        posDiameter = savedValues.getInt("posDiameter", 0);
        diffData = savedValues.getString("diffData", "");
        speedoData = savedValues.getString("speedoData", "");

        // set widgets to same value as saved data
        oemTireSizeTextView.setText(oemTireData);
        oemSectionWidthTextView.setText(oemSectionData);
        oemSidewallTextView.setText(oemSideData);
        oemTireDiameterTextView.setText(oemDiamData);
        oemCircumTextView.setText(oemCirData);
        oemRevKmTextView.setText(oemRevData);

        aftTireSizeTextView.setText(aftTireData);
        aftSectionWidthTextView.setText(aftSectionData);
        aftSidewallTextView.setText(aftSideData);
        aftTireDiameterTextView.setText(aftDiamData);
        aftCircumTextView.setText(aftCirData);
        aftRevKmTextView.setText(aftRevData);

        treadWidthSpinner.setSelection(posTread);
        aspectRatioSpinner.setSelection(posAspect);
        rimDiameterSpinner.setSelection(posDiameter);

        diffPercentTextView.setText(diffData);
        speedoDiffTextView.setText(speedoData);


    }

    private void getSelectedCarInfo(){
        // create Bundle from intent
        Bundle extras = getIntent().getExtras();

        // parse the ID from the intent that is used to pull up vehicle information
        id = Integer.parseInt(extras.get("id").toString());

        Car car = db.getCar(id);
        String selectedVehicle = (car.getYear() + " " +
                car.getMake() + " " +
                car.getModel() + "\n" +
                car.getTreadWidth() + "/" +
                car.getAspectRatio() + "R" +
                car.getRimDiameter());
        sb.append(selectedVehicle);

        // display the selected vehicle information
        selectedVehicleTextView.setText("Selected vehicle:\n" + selectedVehicle + "\n");

        // set OEM base values to match those of the selected vehicle
        oemTreadWidth = car.getTreadWidth();
        oemAspectRatio = car.getAspectRatio();
        oemRimDiameter = car.getRimDiameter();

    }

    private void calculateTireSize(){
        // variables used during and for calculations
        String speedometer;
        double adjustedSpeed, difference;
        final float INCH_TO_MM = 25.4f;
        final float MM_IN_KM = 1000000.0f;
        decFor = new DecimalFormat(".##");

        // variables to store value of spinners
        aftTreadWidth = Integer.parseInt(treadWidthSpinner.getSelectedItem().toString());
        aftAspectRatio = Integer.parseInt(aspectRatioSpinner.getSelectedItem().toString());
        aftRimDiameter = Integer.parseInt(rimDiameterSpinner.getSelectedItem().toString());

        // set the tire sizes to the widgets
        oemTireSize = oemTreadWidth + "/" + oemAspectRatio + "R" + oemRimDiameter;
        oemTireSizeTextView.setText(oemTireSize);
        aftTireSize = aftTreadWidth + "/" + aftAspectRatio + "R" + aftRimDiameter;
        aftTireSizeTextView.setText(aftTireSize);

        // double type variables used for math operations
        double dOemTreadWidth = oemTreadWidth;
        double dOemAspectRatio = oemAspectRatio;
        double dOemRimDiameter = oemRimDiameter;
        double dAftTreadWidth = aftTreadWidth;
        double dAftAspectRatio = aftAspectRatio;
        double dAftRimDiameter = aftRimDiameter;

        // set OEM section width widget
        String sOemSectionWidth = oemTreadWidth + "mm";
        oemSectionWidthTextView.setText(sOemSectionWidth);

        // calculate and display OEM sidewall
        oemSidewall = dOemTreadWidth * (dOemAspectRatio / 100);
        String sOemSidewall = decFor.format(oemSidewall) + "mm";
        oemSidewallTextView.setText(sOemSidewall);

        // calculate and display OEM overall diameter
        oemTireDiameter = (oemSidewall * 2) + (dOemRimDiameter * INCH_TO_MM);
        String sOemTireDiameter = decFor.format(oemTireDiameter) + "mm";
        oemTireDiameterTextView.setText(sOemTireDiameter);

        // calculate and display OEM circumference
        oemCircumference = Math.PI * oemTireDiameter;
        String sOemCircumference = decFor.format(oemCircumference) + "mm";
        oemCircumTextView.setText(sOemCircumference);

        // calculate and display OEM tire revolutions per kilometer
        oemRevKm = MM_IN_KM / oemCircumference;
        String sOemRevKm = decFor.format(oemRevKm);
        oemRevKmTextView.setText(sOemRevKm);

        // set aftermarket section width widget
        String sAftSectionWidth = aftTreadWidth + "mm";
        aftSectionWidthTextView.setText(sAftSectionWidth);

        // calculate and display aftermarket sidewall
        aftSidewall = dAftTreadWidth * (dAftAspectRatio / 100);
        String sAftSidewall = decFor.format(aftSidewall) + "mm";
        aftSidewallTextView.setText(sAftSidewall);

        // calculate and display aftermarket overall diameter
        aftTireDiameter = (aftSidewall * 2) + (dAftRimDiameter * INCH_TO_MM);
        String sAftTireDiameter = decFor.format(aftTireDiameter) + "mm";
        aftTireDiameterTextView.setText(sAftTireDiameter);

        // calculate and display aftermarket circumference
        aftCircumference = Math.PI * aftTireDiameter;
        String sAftCircumference = decFor.format(aftCircumference) + "mm";
        aftCircumTextView.setText(sAftCircumference);

        // calculate and display aftermarket revolutions per kilometer
        aftRevKm = MM_IN_KM / aftCircumference;
        String sAftRevKm = decFor.format(aftRevKm);
        aftRevKmTextView.setText(sAftRevKm);

        // calculate and display the difference between OEM and aftermarket
        difference = (1 - (aftRevKm / oemRevKm)) * 100;
        String sDifference = decFor.format(difference) + "%";
        diffPercentTextView.setText(sDifference);

        adjustedSpeed = difference + 100;
        String sAdjustedSpeed = decFor.format(adjustedSpeed);

        if (difference < 0){
            speedometer = "The tire size input for comparison is " +
                    sDifference + " smaller than the OEM wheel, " +
                    "which means when the speedometer reads 100km/h, " +
                    "the actual speed will be " + sAdjustedSpeed + " km/h.";
        }
        else if (difference > 0){
            speedometer = "The tire size input for comparison is " +
                    sDifference + " larger than the OEM wheel, " +
                    "which means when the speedometer reads 100km/h, " +
                    "the actual speed will be " + sAdjustedSpeed + " km/h.";
        }
        else {
            speedometer = "the tire size input for comparison is " +
                    "the same size as the OEM size, so there's no difference " +
                    "in the size or the speedometer reading at any speed!";
        }

        // display the speedometer analysis string in the widget
        speedoDiffTextView.setText(speedometer);

    }
}
