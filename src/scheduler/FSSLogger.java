/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Menno.VanDiermen
 */
public class FSSLogger {

    ReadyQueue q;
    BufferedWriter logger;
    ShareGroup[] groups;
    int idx; boolean change;

    public FSSLogger(ReadyQueue queue) {
        q = queue; change = false;
        groups = new ShareGroup[10];
        idx = 0;
        File f = new File("log.txt");
        if(f.exists()) f.delete();
        try {
            logger = new BufferedWriter(new FileWriter(f, true));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void close() {
        try {
            logger.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void log(long time, long busyTime) {
        if(time < 1) {
            time = 1; busyTime = 1;
        }
        try {
            String entry = "" + time;
            for (ShareGroup g : groups) {
                if (g != null) {
                    double rate = 100 * (double) g.getTotal()/busyTime;
                    if(rate != g.lastRate) {
                        entry += "\t" + rate;
                        g.lastRate = rate; change = true;
                    }
                }
            }
            if(change) {
                logger.append(entry); logger.newLine();
                change = false;
            }
        } catch (IOException e) {

        }
    }

    public void addGroup(Process p) {
        if (p.group != null) {
            ShareGroup g = p.group;
            for (ShareGroup grp : groups) {
                if (g.equals(grp)) {
                    return;
                }
            }
            groups[idx] = g;
            idx++;
        }
    }
}
