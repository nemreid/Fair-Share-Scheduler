package scheduler;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author Menno.VanDiermen
 */
public class ReadyQueue {

    private PriorityBlockingQueue<Process> q;
    PriorityBlockingQueue<Process> terminated;
    private Comparator<Process> c; long idleTime;
    Process IdleTimer;

    public ReadyQueue(boolean fss) {
        if (fss) {
            c = new ProcessGroupComparator();
        } else {
            c = new ProcessSystemComparator();
        }
        q = new PriorityBlockingQueue<>(100, c);
        terminated = new PriorityBlockingQueue<>(100, c);
        idleTime = 0;
    }

    public Process next() {
        if (q.isEmpty()) {
            return IdleTimer;
        }
        return q.poll();
    }

    public void add(Process p) {
        if (p.equals(IdleTimer)) {
            idleTime += IdleTimer.count; IdleTimer.count = 0;
            return;
        }
        if (p.count < p.burst) {
            q.offer(p);
        } else {
            terminated.offer(p);
        }
    }

    public int size() {
        return q.size() + terminated.size();
    }

    public boolean isEmpty() {
        return q.isEmpty();
    }

    public void schedule() {
        PriorityBlockingQueue<Process> newQ = new PriorityBlockingQueue<>(100, c);
        while(!q.isEmpty()) {
            try { 
                newQ.offer(q.poll());
            } catch(NullPointerException e) {
                
            }
        }
        q = newQ;
    }
}

class ProcessGroupComparator implements Comparator<Process> {

    @Override
    public int compare(Process o1, Process o2) {
        if (o1.getSharePriority() < o2.getSharePriority()) {
            return -1;
        }
        if (o1.getSharePriority() > o2.getSharePriority()) {
            return 1;
        }
        return 0;
    }
}

class ProcessSystemComparator implements Comparator<Process> {

    @Override
    public int compare(Process o1, Process o2) {
        if (o1.getPriority() < o2.getPriority()) {
            return -1;
        }
        if (o1.getPriority() > o2.getPriority()) {
            return 1;
        }
        return 0;
    }
}
