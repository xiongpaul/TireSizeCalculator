package io.github.xiongpaul.tiresizecalculator;
/*
    this is the class that will hold the object of the car in the 'garage' database
 */
public class Car {

    private int carId;
    private int year;
    private String make;
    private String model;

    private int treadWidth;
    private int aspectRatio;
    private int rimDiameter;


    public Car() {
        year = 0;
        make = "";
        model = "";
        treadWidth = 0;
        aspectRatio = 0;
        rimDiameter = 0;

    }

    public Car(int carId, int year, String make, String model, int treadWidth, int aspectRatio, int rimDiameter){
        this.carId = carId;
        this.year = year;
        this.make = make;
        this.model = model;
        this.treadWidth = treadWidth;
        this.aspectRatio = aspectRatio;
        this.rimDiameter = rimDiameter;

    }

    public Car(int year, String make, String model, int treadWidth, int aspectRatio, int rimDiameter){
        this.year = year;
        this.make = make;
        this.model = model;
        this.treadWidth = treadWidth;
        this.aspectRatio = aspectRatio;
        this.rimDiameter = rimDiameter;

    }

    public int getCarId(){
        return carId;
    }

    public void setCarId(){
        this.carId = carId;
    }

    public int getYear(){
        return year;
    }

    public void setYear(){
        this.year = year;
    }

    public String getMake(){
        return make;
    }

    public void setMake(){
        this.make = make;
    }

    public String getModel(){
        return model;
    }

    public void setModel(){
        this.model = model;
    }

    public int getTreadWidth(){
        return treadWidth;
    }

    public void setTreadWidth(){
        this.treadWidth = treadWidth;
    }

    public int getAspectRatio(){
        return aspectRatio;
    }

    public void setAspectRatio(){
        this.aspectRatio = aspectRatio;
    }

    public int getRimDiameter(){
        return rimDiameter;
    }

    public void setRimDiameter(){
        this.rimDiameter = rimDiameter;
    }


}
