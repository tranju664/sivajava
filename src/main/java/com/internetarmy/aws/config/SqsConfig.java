package com.internetarmy.aws.config;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.internetarmy.aws.util.AWSPropertiesConstants;
import com.internetarmy.aws.util.AwsOldCredentialUtils;


@Configuration
@ConditionalOnProperty(name = AWSPropertiesConstants.QUEUE_NAME, matchIfMissing = false)
public class SqsConfig {
	
	private static final Logger log = LoggerFactory.getLogger(SqsConfig.class);

	@Value(AWSPropertiesConstants.IS_ON_POD)
	private boolean isOnPod;
	
	@Value(AWSPropertiesConstants.IS_ON_EC2)
	private boolean isOnEc2;
	
	@Value(AWSPropertiesConstants.REGION)
	private String region;
	
	@Value(AWSPropertiesConstants.PROFILE)
	private String profile;

	@Bean
	AmazonSQSAsync sqsClient() {
		if(isOnPod) {
			log.info("App running on Pod, creating client using default configuration");
			return AmazonSQSAsyncClientBuilder.standard().withRegion(region).build();
		}
		log.debug("CredentialsProvider for SQS is obtained using either EC2Role/CustomerProfile.");
		AWSCredentialsProvider credsProvider = null;
		if(isOnEc2) {
			credsProvider = AwsOldCredentialUtils.getEc2CredentialsManager();
		}
		credsProvider = AwsOldCredentialUtils.getCredentialsProvider(!isOnPod, profile);
		AmazonSQSAsyncClientBuilder clientBuilder = AmazonSQSAsyncClientBuilder.standard();
			clientBuilder.withRegion(region);
		return clientBuilder
				.withCredentials(credsProvider).build();
	}


	@Bean
	SQSConnection sqsConnection() throws JMSException {
		log.debug("Creating SQSConnection Bean.");
		SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), sqsClient());
		return connectionFactory.createConnection();
		
	}
}
