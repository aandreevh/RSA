package general;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Range {

    private final int index;
    private final int start;
    private final int end;

    public Range(int index,int start,int end){
        this.index = index;
        this.start = start;
        this.end = end;
    }

    public static List<Range> generateRanges(int max,int points){
        ArrayList<Integer> data = new ArrayList<>();

        int step = (max+1)/points;
        int off = max +1 - step*points;

        int cur =0;

        for(int i=0;i<points+1;i++){
            final int inc = step + ((i< off) ? 1:0);
            data.add(cur);
            if(inc ==0) break;
            cur += inc;
        }

        return Collections.unmodifiableList(Range.ranges(data));
    }


    public static List<Range> ranges(List<Integer> points){
        List<Range> out = new ArrayList<>();
        int index= 0;
        int prev = 0;
        for(int p : points){
            if(index !=0)
                out.add(new Range(index-1,prev,p));
                index++;
                prev = p;

        }
        return out;
    }

    @Override
    public String toString() {
        return "{" +
                "index=" + index +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    public int getIndex() {
        return index;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }


}
