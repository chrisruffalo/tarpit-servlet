package org.ruffalo.tarpit;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Servlet that is designed to trap clients and continually respond to them
 * without allowing them the ability to request the sweet release of a 
 * dropped connection.
 * <br/><br/>
 * The async servlet is PERFECT for this
 * 
 * @author Chris Ruffalo
 *
 */
@WebServlet(
	displayName="tarpit", 
	asyncSupported=true, 
	description="tarpit trap", 
	name="tarpit", 
	urlPatterns={
		"/*"
	}
)
public class TarpitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.logger.info("got get");
		
		this.execute(request, response);
	}
	
	/**
	 * Implement request logic
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.logger.info("starting async...");
		
		// create async context		
		AsyncContext asyncContext = request.startAsync();
				
		// set read listener
		ReadListener listener = new TarpitReadListener(asyncContext);
		asyncContext.getRequest().getInputStream().setReadListener(listener);
		
		// set write listener
		WriteListener wListener = new TarpitWriteListener(asyncContext);
		asyncContext.getResponse().getOutputStream().setWriteListener(wListener);
	}

}
