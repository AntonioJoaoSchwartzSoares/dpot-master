package com.fivrr.radio;

import java.util.ArrayList;

import android.content.Context;

import android.widget.TableLayout;
import android.widget.TextView;

import com.dpot.radio.R;

public class DataManager {

    private Context context;

    public DataManager(Context context) {
        this.context = context;
    }

    public void createRadioListForRadioScreen(TableLayout UIRadioList, ArrayList<String> userRadios, TextView radioListName, TextView radioListLocation) {
        ArrayList<RadioListElement> radioArray = new ArrayList<RadioListElement>();
        radioArray.add(new RadioListElement(context, context.getResources().getString(R.string.radio_name), context.getResources().getString(R.string.radio_location), context.getResources().getString(R.string.radio_url)));
        UIRadioList.removeAllViews();
        RadioList radioList = new RadioList(context, radioArray, UIRadioList);
        radioList.addRadioStations(radioListName, radioListLocation);

    }

}





















