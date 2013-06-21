package org.ruffalo.tarpit;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarpitReadListener implements ReadListener {

	private AsyncContext context;
	
	private Logger logger;
	
	public TarpitReadListener(AsyncContext context) {
		this.context = context;
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public void onAllDataRead() throws IOException {
		// done reading
		this.logger.info("done");
	}

	public void onDataAvailable() throws IOException {
		// read
		this.logger.info("reading...");
		
		// noop read
		this.context.getRequest().getInputStream().read(new byte[256]);
		
		this.logger.info("read!");
	}

	public void onError(Throwable arg0) {
		this.logger.error("error!", arg0);
	}

}
