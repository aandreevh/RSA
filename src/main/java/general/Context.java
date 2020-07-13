package general;

import util.MonitoredThreadFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Context implements AutoCloseable{
    public static final Context DEFAULT_CONTEXT = new Context(Context.DEFAULT_THREADS,
            Context.DEFAULT_PRECISION,
            Context.DEFAULT_OUTFILE,
            Context.DEFAULT_TEST,
            Context.DEFAULT_VERTICAL,
            Context.DEFAULT_QUIET,
            Context.DEFAULT_RANGES);

    public static final int DEFAULT_THREADS = 1;
    public static final int DEFAULT_PRECISION = 1000;
    public static final String DEFAULT_OUTFILE = "./result.txt";
    public static final boolean DEFAULT_QUIET = false;
    public static final int DEFAULT_VERTICAL = 1024;
    private static final boolean DEFAULT_RANGES=false;
    private static final String DEFAULT_TEST=null;

    private static final String DEFAULT_PARAM_THREADS = "-t";
    private static final String DEFAULT_PARAM_PRECISION = "-p";
    private static final String DEFAULT_PARAM_OUTFILE = "-o";
    private static final String DEFAULT_PARAM_QUIET = "-q";
    private static final String DEFAULT_PARAM_VERTICAL = "-v";
    private static final String DEFAULT_PARAM_RANGES = "-r";
    private static final String DEFAULT_PARAM_TEST = "-f";

    private final int threads;
    private final int precision;
    private final String outputFile;
    private final boolean quiet;
    private final int vertical;
    private final boolean showRanges;
    private final String testFile;

    private final int max;

    private final ForkJoinPool pool;

    public final int ERROR_OFFSET = 5;
    public Context(int threads,int precision,String outputFile,String testFile,int vertical,boolean quiet,boolean showRanges){
        this.threads = threads;
        this.precision = precision+ERROR_OFFSET;
        this.outputFile = outputFile;
        this.quiet = quiet;
        this.showRanges = showRanges;
        this.testFile = testFile;

        this.max = getPrecision()+ERROR_OFFSET;
        this.vertical = vertical;
        this.pool = new ForkJoinPool(getThreads(),new MonitoredThreadFactory(this),null,true);
    }


    public static Context generateFromArgs(String... args){

        int threads = hasArg(DEFAULT_PARAM_THREADS,args) ?
                Integer.parseInt(getArgValue(DEFAULT_PARAM_THREADS,args)) : DEFAULT_THREADS;

        int precision= hasArg(DEFAULT_PARAM_PRECISION,args) ?
                Integer.parseInt(getArgValue(DEFAULT_PARAM_PRECISION,args)) : DEFAULT_PRECISION;

        int vertical = hasArg(DEFAULT_PARAM_VERTICAL,args) ?
                Integer.parseInt(getArgValue(DEFAULT_PARAM_VERTICAL,args)) : DEFAULT_VERTICAL;

        String outfile = hasArg(DEFAULT_PARAM_OUTFILE,args) ?
                getArgValue(DEFAULT_PARAM_OUTFILE,args) : DEFAULT_OUTFILE;

        String testfile = hasArg(DEFAULT_PARAM_TEST,args) ?
                getArgValue(DEFAULT_PARAM_TEST,args) : DEFAULT_TEST;

        boolean quiet = hasArg(DEFAULT_PARAM_QUIET,args);

        boolean showRanges = hasArg(DEFAULT_PARAM_RANGES, args);


        return new Context(threads,precision,outfile,testfile,vertical,quiet,showRanges);
    }

    private static boolean hasArg(String arg,String... args){
        for (String v : args) {
            if(arg.equals(v)) return true;
        }
        return false;
    }

    private static String getArgValue(String arg,String... args){
        for(int i=0;i<args.length;i++){
            if(args[i].equals(arg)) return args[i+1];
        }
        return "";
    }

    @Override
    public String toString() {
        return "general.Context{" +
                "threads=" + threads +
                ", precision=" + precision +
                ", outputFile='" + outputFile + '\'' +
                ", testfile='" + testFile + '\'' +
                ", quiet=" + quiet +
                ", showRanges=" + showRanges +
                ", vertical=" + vertical +
                ", max=" + max +
                '}';
    }

    public int getThreads() {
        return threads;
    }

    public int getPrecision() {
        return precision;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getTestFile(){
        return this.testFile;
    }

    public int getVertical(){
        return this.vertical; }

    public boolean isShowRanges(){
        return  this.showRanges;
    }

    public int getMax(){
        return this.max;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public ForkJoinPool parallel(){
        return this.pool;
    }

    @Override
    public void close() throws Exception {
        pool.shutdown();
    }
}
