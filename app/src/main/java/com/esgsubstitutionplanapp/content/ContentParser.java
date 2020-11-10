package com.esgsubstitutionplanapp.content;

import com.esgsubstitutionplanapp.DB;
import com.esgsubstitutionplanapp.content.model.Date;
import com.esgsubstitutionplanapp.content.model.NewsOfTheDay;
import com.esgsubstitutionplanapp.content.model.Substitution;
import com.esgsubstitutionplanapp.content.model.SubstitutionType;

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
    private static final String DAILY_NEWS = "nachrichtDesTages";

    public static void parseContent(String html){
        SortedSet<Date> dates = new TreeSet<>();
        ArrayList<Substitution> substitutions = new ArrayList<>();
        ArrayList<NewsOfTheDay> newsOfTheDays = new ArrayList<>();

        Document document = Jsoup.parse(html);
        Elements tableBody = document.getElementById(ID_TABLE).getElementsByTag("tbody");
        Elements dailyNewsTable = document.getElementById(DAILY_NEWS).getElementsByTag("tbody");

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
                    substitution.setType(SubstitutionType.SUBSTITUTION_PAUSE);
                } else {
                    substitutions.add(substitution);
                    // filter userclasses
                    if(keyMatchesClass(substitution.getKlassen())){
                        substitution.setType(SubstitutionType.SUBSTITUTION_OF_MYCLASS);
                    } else {
                        substitution.setType(SubstitutionType.SUBSTITUTION_ALL);
                    }
                }
                String date = substitution.getDatum();
                if(date != null && !date.isEmpty()){
                    dates.add(new Date(date));
                }
            }
        } else {
            //TODO show some errormessage
        }

        // TODO add this to Date
        if(dailyNewsTable != null && dailyNewsTable.first() != null){
            for(Element current : dailyNewsTable.first().children()){
                String date = current.child(0).html();
                String news = current.child(1).html().replace("<br>", "\n");
                NewsOfTheDay newsOfTheDay = new NewsOfTheDay(date, news);
                newsOfTheDays.add(newsOfTheDay);
            }
        } else {
            //TODO show some errormessage
        }

        DB.dates = dates;
        DB.allSubstitutions = substitutions;
        DB.newsOfTheDays = newsOfTheDays;
    }

    private static boolean keyMatchesClass(String klasse){
        MyClass myClass = DB.myClass;
        if(klasse.contains(myClass.getFullName())){
            // matches grade and letter
            // example key: "05B", "10E" or "12"
            return true;
        }
        if(klasse.contains(myClass.getGrade()) && klasse.contains(myClass.getLetter())){
            // matches grade and letter separate
            // example key: "10ABC", "05DEF"
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
