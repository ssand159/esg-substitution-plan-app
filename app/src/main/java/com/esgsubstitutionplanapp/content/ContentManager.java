package com.esgsubstitutionplanapp.content;

import android.content.Context;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.esgsubstitutionplanapp.DB;
import com.esgsubstitutionplanapp.MainActivity;
import com.esgsubstitutionplanapp.R;
import com.esgsubstitutionplanapp.connection.ConnectionClient;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ContentManager {

    private final MainActivity mainActivity;
    private final LinearLayout datePicker;
    private final LinearLayout contentView;
    private final ScrollView contentScrollView;
    private final TextView noContentView;
    private final TextView errorView;
    private final TextView newsofthedayText;

    public ContentManager(MainActivity mainActivity, LinearLayout datePicker, LinearLayout contentView, TextView noContentView, ScrollView contentScrollView, TextView errorView, TextView newsofthedayText){
        this.mainActivity = mainActivity;
        this.datePicker = datePicker;
        this.contentView = contentView;
        this.noContentView = noContentView;
        this.contentScrollView = contentScrollView;
        this.errorView = errorView;
        this.newsofthedayText = newsofthedayText;
    }


    public void loadContent() throws ExecutionException, InterruptedException {
        if(DB.lastUpdate + DB.fiveMinutesInMillis < System.currentTimeMillis()){
            DB.lastUpdate = System.currentTimeMillis();
            new RetrieveContentTask().execute().get();
        }
    }

    public void paintContent(ArrayList<Substitution> substitutions, String activeDate) throws ExecutionException, InterruptedException {
        loadContent();

        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView.removeAllViews();
        errorView.setVisibility(View.GONE);

        if(substitutions == null || substitutions.isEmpty()){
            noContentView.setVisibility(View.VISIBLE);
            contentScrollView.setVisibility(View.GONE);
        } else {
            noContentView.setVisibility(View.GONE);
            contentScrollView.setVisibility(View.VISIBLE);
            for(Substitution substitution : substitutions){
                if(substitution.getDatum().equals(activeDate)){
                    addToView(inflater, contentView, substitution);
                }
            }
        }

        // update news
        for(NewsOfTheDay newsOfTheDay : DB.newsOfTheDays){
            if(newsOfTheDay.getDate().equalsIgnoreCase(activeDate)){
                newsofthedayText.setVisibility(View.VISIBLE);
                newsofthedayText.setText(newsOfTheDay.getNews());
            } else {
                newsofthedayText.setVisibility(View.GONE);
            }
        }


    }

    private void addToView(LayoutInflater inflater, LinearLayout scrollView, Substitution substitution){
        View custom = inflater.inflate(R.layout.layout_substitution, null);
        scrollView.addView(custom);

        // set all view
        addText(custom, R.id.template_row_klassen, R.id.template_klassen, substitution.getKlassen());
        addText(custom, R.id.template_row_art, R.id.template_art, substitution.getArt());
        addText(custom, R.id.template_row_bemerkung, R.id.template_bemerkung, substitution.getBemerkung());
        addText(custom, R.id.template_row_datum, R.id.template_datum, substitution.getDatum());
        addText(custom, R.id.template_row_fach, R.id.template_fach, substitution.getFach());
        addText(custom, R.id.template_row_fach2, R.id.template_fach2, substitution.getFach2());
        addText(custom, R.id.template_row_std, R.id.template_std, substitution.getStd());
        addText(custom, R.id.template_row_verlegtvon, R.id.template_verlegtvon, substitution.getVerlegtVon());
        addText(custom, R.id.template_row_vertretung, R.id.template_vertretung, substitution.getVertretung());
        addText(custom, R.id.template_row_zuvertreten, R.id.template_zuvertreten, substitution.getZuVertreten());
        addText(custom, R.id.template_row_raum, R.id.template_raum, substitution.getRaum());
    }

    private void addText(View custom, int tableRowId, int textViewId, String text){
        TextView tv = custom.findViewById(textViewId);
        if(text == null || text.trim().isEmpty()){
            View row = custom.findViewById(tableRowId);
            row.setVisibility(View.GONE);
        } else {
            tv.setText(text);
        }
    }

    public String paintDateViews(){
        datePicker.removeAllViews();
        boolean first = true;
        String firstDate = null;
        datePicker.setWeightSum(DB.dates.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
        for(String date : DB.dates){
            TextView dateView = new TextView(mainActivity);
            dateView.setText(date);
            dateView.setLayoutParams(params);
            dateView.setGravity(Gravity.CENTER_HORIZONTAL);
            dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainActivity.getResources().getDimension(R.dimen.selector_textsize));
            dateView.setClickable(true);
            dateView.setFocusable(true);
            dateView.setOnClickListener(v -> mainActivity.dateClicked(dateView, date));
            if(first){
                dateView.setBackgroundColor(mainActivity.getResources().getColor(R.color.activeSelector));
                dateView.setTextColor(mainActivity.getResources().getColor(R.color.activeText));
                first = false;
                firstDate = date;
            } else {
                dateView.setBackgroundColor(mainActivity.getResources().getColor(R.color.inActiveSelector));
            }
            datePicker.addView(dateView);
        }
        return firstDate;
    }


    static class RetrieveContentTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                // get content
                ConnectionClient connectionClient = new ConnectionClient(DB.endpoint, DB.username, DB.password, false);
                String html = connectionClient.getHtml();

                // parse content and filter it according to user settings
                ContentParser.parseContent(html);
                ContentParser.filterSubstitutionsForMyClass();

            } catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }
    }
}
