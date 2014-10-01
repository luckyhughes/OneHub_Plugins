package com.onetesthub.jmeter;

public class SamplePerfMetrics {
	
    String sampleLabel;
	long sampleResponseTime;
    long sampleLatency;
    long sampleCountResponseCode2xx;
    long sampleCountResponseCodeNon2xx;
    long sampleCountSuccess; 
    long sampleCountNonSuccess;
    long countSampleLabel;
    
    public long getCountSampleLabel() {
		return countSampleLabel;
	}
	public void setCountSampleLabel(long countSampleLabel) {
		this.countSampleLabel = countSampleLabel;
	}
	
    
	public String getSampleLabel() {
		return sampleLabel;
	}
	public void setSampleLabel(String sampleLabel) {
		this.sampleLabel = sampleLabel;
	}
	public long getSampleResponseTime() {
		return sampleResponseTime;
	}
	public void setSampleResponseTime(long sampleResponseTime) {
		this.sampleResponseTime = sampleResponseTime;
	}
	public long getSampleLatency() {
		return sampleLatency;
	}
	public void setSampleLatency(long sampleLatency) {
		this.sampleLatency = sampleLatency;
	}
	public long getSampleCountResponseCode2xx() {
		return sampleCountResponseCode2xx;
	}
	public void setSampleCountResponseCode2xx(long sampleCountResponseCode2xx) {
		this.sampleCountResponseCode2xx = sampleCountResponseCode2xx;
	}
	public long getSampleCountResponseCodeNon2xx() {
		return sampleCountResponseCodeNon2xx;
	}
	public void setSampleCountResponseCodeNon2xx(long sampleCountResponseCodeNon2xx) {
		this.sampleCountResponseCodeNon2xx = sampleCountResponseCodeNon2xx;
	}
	public long getSampleCountSuccess() {
		return sampleCountSuccess;
	}
	public void setSampleCountSuccess(long sampleCountSuccess) {
		this.sampleCountSuccess = sampleCountSuccess;
	}
	public long getSampleCountNonSuccess() {
		return sampleCountNonSuccess;
	}
	public void setSampleCountNonSuccess(long sampleCountNonSuccess) {
		this.sampleCountNonSuccess = sampleCountNonSuccess;
	} 
    
	

}
