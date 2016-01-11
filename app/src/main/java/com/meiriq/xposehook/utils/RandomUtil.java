package com.meiriq.xposehook.utils;

import com.meiriq.xposehook.bean.DataInfo;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by tian on 16-1-9.
 */
public class RandomUtil {

    public static final String[] LETTERS = {"a","b","c","d","e","f","g","h","i","j",
            "k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    public static final String[] NUMBER = {"1","2","3","4","5","6","7","8","9","0"};

    public static final String[] LETTERSANDNUMBER = {"1","2","3","4"
            ,"5","6","7","8","9","0","a","b","c","d","e","f","g","h","i","j",
            "k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","1","2","3","4"
            ,"5","6","7","8","9","0"};

    public static DataInfo getRandom(){




















        return null;
    }

    private static String getNetTypeName(){

        return null;
    }

    private static final String[] OPERATOR = {"46000","46001","46002","46003","46007"};
    private static String getOperator(){
        int num = (int) (Math.random() * OPERATOR.length);
        return OPERATOR[num];
    }

    private static String getISim(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(4600);
        for (int i = 0; i < 11; i++) {
            int num = (int) (Math.random() * 10);
            stringBuilder.append(num);
        }
        return stringBuilder.toString();
    }

    private static String getSimSerialNumber(){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(89860);
        for (int i = 0; i < 15; i++) {
            int num = (int) (Math.random() * 10);
            if(i == 0){
                if(num < 6)
                    stringBuilder.append(0);
                else
                    stringBuilder.append(1);
            }else {
                stringBuilder.append(num);
            }
        }
        return stringBuilder.toString();
    }

    private static String getPhoneBumber(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(1);

        for (int i = 0; i < 8; i++) {
            int num = (int) (Math.random() * 10);

            if(i == 0){
                if(num < 4)
                    stringBuilder.append(3);
                else if(num < 7)
                    stringBuilder.append(5);
                else
                    stringBuilder.append(8);
            }else {
                stringBuilder.append(num);
            }

        }
        return stringBuilder.toString();
    }

    private static String getAndroidId(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            stringBuilder.append(LETTERSANDNUMBER[(int)Math.random()*LETTERSANDNUMBER.length]);
        }
        return stringBuilder.toString();
    }

    private static String getImei(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            stringBuilder.append((int)(Math.random() * 10));
        }

        return stringBuilder.toString();
    };

}
