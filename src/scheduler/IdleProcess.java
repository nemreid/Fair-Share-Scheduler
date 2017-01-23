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
public class IdleProcess extends Process {
    
    IdleProcess(Processor cpu) {
        super(cpu); this.setBurst(Integer.MAX_VALUE);
    }
    
    @Override
    public double getPriority() {
        return Double.MAX_VALUE;
    }
    
    @Override
    public double getSharePriority() {
        return Double.MAX_VALUE;
    }
    
}
