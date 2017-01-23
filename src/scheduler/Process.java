/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

/**
 *
 * @author Menno.VanDiermen
 */
public class Process {
    Processor cpu; 
    int count, burst; long start;
    long lastRun; static int i = 0;
    ShareGroup group;
    
    public Process(Processor cpu) {
        this.cpu = cpu; count = 0; start = cpu.time;
        lastRun = 1;
        burst = 10 + (int) ((100000 * Math.random()) % 290);
    }
    
    public Process(Processor cpu, ShareGroup grp) {
        this(cpu);
        group = grp;
        group.addMember(this);
    }
    
    public void setGroup(ShareGroup grp) {
        group = grp;
    }
    
    public long getAge() {
        return cpu.time - this.start;
    }
    
    public long getWait() {
        return cpu.time - this.lastRun;
    }
    
    public double getPriority() {
        return (double) count/lastRun;
    }
    
    public double getSharePriority() {
        double priority = getPriority();
        priority = priority % 1;
        return group.getPriority() + priority;
    }
    
    public void setBurst(int burst) {
        this.burst = burst;
    }
    
    @Override
    public String toString() {
        i++;
        return "Process " + i + " -- Count: " + count + " Burst: " + burst 
        + " Priority: " + getPriority() + ", Age: " + getAge();
    }
}