package com.internetarmy.aws.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class AwsOldCredentialUtils {

	private static final Logger log = LoggerFactory.getLogger(AwsOldCredentialUtils.class);
	
	public static AWSCredentialsProvider getCredentialsProvider(boolean onDevMachine, String profile) {
		if(onDevMachine && !CoreUtilities.isNullOrEmpty(profile)) {
			log.debug("Creating CredentialProvider with Custom Profile.");
			return new ProfileCredentialsProvider(profile);
		} else {
			log.error("Could not find any way to create a credential provider.");
			String message = "If application is running on EC2 please proide property aws.disableEc2Metadata=false \n"
					+ " else if the application is running outside AWS Environment please provide profile by aws.profile=<your_profile>, "
					+ "if you cant provide profile. you can provide properties \n"
					+ "cloud.aws.credentials.access-key, cloud.aws.credentials.secret-key, cloud.aws.region,loud.aws.credentials.session-token";
			log.error(message);
			throw new RuntimeException(message);
		}
	}
	
	public static InstanceProfileCredentialsProvider getEc2CredentialsManager() {
		return InstanceProfileCredentialsProvider.getInstance();
	}

}
