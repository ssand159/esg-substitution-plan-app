package com.esgsubstitutionplanapp.content;

import com.esgsubstitutionplanapp.content.model.Substitution;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.ArrayList;

public class UserFilter {

    public static ArrayList<Substitution> getSubstitutionsForMyClass(MultiValuedMap<String, Substitution> map, MyClass myClass){
        ArrayList<Substitution> mySubstitutions = new ArrayList<>();

        for(String key : map.keySet()){
            if(keyMatchesClass(key, myClass)){
                mySubstitutions.addAll(map.get(key));
            }
        }

//        StringBuilder stringBuilder = new StringBuilder("Substitutions for ").append(myClass.getFullName()).append("\n");
//        for(Substitution substitution : mySubstitutions){
//            stringBuilder.append("- ").append(substitution).append("\n");
//        }
//        System.out.println(stringBuilder.toString());
        return mySubstitutions;
    }

    private static boolean keyMatchesClass(String key, MyClass myClass){
        if(key.contains(myClass.getFullName())){
            // matches grade and letter
            // example key: "05B", "10E" or "12"
            return true;
        }
        if(key.length() == 2 && key.contains(myClass.getGrade())){
            // matches grade only
            // example key: "05" or "09"
            return key.equals(myClass.getGrade());
        }
        if(key.contains(",")){
            String[] classes = key.split(",");
            for(String aClass : classes){
                if(aClass.trim().length() == 2 && aClass.trim().equals(myClass.getGrade())){
                    // match grade if multiple grades are given
                    // example key: "07, 08"
                    return true;
                }
            }
        }
        return false;
    }
}
