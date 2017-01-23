/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tester;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import scheduler.Process;
import scheduler.Processor;
import scheduler.ShareGroup;

/**
 *
 * @author Menno.VanDiermen
 */
public class ScheduleTest {
    
    public static void main(String[] args) throws InterruptedException {
        Processor cpu = new Processor(true); //pass FSS: true or false
        
        Random r = new Random();
        
        ShareGroup users = new ShareGroup(75);
        ShareGroup admin = new ShareGroup(25);
        // ShareGroup batch = new ShareGroup(25);
        
        
        
        // used for dynamically added processes, includes user process spikes:
        ArrayBlockingQueue<Process> userQ = new ArrayBlockingQueue<>(1500);
        ArrayBlockingQueue<Process> adminQ = new ArrayBlockingQueue<>(3000);
        while(userQ.offer(new Process(cpu, users)));
        while(adminQ.offer(new Process(cpu, admin)));
        
        long start = System.currentTimeMillis(); cpu.start();
        int j = 0;
        while(userQ.size() + adminQ.size() > 0) {
            if(j % 30 == 0 ) {
                for(int i=0;i<30;i++) {
                    if(userQ.isEmpty()) break;
                    cpu.enqueue(userQ.poll());
                }
            }
            for(int i = (int) (10 * Math.random()) % 5; i > 0; i--) {
                if(userQ.isEmpty()) break;
                cpu.enqueue(userQ.poll());
            }
            for(int i = (int) (10 * Math.random()) % 5; i > 0; i--) {
                if(adminQ.isEmpty()) break;
                cpu.enqueue(adminQ.poll());
            }
            Thread.sleep((long) (100 * Math.random()));
            j++; 
        }
        
        /* // used for presupplied processes: 
        for(int i = 0; i < 1500; i++) {
            cpu.enqueue(new Process(cpu, users));
            cpu.enqueue(new Process(cpu, admin));
            cpu.enqueue(new Process(cpu, admin));
            Thread.sleep((long) (10 * Math.random()));
        }
        cpu.start(); */
        
        
        long tFinish = System.currentTimeMillis()- start;
        cpu.testerDone = true;
        cpu.join();
        
        System.out.println("Busy time: " + (cpu.time - cpu.idleTime()) + ", idle time: " + cpu.idleTime());
        System.out.println("Admin group total: " + admin.getTotal());
        System.out.println("User group total: " + users.getTotal());
        System.out.println("Total number of processes: " + cpu.q.size());
        System.out.println("Tester time: " + tFinish);
        System.out.println("Total time: " + (System.currentTimeMillis() - start));
        System.out.println("Finished");
    }
}
