package com.onetesthub.jmeter;

public class AggregatePerfMetrics {
	
    long countResponseCode2xx;
    long countResponseCodeNon2xx;
    long countSuccessSample; 
    long countSampleNonSuccessSample;
    long countTotalThread;
    long bytesReceived;
    //long bytesSent;
    long responseTime;
	

	public long getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
	public long getCountResponseCode2xx() {
		return countResponseCode2xx;
	}
	public void setCountResponseCode2xx(long countResponseCode2xx) {
		this.countResponseCode2xx = countResponseCode2xx;
	}
	public long getCountResponseCodeNon2xx() {
		return countResponseCodeNon2xx;
	}
	public void setCountResponseCodeNon2xx(long countResponseCodeNon2xx) {
		this.countResponseCodeNon2xx = countResponseCodeNon2xx;
	}
	public long getCountSuccessSample() {
		return countSuccessSample;
	}
	public void setCountSuccessSample(long countSuccessSample) {
		this.countSuccessSample = countSuccessSample;
	}
	public long getCountSampleNonSuccessSample() {
		return countSampleNonSuccessSample;
	}
	public void setCountSampleNonSuccessSample(long countSampleNonSuccessSample) {
		this.countSampleNonSuccessSample = countSampleNonSuccessSample;
	}
	public long getCountTotalThread() {
		return countTotalThread;
	}
	public void setCountTotalThread(long countTotalThread) {
		this.countTotalThread = countTotalThread;
	}
	public long getBytesReceived() {
		return bytesReceived;
	}
	public void setBytesReceived(long bytesReceived) {
		this.bytesReceived = bytesReceived;
	}
//	public long getBytesSent() {
//		return bytesSent;
//	}
//	public void setBytesSent(long bytesSent) {
//		this.bytesSent = bytesSent;
//	}
	
    
	
}
