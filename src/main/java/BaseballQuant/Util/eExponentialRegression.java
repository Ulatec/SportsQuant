package BaseballQuant.Util;

import java.util.ArrayList;

public class eExponentialRegression {
    private ArrayList<Double> x;    // ArrayList for x-values of the dataset
    private ArrayList<Double> y;    // ArrayList for Y-values of the dataset

    private Double lnybar;
    private Double xbar;

    private Double Sxx;
    private Double Sxy;
    private Double Syy;

    private Double a;
    private Double b;
    private Double r;

    public eExponentialRegression(ArrayList<Double> x, ArrayList<Double> y){
        this.x = x; this.y = y; // Store x and y values
        findLnyBar(); findXBar();
        findSxx(); findSxy(); findSyy();
        findB(); findA(); findR();
    }

    private void findLnyBar(){
        Double total = 0.0;
        for (Double i:y)
            total+=Math.log(i);
        lnybar = total/y.size();
    }

    private void findXBar(){
        Double total = 0.0;
        for (Double i:x)
            total+=i;
        xbar = total/x.size();
    }

    private void findSxx(){
        Double total = 0.0;
        for (Double i:x)
            total+=Math.pow(i,2);
        Sxx = total / x.size() - Math.pow(xbar, 2);
    }

    private void findSxy(){
        Double total = 0.0;
        for (int i = 0; i < x.size(); i++)
            total+=x.get(i)*Math.log(y.get(i));
        Sxy = total / x.size() - (xbar*lnybar);
    }

    private void findSyy(){
        Double total = 0.0;
        for (Double i:y)
            total+=Math.log(i)*Math.log(i);
        Syy = total / y.size() - Math.pow(lnybar, 2);
    }

    private void findA(){
        a = Math.pow(Math.E,(lnybar-(b*xbar)));
    }

    private void findB(){
        b = Sxy/Sxx;
    }

    private void findR(){
        r = Sxy/(Math.sqrt(Sxx)*Math.sqrt(Syy));
    }

    public double getA(){
        return a;
    }

    public double getB(){
        return b;
    }

    public Double getR2(){
        return r*r;
    }
}