package com.onetesthub.jmeter;

public class MySampleResult {
	
	String threadName;
    String sampleLabel;
    long responseTime;
    long latency;
    String responseCode;
    long isSuccessful; 
   // long sentBytes;
    long receivedBytes;
    long threadCount;
    long endTime;
    
    
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public String getSampleLabel() {
		return sampleLabel;
	}
	public void setSampleLabel(String sampleLabel) {
		this.sampleLabel = sampleLabel;
	}
	public long getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
	public long getLatency() {
		return latency;
	}
	public void setLatency(long latency) {
		this.latency = latency;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public long getIsSuccessful() {
		return isSuccessful;
	}
	public void setIsSuccessful(long isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
//	public long getSentBytes() {
//		return sentBytes;
//	}
//	public void setSentBytes(long sentBytes) {
//		this.sentBytes = sentBytes;
//	}
	public long getReceivedBytes() {
		return receivedBytes;
	}
	public void setReceivedBytes(long receivedBytes) {
		this.receivedBytes = receivedBytes;
	}
	public long getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(long threadCount) {
		this.threadCount = threadCount;
	}
    

}
