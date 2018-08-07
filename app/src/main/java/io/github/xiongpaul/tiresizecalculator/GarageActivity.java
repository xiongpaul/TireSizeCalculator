package io.github.xiongpaul.tiresizecalculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class GarageActivity extends Activity implements OnItemClickListener {

    private ListView garageListView;
    private CarDB db;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        garageListView = (ListView) findViewById(R.id.garageListView);
        garageListView.setOnItemClickListener(this);

        db = new CarDB(this);
        sb = new StringBuilder();

        updateDisplay();

    }

    @Override
    protected void onResume(){
        super.onResume();

        updateDisplay();
    }


    private void updateDisplay() {
        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data = db.getCars();

        // create the resource, from, and to variables
        int resource = R.layout.garage_list_view_item;
        String[] from = {"year", "make", "model"};
        int[] to = {R.id.carYearTextView, R.id.carMakeTextView, R.id.carModelTextView};

        // create and set the adapter
        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);
        garageListView.setAdapter(adapter);

    }


    // this is called on a button click and displays corresponding activity
    public void startNewActivity(View v){
        Intent intent;

        switch(v.getId()){
            case R.id.addCarButton:
                intent = new Intent(this, AddCarActivity.class);
                startActivity(intent);
                break;

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ArrayList<HashMap<String, String>> data = db.getCars();

        // variable to store actual database _id field of car
        final int carId = Integer.parseInt(String.valueOf(data.get(position).get("_id")));

        // access database using the car id
        Car car = db.getCar(carId);

        // string builder object used to create information used by dialog box
        final String carInfo = (car.getYear() + " " +
                car.getMake() + " " +
                car.getModel() + "\n" +
                car.getTreadWidth() + "/" +
                car.getAspectRatio() + "R" +
                car.getRimDiameter());
        sb.append(carInfo);

        // string builder object used to create information used when delete button is clicked
        final String carDeleteInfo = (car.getYear() + " " +
                car.getMake() + " " +
                car.getModel());
        sb.append(carDeleteInfo);

        // instantiate dialog box and set all values
        final Dialog dialog = new Dialog(GarageActivity.this);
        dialog.setContentView(R.layout.selected_car_dialog_box);
        dialog.show();

        TextView clickedCarTextView = (TextView)dialog.findViewById(R.id.clickedCarTextView);

        Button cancelButton = (Button)dialog.findViewById(R.id.cancelButton);
        Button deleteButton = (Button)dialog.findViewById(R.id.deleteButton);
        Button proceedButton = (Button)dialog.findViewById(R.id.proceedButton);

        clickedCarTextView.setText(carInfo);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cancel the dialog and go back to the garage
                dialog.cancel();

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // deletes the selected car using the car id
                db.deleteCar(carId);
                String message = carDeleteInfo + " removed from garage";

                // sends message to user to inform of successful delete
                Toast toast = Toast.makeText(GarageActivity.this, message, Toast.LENGTH_SHORT);
                toast.show();

                dialog.cancel();
                updateDisplay();

            }
        });
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // create intent
                Intent intent = new Intent(GarageActivity.this, CalculatorActivity.class);

                // add DB _id value of selected car into intent
                intent.putExtra("id", carId);

                // start the new intent (calculator activity)
                GarageActivity.this.startActivity(intent);

                dialog.cancel();

            }
        });

    }

}
