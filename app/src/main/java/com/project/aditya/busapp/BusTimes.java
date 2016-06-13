package com.project.aditya.busapp;

/**
 * Created by Aditya on 14/6/2016.
 */
public class BusTimes {
    private String num;
    private int t1;
    private int t2;

    public BusTimes(){

    }

    public BusTimes(String no, int time1, int time2){
        num = no;
        t1 = time1;
        t2 = time2;
    }

    public void setNum(String no){num = no;}

    public void setTime1(int time1){t1 = time1;}

    public void setTime2(int time2){t2 = time2;}

    public String getNum(){return num;}

    public int getT1(){return t1;}

    public int getT2(){return t2;}
}
