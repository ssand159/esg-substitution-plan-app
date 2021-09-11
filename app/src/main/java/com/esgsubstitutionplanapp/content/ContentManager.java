package com.esgsubstitutionplanapp.content;

import android.content.Context;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esgsubstitutionplanapp.DB;
import com.esgsubstitutionplanapp.MainActivity;
import com.esgsubstitutionplanapp.R;
import com.esgsubstitutionplanapp.connection.ConnectionClient;
import com.esgsubstitutionplanapp.content.model.Date;
import com.esgsubstitutionplanapp.content.model.Substitution;

import java.util.concurrent.ExecutionException;

public class ContentManager {

    private final MainActivity mainActivity;
    private final LinearLayout datePicker;
    private final LinearLayout contentView;
    private final View contentContainer;
    private final TextView noContentView;
    private final TextView newsOfTheDayView;

    public ContentManager(MainActivity mainActivity, LinearLayout datePicker, LinearLayout contentView, TextView noContentView, View contentContainer, TextView newsOfTheDayView){
        this.mainActivity = mainActivity;
        this.datePicker = datePicker;
        this.contentView = contentView;
        this.noContentView = noContentView;
        this.contentContainer = contentContainer;
        this.newsOfTheDayView = newsOfTheDayView;
    }

    public void downloadAndShowContent() throws ExecutionException, InterruptedException {
        new RetrieveContentTask().execute().get();
        paintDateViews();
        paintSubstitutionViews();
        changeDay(DB.dates.first().getDate());
    }

    public void changeDay(String activeDate){
        filterSubstitutionforDay(activeDate);
        showNewsOfTheDay(activeDate);
    }

    private void filterSubstitutionforDay(String activeDate){
        boolean somethingIsVisible = false;
        int childCount = contentView.getChildCount();

        if(childCount > 0){
            for(int i = 0; i < childCount; i++){
                View current = contentView.getChildAt(i);
                SubstitutionView substitutionView = current.findViewById(R.id.substitution);

                if(keyMatchesClass(substitutionView.getKlassen()) && substitutionView.getDate().equals(activeDate)){
                    current.setVisibility(View.VISIBLE);
                    somethingIsVisible = true;
                } else {
                    current.setVisibility(View.GONE);
                }
            }
        }
        if(!somethingIsVisible){
            noContentView.setVisibility(View.VISIBLE);
            contentContainer.setVisibility(View.GONE);
        } else {
            noContentView.setVisibility(View.GONE);
            contentContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showNewsOfTheDay(String activeDate){
        for(Date date : DB.dates){
            if(date.getDate().equals(activeDate)){
                String newsOfTheDay = date.getNewsOfTheDay();
                if(newsOfTheDay != null && !newsOfTheDay.isEmpty()){
                    newsOfTheDayView.setText(newsOfTheDay);
                    newsOfTheDayView.setVisibility(View.VISIBLE);
                } else {
                    newsOfTheDayView.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    private void paintSubstitutionViews() {
        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.removeAllViews();

        for(Substitution substitution : DB.substitutions){
            View custom = inflater.inflate(R.layout.layout_substitution, null);
            custom.setLayoutParams(params);
            contentView.addView(custom);

            // add attributes
            SubstitutionView substitutionView = custom.findViewById(R.id.substitution);
            substitutionView.setDate(substitution.getDatum());
            substitutionView.setKlassen(substitution.getKlassen());
            substitutionView.setVisibility(View.GONE);

            // set all view inside substitutionView
            addText(custom, R.id.template_klassen, substitution.getKlassen());
            addText(custom, R.id.template_art, substitution.getArt());
            addText(custom, R.id.template_bemerkung, substitution.getBemerkung());
            addText(custom, R.id.template_fach, substitution.getFach());
            addText(custom, R.id.template_fach2, substitution.getFach2());
            addText(custom, R.id.template_std, substitution.getStd());
            addText(custom, R.id.template_verlegtvon, substitution.getVerlegtVon());
            addText(custom, R.id.template_vertretung, substitution.getVertretung());
            addText(custom, R.id.template_zuvertreten, substitution.getZuVertreten());
            addText(custom, R.id.template_raum, substitution.getRaum());
//        addText(custom, R.id.template_row_datum, R.id.template_datum, substitution.getDatum());
        }
    }

    private void addText(View custom, int textViewId, String text){
        TextView tv = custom.findViewById(textViewId);
        if(text == null || text.trim().isEmpty()){
            tv.setText("-");
        } else {
            tv.setText(text);
        }
    }

    private void paintDateViews(){
        // cleanup & init
        datePicker.removeAllViews();
        datePicker.setWeightSum(DB.dates.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;

        // paint every date available
        boolean first = true;
        for(Date date : DB.dates){
            TextView dateView = new TextView(mainActivity);
            dateView.setText(date.getDate());
            dateView.setLayoutParams(params);
            dateView.setGravity(Gravity.CENTER_HORIZONTAL);
            dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainActivity.getResources().getDimension(R.dimen.textsize_selection));
            dateView.setClickable(true);
            dateView.setFocusable(true);
            dateView.setOnClickListener(v -> mainActivity.dateClicked(dateView, date.getDate()));
            if(first){
                first = false;
                dateView.setBackgroundColor(mainActivity.getResources().getColor(R.color.activeSelector));
                dateView.setTextColor(mainActivity.getResources().getColor(R.color.activeText));
            } else {
                dateView.setBackgroundColor(mainActivity.getResources().getColor(R.color.inActiveSelector));
                dateView.setTextColor(mainActivity.getResources().getColor(R.color.inactiveText));
            }
            datePicker.addView(dateView);
        }
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

            } catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }
    }

    private boolean keyMatchesClass(String klasse){
        MyClass myClass = DB.myClass;
        if(myClass.getGrade().contains("alle")){
            return true;
        }
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

}
