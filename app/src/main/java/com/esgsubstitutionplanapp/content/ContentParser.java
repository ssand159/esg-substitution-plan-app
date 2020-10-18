package com.esgsubstitutionplanapp.content;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

public class ContentParser {

    private static final String ID_TABLE = "schuelerVertretungsplan";
    private final SortedSet<Date> dates = new TreeSet<>();
    private final boolean logOutput;

    public ContentParser (boolean logOutput){
        this.logOutput = logOutput;
    }

    public MultiValuedMap<String, Substitution> createSubstitutionList(String html){
        MultiValuedMap<String, Substitution> map = new ArrayListValuedHashMap<>();
        Document document = Jsoup.parse(html);
        dates.clear();

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
                map.put(substitution.getKlassen().trim(), substitution);
                dates.add(new Date(substitution.getDatum()));
            }
        } else {
            //TODO show some errormessage
        }

        if(logOutput){
            logMap(map);
        }
        return map;
    }

    private String getValue(Element current, String key){
        for(Element cell : current.children()){
            if(cell.attr("data-layer").trim().equalsIgnoreCase(key)){
                return cell.text().trim();
            }
        }
        return "";
    }

    private void logMap(MultiValuedMap<String, Substitution> map){
        for(String key : map.keySet()){
            System.out.println("Key: " + key);
            Collection<Substitution> substitutions = map.get(key);
            for(Substitution substitution : substitutions){
                System.out.println("- " + substitution);
            }
        }
    }

    public SortedSet<Date> getDates() {
        return dates;
    }
}
