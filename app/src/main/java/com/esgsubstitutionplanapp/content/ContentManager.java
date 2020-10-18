package com.esgsubstitutionplanapp.content;

import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.esgsubstitutionplanapp.DB;
import com.esgsubstitutionplanapp.MainActivity;
import com.esgsubstitutionplanapp.R;
import com.esgsubstitutionplanapp.connection.ConnectionClient;

import org.apache.commons.collections4.MultiValuedMap;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

public class ContentManager {

    private Content content = new Content();

    public void showContent(MainActivity mainActivity, LinearLayout datePicker, ScrollView scrollView, TextView activeClass, String activeDate) throws ExecutionException, InterruptedException {
        if(DB.lastUpdate + DB.fiveMinutesInMillis < System.currentTimeMillis()){
            content = new RetrieveContentTask().execute().get();
            DB.lastUpdate = System.currentTimeMillis();
        }
        String firstDate = setDateViews(datePicker, mainActivity);
        if(activeClass == null){
            activeDate = firstDate;
        }
        paintContent(scrollView, activeDate, activeClass);
    }

    private void paintContent(ScrollView scrollView, String activeDate, TextView activeClass){
    }

    private String setDateViews(LinearLayout datePicker, MainActivity mainActivity){
        boolean first = true;
        String firstDate = "";
        SortedSet<Date> dates = content.getDates();
        datePicker.setWeightSum(dates.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        for(Date date : dates){
            TextView dateView = new TextView(mainActivity);
            dateView.setText(date.getDate());
            dateView.setLayoutParams(params);
            dateView.setGravity(Gravity.CENTER_HORIZONTAL);
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


    class RetrieveContentTask extends AsyncTask<String, Void, Content> {

        @Override
        protected Content doInBackground(String... strings) {
            Content localContent = new Content();
            MultiValuedMap<String, Substitution> allSubstitutions;
            ArrayList<Substitution> mySubstitutions;
            try {
                // get content
                ConnectionClient connectionClient = new ConnectionClient(DB.endpoint, DB.username, DB.password, false);
                String html = connectionClient.getHtml();

                // parse content
                ContentParser contentParser = new ContentParser(false);
                allSubstitutions = contentParser.createSubstitutionList(html);

                // filter content according to user settings
                UserFilter userFilter= new UserFilter(true);
                mySubstitutions = userFilter.getSubstitutionsForMyClass(allSubstitutions, DB.myClass);

                localContent.setAllSubstitutions(allSubstitutions);
                localContent.setMySubstitutions(mySubstitutions);
                localContent.setDates(contentParser.getDates());

            } catch (Exception e){
                e.printStackTrace();
            }
            return localContent;
        }
    }
}
