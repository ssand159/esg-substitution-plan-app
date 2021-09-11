package com.esgsubstitutionplanapp.content;

import com.esgsubstitutionplanapp.DB;
import com.esgsubstitutionplanapp.content.model.Date;
import com.esgsubstitutionplanapp.content.model.Substitution;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class ContentParser {

    private static final String ID_TABLE = "schuelerVertretungsplan";
    private static final String DAILY_NEWS = "nachrichtDesTages";

    public static void parseContent(String html){
        SortedSet<Date> dates = new TreeSet<>();
        ArrayList<Substitution> substitutions = new ArrayList<>();

        Document document = Jsoup.parse(html);
        Elements tableBody = document.getElementById(ID_TABLE).getElementsByTag("tbody");
        Elements dailyNewsTable = document.getElementById(DAILY_NEWS).getElementsByTag("tbody");

        Element firstSubstitution = tableBody.first();
        if(firstSubstitution != null){
            for(Element current : firstSubstitution.children()){
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

                // add substitutions
                substitutions.add(substitution);

                // add date
                String date = substitution.getDatum();
                if(date != null && !date.isEmpty()){
                    dates.add(new Date(date));
                }
            }
        }

        Element firstNews = dailyNewsTable.first();
        if(firstNews != null){
            for(Element current : firstNews.children()){
                String temp_date = current.child(0).html();
                String news = current.child(1).html().replace("<br>", "\n");
                for(Date date : dates){
                    if(date.getDate().equals(temp_date)){
                        date.setNewsOfTheDay(news);
                    }
                }
            }

        }

        DB.dates = dates;
        DB.substitutions = substitutions;
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
