package com.internetarmy.aws.config;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.internetarmy.aws.util.Executor;

public class SQSListner implements javax.jms.MessageListener {

	private static final Logger log = LoggerFactory.getLogger(SQSListner.class);
//	private static final EdxLogger log = EdxLogger.getLogger(SQSListner.class);

	private Executor executor;

	public SQSListner(Executor executor) {
		if(executor == null) {
			log.error("Recieved null value for parameter executor.");
			throw new NullPointerException("Parameter executor is required");
		}
		log.debug("Creating Listner");
		this.executor = executor;
	}

	@Override
	public void onMessage(javax.jms.Message message) {
		log.info("Starting listning");
		try {
			executor.execute(message);
			log.info("Started Listnening");
		} catch (JMSException e) {
			log.error(e.getMessage());
		}
		log.info("Listner started.");
	}
}