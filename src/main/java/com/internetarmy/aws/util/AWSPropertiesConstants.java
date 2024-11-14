package com.internetarmy.aws.util;

public class AWSPropertiesConstants {
	
	public static final String IS_ON_POD = "${aws.isonpod:false}";
	public static final String IS_ON_EC2 = "${aws.isOnEc2:false}";
	public static final String REGION = "${aws.region}";
	public static final String PROFILE = "${aws.profile:}";
	public static final String QUEUE_NAME = "${aws.sqs.queue:}";
	public static final String IS_SQS_ENABLE = "${aws.sqs.enable:false}";
	public static final String IS_SQS_ENABLE_ACTUAL = "aws.sqs.enable";

}
