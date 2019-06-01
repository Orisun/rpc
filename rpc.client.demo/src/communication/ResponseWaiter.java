package communication;  

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import serialize.ServiceResponse;
  
/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ResponseWaiter {

	private ServiceResponse response;
	private CountDownLatch latch;
	
	public ResponseWaiter(){
		latch=new CountDownLatch(1);
	}
	
	public boolean waitResponse(long timeout) throws InterruptedException{
		return latch.await(timeout, TimeUnit.MILLISECONDS);
	}
	
	public void set(){
		latch.countDown();
	}

	public ServiceResponse getResponse() {
		return response;
	}

	public void setResponse(ServiceResponse response) {
		this.response = response;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	
}
