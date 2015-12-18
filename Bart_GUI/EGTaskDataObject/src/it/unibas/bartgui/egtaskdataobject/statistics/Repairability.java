package it.unibas.bartgui.egtaskdataobject.statistics;

import java.io.Serializable;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class Repairability implements Serializable{

        private double mean;
        private double confidenceInterval;

        public Repairability(double mean, double confidenceInterval) {
            this.mean = mean;
            this.confidenceInterval = confidenceInterval;
        }
        
        
        public double getMean() {
            return mean;
        }
        
        public void setMean(double mean) {
            this.mean = mean;
        }
        
        public double getConfidenceInterval() {
            return confidenceInterval;
        }
        
        public void setConfidenceInterval(double confidenceInterval) {
            this.confidenceInterval = confidenceInterval;
        }    
}
