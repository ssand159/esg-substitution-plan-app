package com.esgsubstitutionplanapp.content.model;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.ArrayList;
import java.util.SortedSet;

public class Content {
    private MultiValuedMap<String, Substitution> allSubstitutions;
    private ArrayList<Substitution> mySubstitutions;
    private SortedSet<Date> dates;

    public SortedSet<Date> getDates() {
        return dates;
    }

    public MultiValuedMap<String, Substitution> getAllSubstitutions() {
        return allSubstitutions;
    }

    public ArrayList<Substitution> getMySubstitutions() {
        return mySubstitutions;
    }

    public void setAllSubstitutions(MultiValuedMap<String, Substitution> allSubstitutions) {
        this.allSubstitutions = allSubstitutions;
    }

    public void setDates(SortedSet<Date> dates) {
        this.dates = dates;
    }

    public void setMySubstitutions(ArrayList<Substitution> mySubstitutions) {
        this.mySubstitutions = mySubstitutions;
    }
}
