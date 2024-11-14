package com.internetarmy.aws.service.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.BatchResultErrorEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.internetarmy.aws.config.SQSListner;
import com.internetarmy.aws.util.AWSPropertiesConstants;
import com.internetarmy.aws.util.CoreUtilities;
import com.internetarmy.aws.util.Executor;

@Service
@ConditionalOnProperty(name = AWSPropertiesConstants.IS_SQS_ENABLE_ACTUAL, matchIfMissing = false)
public class SQSService {

	private Logger log = LoggerFactory.getLogger(SQSService.class);

	@Autowired
	private AmazonSQSAsync asyncSqs;

	@Autowired
	private com.amazon.sqs.javamessaging.SQSConnection sqsConnection;

	/**
	 * This is method send messges to queue in synchronously.
	 * 
	 * @param message The message that you want to send to sqs.
	 * @param sqsUrl  a string value represents sqsqueue name(url) in AWS
	 * @return a string value represents the id of the message that posted to sqs.
	 */
	public String sendMessage(String message, String sqsUrl) {
		SendMessageResult result = asyncSqs.sendMessage(sqsUrl, message);
		String messageId = result.getMessageId();
		return messageId;
	}

	/**
	 * This method sends messages to queue asynchronously.
	 * 
	 * @param message a string value to be sent to sqs queue.
	 * @param sqsUrl  a string value represents sqsqueue name(url) in AWS
	 */
	public Future<SendMessageResult> sendMessageAsync(String message, String sqsUrl) {
		Future<SendMessageResult> sendMessageAsync = asyncSqs.sendMessageAsync(sqsUrl, message);
		return sendMessageAsync;
	}

	/**
	 * Send Bulk messages in single request.
	 * 
	 * @param messages a List<String> value represents messages/data to be sent to
	 *                 SQS
	 * @return List<BatchResultErrorEntry> incase of failure. return empty if all
	 *         messages sent successfully.
	 */
	public List<BatchResultErrorEntry> sendBatchMessages(List<String> messages, String sqsUrl) {
		List<SendMessageBatchRequestEntry> batchMessages = getBatchMessages(messages);
		SendMessageBatchResult sendMessageBatch = asyncSqs.sendMessageBatch(sqsUrl, batchMessages);
		List<BatchResultErrorEntry> failedEntries = sendMessageBatch.getFailed();
		if (failedEntries != null && !failedEntries.isEmpty()) {
			log.error("Few Messages not sent.");
			return failedEntries;
		}
		log.info("Sent all messages to queue successfully.");
		return List.of();
	}

	/**
	 * Send Bulk messages in single request asynchronously.
	 * 
	 * @param messages a List<String> value represents messages/data to be sent to
	 *                 SQS
	 * @return List<BatchResultErrorEntry> incase of failure. return empty if all
	 *         messages sent successfully.
	 */
	public Future<SendMessageBatchResult> sendBatchMessagesAsync(List<String> messages, String sqsUrl) {
		List<SendMessageBatchRequestEntry> batchMessages = getBatchMessages(messages);
		Future<SendMessageBatchResult> messageBatchAsync = asyncSqs.sendMessageBatchAsync(sqsUrl, batchMessages);
		return messageBatchAsync;
	}

	private List<SendMessageBatchRequestEntry> getBatchMessages(List<String> messages) {
		List<SendMessageBatchRequestEntry> batchMessages = new ArrayList<>();
		int i = 0;
		for (String msg : messages) {
			batchMessages.add(new SendMessageBatchRequestEntry(String.valueOf(++i), msg));
		}
		return batchMessages;
	}

	public String sendMessage(String message, String sqsUrl, String groupId, String deduplicationId) {
		// TODO Auto-generated method stub
		SendMessageRequest req = new SendMessageRequest(sqsUrl, message);
		req.setMessageGroupId(groupId);
		if (!CoreUtilities.isNullOrEmpty(deduplicationId)) {
			req.withMessageDeduplicationId(deduplicationId);
		}
		SendMessageResult result = asyncSqs.sendMessage(req);
		String messageId = result.getMessageId();
		return messageId;
	}

	public Future<SendMessageResult> sendMessageAsync(String message, String sqsUrl, String groupId,
			String deduplicationId) {
		SendMessageRequest req = new SendMessageRequest(sqsUrl, message);
		req.setMessageGroupId(groupId);
		if (!CoreUtilities.isNullOrEmpty(deduplicationId)) {
			req.withMessageDeduplicationId(deduplicationId);
		}
		Future<SendMessageResult> sendMessageAsync = asyncSqs.sendMessageAsync(req);
		return sendMessageAsync;
	}

	public List<BatchResultErrorEntry> sendBatchMessages(List<String> messages, String sqsUrl, String groupId,
			String deduplicationId) {
		List<SendMessageBatchRequestEntry> batchMessages = getBatchMessages(messages, groupId, deduplicationId);
		SendMessageBatchResult sendMessageBatch = asyncSqs.sendMessageBatch(sqsUrl, batchMessages);
		List<BatchResultErrorEntry> failedEntries = sendMessageBatch.getFailed();
		if (failedEntries != null && !failedEntries.isEmpty()) {
			log.error("Few Messages not sent.");
			return failedEntries;
		}
		log.info("Sent all messages to queue successfully.");
		return List.of();
	}

	public Future<SendMessageBatchResult> sendBatchMessagesAsync(List<String> messages, String sqsUrl, String groupId,
			String deduplicationId) {
		List<SendMessageBatchRequestEntry> batchMessages = getBatchMessages(messages);
		Future<SendMessageBatchResult> messageBatchAsync = asyncSqs.sendMessageBatchAsync(sqsUrl, batchMessages);
		return messageBatchAsync;
	}

	private List<SendMessageBatchRequestEntry> getBatchMessages(List<String> messages, String groupId,
			String deduplicationId) {
		List<SendMessageBatchRequestEntry> batchMessages = new ArrayList<>();
		int i = 0;
		for (String msg : messages) {
			SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry(String.valueOf(++i), msg);
			entry.setMessageGroupId(groupId);
			if (!CoreUtilities.isNullOrEmpty(deduplicationId)) {
				entry.setMessageDeduplicationId(deduplicationId);
			}
			batchMessages.add(new SendMessageBatchRequestEntry(String.valueOf(++i), msg));
		}
		return batchMessages;
	}

	public com.amazon.sqs.javamessaging.SQSConnection startConnection(Executor executor, String eventType) {
		try {
			log.debug("Creating session");
			Session session = sqsConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			log.info("Trying to connect to queue");
			Queue queue = session.createQueue(eventType);
			log.info("Connected to Queue ::: {}", eventType);
			MessageConsumer consumer = session.createConsumer(queue);
			log.info("Setting the Listner");
			consumer.setMessageListener(new SQSListner(executor));
			log.info("Start receiving message from Queue");
			sqsConnection.start();
			try {
				Awaitility.waitAtMost(Duration.ofSeconds(1)).until(() -> false);
			} catch (ConditionTimeoutException e) {
				log.debug("This is an expected exception to make the process wait. Error: {}", e.getMessage());
			}
		} catch (JMSException e) {
			log.error(e.getMessage());
		}
		return sqsConnection;
	}

}
