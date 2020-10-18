package com.esgsubstitutionplanapp.content;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esgsubstitutionplanapp.DB;
import com.esgsubstitutionplanapp.MainActivity;
import com.esgsubstitutionplanapp.R;
import com.esgsubstitutionplanapp.connection.ConnectionClient;
import com.esgsubstitutionplanapp.content.model.Content;
import com.esgsubstitutionplanapp.content.model.Date;
import com.esgsubstitutionplanapp.content.model.Substitution;

import org.apache.commons.collections4.MultiValuedMap;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

public class ContentManager {

    public void showContent(MainActivity mainActivity, LinearLayout datePicker, LinearLayout scrollView, TextView activeClass, String activeDate) throws ExecutionException, InterruptedException {
        if(DB.lastUpdate + DB.fiveMinutesInMillis < System.currentTimeMillis()){
            DB.lastUpdate = System.currentTimeMillis();
            new RetrieveContentTask().execute().get();
        }
        String firstDate = setDateViews(datePicker, mainActivity);
        if(activeClass == null){
            activeDate = firstDate;
        }
        paintContent(mainActivity, scrollView, activeDate, activeClass);
    }

    public void updateUserFilter(){
        ArrayList<Substitution> mySubstitutions = UserFilter.getSubstitutionsForMyClass(DB.content.getAllSubstitutions(), DB.myClass);
        DB.content.setMySubstitutions(mySubstitutions);
    }

    private void paintContent(Activity mainActivity, LinearLayout scrollView, String activeDate, TextView activeClass){
        Content content = DB.content;
        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scrollView.removeAllViews();

        if(activeClass.getId() == R.id.myclassText){
            for(Substitution substitution : content.getMySubstitutions()){
                if(substitution.getDatum().equals(activeDate)){
                    addToView(inflater, scrollView, substitution);
                }
            }
        } else {
            for(String key : content.getAllSubstitutions().keySet()){
                for(Substitution substitution : content.getAllSubstitutions().get(key)){
                    addToView(inflater, scrollView, substitution);
                }
            }
        }
    }

    private void addToView(LayoutInflater inflater, LinearLayout scrollView, Substitution substitution){
        View custom = inflater.inflate(R.layout.layout_substitution, null);
        scrollView.addView(custom);

        // set all view
        TextView tv = custom.findViewById(R.id.template_klassen);
        tv.setText(substitution.getKlassen());
        TextView art = custom.findViewById(R.id.template_art);
        art.setText(substitution.getArt());
        TextView bemerkung = custom.findViewById(R.id.template_bemerkung);
        bemerkung.setText(substitution.getBemerkung());
        TextView datum = custom.findViewById(R.id.template_datum);
        datum.setText(substitution.getDatum());
        TextView fach = custom.findViewById(R.id.template_fach);
        fach.setText(substitution.getFach());
        TextView fach2 = custom.findViewById(R.id.template_fach2);
        fach2.setText(substitution.getFach2());
        TextView raum = custom.findViewById(R.id.template_raum);
        raum.setText(substitution.getRaum());
        TextView std = custom.findViewById(R.id.template_std);
        std.setText(substitution.getStd());
        TextView verlegtvon = custom.findViewById(R.id.template_verlegtvon);
        verlegtvon.setText(substitution.getVerlegtVon());
        TextView vertretung = custom.findViewById(R.id.template_vertretung);
        vertretung.setText(substitution.getVertretung());
        TextView zuverlegen = custom.findViewById(R.id.template_zuvertreten);
        zuverlegen.setText(substitution.getZuVertreten());

    }

    private String setDateViews(LinearLayout datePicker, MainActivity mainActivity){
        datePicker.removeAllViews();
        boolean first = true;
        String firstDate = "";
        SortedSet<Date> dates = DB.content.getDates();
        datePicker.setWeightSum(dates.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
        for(Date date : dates){
            TextView dateView = new TextView(mainActivity);
            dateView.setText(date.getDate());
            dateView.setLayoutParams(params);
            dateView.setGravity(Gravity.CENTER_HORIZONTAL);
            dateView.setTextSize(mainActivity.getResources().getDimension(R.dimen.selector_textsize));
            dateView.setClickable(true);
            dateView.setFocusable(true);
            dateView.setOnClickListener(v -> mainActivity.dateClicked(dateView, date.getDate()));
            if(first){
                dateView.setBackgroundColor(mainActivity.getResources().getColor(R.color.activeSelector));
                firstDate = date.getDate();
                first = false;
            } else {
                dateView.setBackgroundColor(mainActivity.getResources().getColor(R.color.inActiveSelector));
            }
            datePicker.addView(dateView);
        }
        return firstDate;
    }


    static class RetrieveContentTask extends AsyncTask<String, Void, Content> {

        @Override
        protected Content doInBackground(String... strings) {
            MultiValuedMap<String, Substitution> allSubstitutions;
            ArrayList<Substitution> mySubstitutions;
            try {
                // get content
                ConnectionClient connectionClient = new ConnectionClient(DB.endpoint, DB.username, DB.password, false);
                String html = connectionClient.getHtml();

                // parse content
                allSubstitutions = ContentParser.createSubstitutionList(html);

                // filter content according to user settings
                mySubstitutions = UserFilter.getSubstitutionsForMyClass(allSubstitutions, DB.myClass);

                DB.content.setAllSubstitutions(allSubstitutions);
                DB.content.setMySubstitutions(mySubstitutions);
                DB.content.setDates(ContentParser.getDates());
            } catch (Exception e){
                e.printStackTrace();
            }
            return DB.content;
        }
    }
}
