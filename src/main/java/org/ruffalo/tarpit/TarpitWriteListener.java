package org.ruffalo.tarpit;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarpitWriteListener implements WriteListener {

	private AsyncContext context;
	
	private Logger logger;
	
	public TarpitWriteListener(AsyncContext context) {
		this.context = context;
		
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public void onError(Throwable arg0) {
		this.logger.error("error!", arg0);
		
		// done writing
		this.context.complete();
	}

	public void onWritePossible() throws IOException {
		// log
		this.logger.info("write is possible");

		// get response
		ServletResponse response = this.context.getResponse();
		
		// say "hi, have a huge blob of crap"
		long sizeToWrite = (long)Math.pow(2, 20); //(2^10 bytes)
		int waitFactor = 0;
		int bufferSize = response.getBufferSize();
		response.setContentType("application/octet-stream");
		response.setContentLengthLong(sizeToWrite);
				
		// get output for streaming
		ServletOutputStream output = this.context.getResponse().getOutputStream();
				
		// stream!
		while(sizeToWrite > 0) {
			// create random string
			String rString = "abcdefghijklmnopqrstuvwxyz";
			byte[] bytes = rString.getBytes();
			
			// print
			output.write(bytes);
		
			// calculate wait factor
			bufferSize -= bytes.length;
			if(bufferSize <= 0) {
				bufferSize = response.getBufferSize();
				waitFactor++;
			}
			
			if(waitFactor > 0) {
				try {
					Thread.sleep(waitFactor);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}				
			}
			
			// subtract size
			sizeToWrite -= bytes.length;
		}
		
		// done writing
		this.context.complete();
	}

}
