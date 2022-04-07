package com.eteam.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class CommaInputFilter implements InputFilter {
 
 
    @Override
    public CharSequence filter(CharSequence source,
            int sourceStart, int sourceEnd,
            Spanned destination, int destinationStart,
            int destinationEnd) 
    {  
        String textToCheck = destination.subSequence(0, destinationStart).
            toString() + source.subSequence(sourceStart, sourceEnd) +
            destination.subSequence(
            destinationEnd, destination.length()).toString();
  
 //       if(textToCheck.startsWith("0")){
 //       	return "";
 //       }
        // Entered text does not match the pattern
        if(textToCheck.contains(".")){
        	return ",";
   
        }
  
        return null;
    }

}

