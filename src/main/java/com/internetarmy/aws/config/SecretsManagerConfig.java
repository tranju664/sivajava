package com.internetarmy.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.internetarmy.aws.util.AWSPropertiesConstants;
import com.internetarmy.aws.util.AwsCredentialUtil;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClientBuilder;

@Configuration
public class SecretsManagerConfig {
	
	@Value(AWSPropertiesConstants.IS_ON_POD)
	private boolean isOnPod;
	
	@Value(AWSPropertiesConstants.IS_ON_EC2)
	private boolean isOnEc2;
	
	@Value(AWSPropertiesConstants.REGION)
	private String region;
	
	@Value(AWSPropertiesConstants.PROFILE)
	private String profile;
	
	@Bean
	SecretsManagerAsyncClient getSecretsManagerCilent() {
		if(isOnPod) {
			return SecretsManagerAsyncClient.builder().region(Region.of(region)).build();
		}
		AwsCredentialsProvider credentialProvider = null;
		if(isOnEc2) {
			credentialProvider = AwsCredentialUtil.getEc2CredentialsManager();
		}
		if(credentialProvider == null) {
			credentialProvider = AwsCredentialUtil.getCredentialProvider(!isOnPod, profile);
		}
		SecretsManagerAsyncClientBuilder clientBuilder = SecretsManagerAsyncClient.builder();
		if(region == null || region.isBlank()) {
			throw new RuntimeException("Please provide region");
		}
		clientBuilder.region(Region.of(region));
		return clientBuilder.credentialsProvider(credentialProvider).build();
	}

}
