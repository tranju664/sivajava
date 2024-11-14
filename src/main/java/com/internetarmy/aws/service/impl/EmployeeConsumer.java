package com.internetarmy.aws.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.internetarmy.aws.util.AWSPropertiesConstants;
import com.internetarmy.aws.util.CoreUtilities;

@Service
@ConditionalOnProperty(value = AWSPropertiesConstants.IS_SQS_ENABLE_ACTUAL, havingValue = "true", matchIfMissing = false)
public class EmployeeConsumer {

	private static final Logger log = LoggerFactory.getLogger(EmployeeConsumer.class);
	
	@Value(AWSPropertiesConstants.QUEUE_NAME)
	private String employeeQueue;
	
	@Autowired
	private EmployeeExecutor executor;
	
	@Autowired
	private SQSService sqsConnection;

	public void readMessages() {
		if(CoreUtilities.isNullOrEmpty(employeeQueue)) {
			log.error("\nCannot start listneneing to queue. Because queue name is not provided. \n\nAnd you can provide queue name by using property {}. \n\n",AWSPropertiesConstants.QUEUE_NAME);
			return;
		}
		log.info("Starting Consumer.");
		sqsConnection.startConnection(executor, employeeQueue);
		log.info("Consumer Started.");
	}
}
