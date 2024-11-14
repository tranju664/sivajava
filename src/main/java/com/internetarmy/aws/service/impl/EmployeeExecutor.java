package com.internetarmy.aws.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internetarmy.aws.model.Employee;
import com.internetarmy.aws.util.AWSPropertiesConstants;
import com.internetarmy.aws.util.Executor;

@Service
@ConditionalOnProperty(value = AWSPropertiesConstants.IS_SQS_ENABLE_ACTUAL, havingValue = "true", matchIfMissing = false)
public class EmployeeExecutor implements Executor{
	
	private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
	
	@Autowired
	private EmployeeService service;
	
	@Autowired
	private ObjectMapper mapper;
	
	
	@Override
	public void execute(Message message) throws JMSException {
		try {
			message.acknowledge();
			String body = ((TextMessage) message).getText();
			Employee emp = mapper.readValue(body, Employee.class);
			emp = service.saveEmployee(emp);
			log.info("Employee saved. EmpId: {}", emp.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
