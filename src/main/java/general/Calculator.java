package general;

import org.apfloat.Apfloat;

import java.util.List;

import static util.MonitorUtil.*;

public class Calculator {

    private final Context context;
    private final List<Range> rangesVertical;
    private final List<Range> rangesThreads;

    public Calculator(Context context) {
        this.context = context;
        rangesVertical = Range.generateRanges(getContext().getMax(),getContext().getVertical());
        rangesThreads = Range.generateRanges(rangesVertical.size()-1,getContext().getThreads());
    }

    public Apfloat calculate() {
        Apfloat data[] = new Apfloat[rangesVertical.size()];
        Apfloat sums[] = new Apfloat[rangesVertical.size()];

        logTime(simplify(() -> phase1(data, sums)), "Phase 1(multi)");
        logTime(simplify(() -> phase2(data, sums)), "Phase 2(single)");
        logTime(simplify(() -> phase3(data, sums)), "Phase 3(multi)");

        return logTime(()->phase4(data,sums),"Phase 4(single)");

    }

    private void phase1(Apfloat[] data, Apfloat[] sums) {
        try {
            getContext().parallel().submit(() -> {
                rangesVertical.parallelStream().forEach(e -> {
                    if(getContext().isShowRanges())log("Processing range: "+e);
                    data[e.getIndex()] = make(e.getStart() == 0 ? 1 : (2 * e.getStart()) * (2 * e.getStart() - 1));
                    sums[e.getIndex()] = make(2 * e.getStart() + 1).divide(data[e.getIndex()]);
                    for (int i = e.getStart() + 1; i < e.getEnd(); i++) {
                        data[e.getIndex()] = data[e.getIndex()].multiply(make(2 * i * (2 * i - 1)));
                        sums[e.getIndex()] = sums[e.getIndex()].add(make(2 * i + 1).divide(data[e.getIndex()]));
                    }
                });
            }).get();
        } catch (Exception e) {
            err(e);
        }

    }

    private void phase2(Apfloat[] data, Apfloat[] sums) {
        for (int i = 1; i < rangesVertical.size(); i++) {
            data[i] = data[i].multiply(data[i - 1]);
        }
    }

    private void phase3(Apfloat[] data, Apfloat[] sums) {
        try {
            getContext().parallel().submit(() -> {
                rangesThreads.parallelStream().forEach(e -> {
                    if(getContext().isShowRanges())log("Processing range: "+e);
                    for(int i= e.getStart();i<e.getEnd();i++){
                        if(i ==0) continue;
                        sums[i] = sums[i].divide(data[i-1]);
                    }
                });
            }).get();
        } catch (Exception e) {
            err(e);
        }
    }

    private Apfloat phase4(Apfloat[] data, Apfloat[] sums) {
        Apfloat total = make(0);
        for (int i = 0; i < rangesVertical.size(); i++) {
            total = total.add(sums[i]);
        }
        return  total.precision(getContext().getPrecision() - getContext().ERROR_OFFSET);
    }


    private Apfloat make(int num) {
        return new Apfloat(num, getContext().getPrecision());
    }


    public Context getContext() {
        return this.context;
    }

}
