package com.internetarmy.aws.util;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

public class AwsCredentialUtil {

	public static AwsCredentialsProvider getCredentialProvider(boolean isOnDevMachine, String profile) {
		if(!isOnDevMachine) {
			return InstanceProfileCredentialsProvider.create();
		} else if(profile != null && !profile.isEmpty()) {
			return ProfileCredentialsProvider.create(profile);
		}
		throw new RuntimeException("Please provide profile or run it on AWS Env.");
	}
	
	public static InstanceProfileCredentialsProvider getEc2CredentialsManager() {
		return InstanceProfileCredentialsProvider.create();
	}
	
}
