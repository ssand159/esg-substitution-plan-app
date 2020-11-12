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
import com.esgsubstitutionplanapp.content.model.SubstitutionType;

import java.util.concurrent.ExecutionException;

public class ContentManager {

    private final MainActivity mainActivity;
    private final LinearLayout datePicker;
    private final LinearLayout contentView;
    private final View contentContainer;
    private final TextView noContentView;
    private final TextView errorView;

    public ContentManager(MainActivity mainActivity, LinearLayout datePicker, LinearLayout contentView, TextView noContentView, View contentContainer, TextView errorView){
        this.mainActivity = mainActivity;
        this.datePicker = datePicker;
        this.contentView = contentView;
        this.noContentView = noContentView;
        this.contentContainer = contentContainer;
        this.errorView = errorView;
    }

    public void loadContent() throws ExecutionException, InterruptedException {
        if(DB.getLastUpdate() + DB.fiveMinutesInMillis < System.currentTimeMillis()){
            DB.setLastUpdate(System.currentTimeMillis());
            new RetrieveContentTask().execute().get();
        }
    }

    public void filterSubstitutionsForClass(){
        for(Substitution substitution : DB.allSubstitutions){
            if(substitution.getType() != SubstitutionType.SUBSTITUTION_PAUSE){
                // filter userclasses
                if(keyMatchesClass(substitution.getKlassen())){
                    substitution.setType(SubstitutionType.SUBSTITUTION_OF_MYCLASS);
                } else {
                    substitution.setType(SubstitutionType.SUBSTITUTION_ALL);
                }
            }
        }
    }

    public void updateContent(SubstitutionType type, String activeDate){
        boolean somethingIsVisible = false;
        int childCount = contentView.getChildCount();

        if(childCount > 0){
            for(int i = 0; i < childCount; i++){
                View current = contentView.getChildAt(i);
                SubstitutionView substitutionView = current.findViewById(R.id.substitution);

                if(substitutionView.getSubstitutionType().equals(type) && substitutionView.getDate().equals(activeDate)){
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

    public void setUpContent() throws ExecutionException, InterruptedException {
        loadContent();

        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView.removeAllViews();
        errorView.setVisibility(View.GONE);

        if(DB.allSubstitutions == null || DB.allSubstitutions.isEmpty()){
            noContentView.setVisibility(View.VISIBLE);
            contentContainer.setVisibility(View.GONE);
        } else {
            noContentView.setVisibility(View.GONE);
            contentContainer.setVisibility(View.VISIBLE);
            for(Substitution substitution : DB.allSubstitutions){
                addToView(inflater, contentView, substitution);
            }
        }
    }

    private void addToView(LayoutInflater inflater, LinearLayout scrollView, Substitution substitution){
        View custom = inflater.inflate(R.layout.layout_substitution, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        custom.setLayoutParams(params);
        scrollView.addView(custom);

        // add attributes
        SubstitutionView substitutionView = custom.findViewById(R.id.substitution);
        substitutionView.setSubstitutionType(substitution.getType());
        substitutionView.setDate(substitution.getDatum());

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

    private void addText(View custom, int textViewId, String text){
        TextView tv = custom.findViewById(textViewId);
        if(text == null || text.trim().isEmpty()){
            tv.setText("-");
        } else {
            tv.setText(text);
        }
    }

    public String paintDateViews(){
        datePicker.removeAllViews();
        boolean first = true;
        Date firstDate = null;
        datePicker.setWeightSum(DB.dates.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
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
                dateView.setBackgroundColor(mainActivity.getResources().getColor(R.color.activeSelector));
                dateView.setTextColor(mainActivity.getResources().getColor(R.color.activeText));
                first = false;
                firstDate = date;
            } else {
                dateView.setBackgroundColor(mainActivity.getResources().getColor(R.color.inActiveSelector));
            }
            datePicker.addView(dateView);
        }
        return firstDate.getDate();
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
