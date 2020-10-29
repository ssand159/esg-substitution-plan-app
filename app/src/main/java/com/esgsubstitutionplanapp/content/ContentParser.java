package com.esgsubstitutionplanapp.content;

import com.esgsubstitutionplanapp.DB;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class ContentParser {

    private static final String ID_TABLE = "schuelerVertretungsplan";

    public static void createSubstitutionList(String html){
        SortedSet<String> dates = new TreeSet<>();
        ArrayList<Substitution> substitutions = new ArrayList<>();
        ArrayList<Substitution> pauses = new ArrayList<>();

        Document document = Jsoup.parse(html);
        Elements tableBody = document.getElementById(ID_TABLE).getElementsByTag("tbody");

        if(tableBody != null && tableBody.first() != null){

            for(Element current : tableBody.first().children()){
                // create substitution object
                Substitution substitution = new Substitution();
                substitution.setKlassen(getValue(current, "Klasse(n)"));
                substitution.setDatum(getValue(current, "Datum"));
                substitution.setStd(getValue(current, "Std"));
                substitution.setArt(getValue(current, "Art"));
                substitution.setVertretung(getValue(current, "Vertretung"));
                substitution.setFach(getValue(current, "Fach"));
                substitution.setRaum(getValue(current, "Raum"));
                substitution.setFach2(getValue(current, "(Fach)"));
                substitution.setZuVertreten(getValue(current, "zu&nbsp;vertreten"));
                substitution.setBemerkung(getValue(current, "Bemerkung"));
                substitution.setVerlegtVon(getValue(current, "verlegt&nbsp;von"));

                // add
                if(substitution.getArt().toLowerCase().contains("pausenaufsicht")){
                    substitution.setArt(null);
                    pauses.add(substitution);
                } else {
                    substitutions.add(substitution);
                }
                dates.add(substitution.getDatum());
            }
        } else {
            //TODO show some errormessage
        }

        Collections.sort(substitutions);
        DB.dates = dates;
        DB.allSubstitutions = substitutions;
        DB.pauses = pauses;
    }

    public static void filterSubstitutionsForMyClass(){
        ArrayList<Substitution> mySubstitutions = new ArrayList<>();
        DB.mySubstitutions = mySubstitutions;

        for(Substitution substitution : DB.allSubstitutions){
            if(keyMatchesClass(substitution.getKlassen())){
                mySubstitutions.add(substitution);
            }
        }
    }

    private static boolean keyMatchesClass(String klasse){
        MyClass myClass = DB.myClass;
        if(klasse.contains(myClass.getFullName())){
            // matches grade and letter
            // example key: "05B", "10E" or "12"
            return true;
        }
        if(klasse.length() == 2 && klasse.contains(myClass.getGrade())){
            // matches grade only
            // example key: "05" or "09"
            return klasse.equals(myClass.getGrade());
        }
        if(klasse.contains(",")){
            String[] classes = klasse.split(",");
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

    private static String getValue(Element current, String key){
        for(Element cell : current.children()){
            if(cell.attr("data-layer").trim().equalsIgnoreCase(key)){
                return cell.text().trim().replace("-","");
            }
        }
        return null;
    }

}
