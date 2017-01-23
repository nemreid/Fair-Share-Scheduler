package scheduler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Menno.VanDiermen
 */
public class ShareGroup {
    int shares; long oldest;
    final List<Process> members;
    Processor cpu; double lastRate;
    
    public ShareGroup (int shares) {
        this.shares = shares; oldest = 1; lastRate = 0.0;
        members = new CopyOnWriteArrayList<>();
    }
    
    public void addMember(Process p) {
        members.add(p);
    }
    
    public synchronized int getTotal() {
        int count = 0;
        for(Process p: members) {
            count += p.count;
        }
        return count;
    }

    double getPriority() {
        if(cpu == null) cpu = members.get(0).cpu;
        long busyTime = cpu.time - cpu.idleTime();
        double allottedShare = busyTime * ((double) shares/100);
        double actualShare = (double) getTotal() - allottedShare;
        return actualShare;
    }
}
