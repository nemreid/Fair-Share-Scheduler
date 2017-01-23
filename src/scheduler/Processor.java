package scheduler;

/**
 *
 * @author Menno.VanDiermen
 */

public class Processor extends Thread {
    
    public long time, totalIdle;
    private int cycle, totalCycles;
    private final static int quantum = 50;
    
    private boolean running;
    private Process curTask; public boolean testerDone;
    public ReadyQueue q; private FSSLogger log;
    
    public Processor(boolean fss) {
        time = 0; running = true; totalCycles = 0; cycle = 0;
        q = new ReadyQueue(fss); q.IdleTimer = new IdleProcess(this);
        curTask = q.IdleTimer; log = new FSSLogger(q); testerDone = false;
        
    }
    
    public void end() {
        running = false; q.schedule(); log.close();
    }
    
    @Override
    public void run() {
        while(running) {
            time++; cycle++; 
            if(cycle >= quantum) taskSwitch();
            if(curTask.count < curTask.burst) {
                curTask.count++;
            } else {
                taskSwitch();
            }
            if(time % 1000 == 0) {
                q.schedule(); long busyTime = time-idleTime();
                log.log(time, busyTime);
            }
            if(testerDone && q.isEmpty()) end();
        }
    }
    
    public void taskSwitch() {
        cycle = 0; totalCycles++;
        curTask.lastRun = time;
        q.add(curTask);
        curTask = q.next();
    }
    
    public synchronized void enqueue(Process p) {
        q.add(p); log.addGroup(p);
    }
    
    public long idleTime() {
        return q.idleTime;
    }
}