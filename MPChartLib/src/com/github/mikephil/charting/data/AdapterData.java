
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class AdapterData extends BarLineScatterCandleBubbleData<ILineDataSet> {

    public AdapterData() {
        super();
    }

    public AdapterData(List<String> xVals) {
        super(xVals);
    }

    public AdapterData(String[] xVals) {
        super(xVals);
    }

    public AdapterData(List<String> xVals, List<ILineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public AdapterData(String[] xVals, List<ILineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public AdapterData(List<String> xVals, ILineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public AdapterData(String[] xVals, ILineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<ILineDataSet> toList(ILineDataSet dataSet) {
        List<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(dataSet);
        return sets;
    }


}
