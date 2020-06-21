package util;

import general.Context;
import org.apfloat.Apfloat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class MonitorUtil {

    public static final DateFormat DATE_FORMAT =new SimpleDateFormat("yy/MM/dd HH:mm:ss");

    private static boolean quiet = false;
    public static void setQuiet(boolean quiet){
        MonitorUtil.quiet = quiet;
    }

    private static int monitorDepth = 0;
    public static  int getMonitorDepth(){
        return monitorDepth;
    }

    public static boolean isQuiet(){
        return  quiet;
    }

    public static void log(Object message){
        if(!isQuiet()) System.out.println(prefix()+" "+message);
    }
    public static void err(Object message){
        if(!isQuiet()) System.err.println(prefix()+" "+message);
    }

    private static String prefix(){
        String depth = new String(new char[getMonitorDepth()]).replace("\0", "  ");
       return  "["+(DATE_FORMAT.format(new Date()))+"] "+Thread.currentThread().getName()+":"+depth;
    }

    public static<T> Supplier<Object> simplify(Runnable sup){
        return ()->{
            sup.run();
            return (Object)null;
        };
    }

    public static<T> T logTime(Supplier<T> functor,String phaseName){

        return MonitorUtil.monitorTime(functor, s-> {
            log("Starting "+phaseName+"");
            },(u,v)->{
            log("Time for "+phaseName+": "+v+" s.");
            }).apply(phaseName);
    }

    public static<A,T> Function<A,T> monitorTime(Supplier<T> functor,
                                                 Consumer<A> OnStart,
                                                 BiConsumer<T,Double> OnComplete){
        AtomicReference<Long> time = new AtomicReference<>((long) 0);
        return monitor(functor,a->{
          time.set(System.nanoTime());
          OnStart.accept(a);
        },(e)->{
          long t=  System.nanoTime() - time.get();
          t = TimeUnit.NANOSECONDS.toMillis(t);
          OnComplete.accept(e,(double)t/1000d);
        });
    }

    public static<A,T> Function<A,T> monitor(Supplier<T> functor,
                                             Consumer<A> OnStart,
                                             Consumer<T> OnComplete){
        return a->{
            if(OnStart != null) OnStart.accept(a);
            monitorDepth++;
            T element = functor.get();
            monitorDepth--;
            if(OnComplete !=  null) OnComplete.accept(element);
            return element;
        };
    }

}
