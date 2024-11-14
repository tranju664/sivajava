package com.internetarmy.aws.util;

import javax.jms.JMSException;
import javax.jms.Message;

public interface Executor {
	void execute (Message message) throws JMSException ;
}
