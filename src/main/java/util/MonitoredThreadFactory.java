package util;

import general.Context;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import static util.MonitorUtil.*;
public class MonitoredThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

    private final Context context;
    public MonitoredThreadFactory(Context context){
        this.context = context;
    }
    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {

        final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        worker.setName("Worker "+String.valueOf(worker.getPoolIndex()));
        if(getContext().isShowRanges()) log("Created Worker:"+ worker.getName());
        return worker;
    }

    public Context getContext(){
        return this.context;
    }
}
