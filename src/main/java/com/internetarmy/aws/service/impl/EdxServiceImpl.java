package com.internetarmy.aws.service.impl;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internetarmy.aws.service.EdxService;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretResponse;

@Service
public class EdxServiceImpl implements EdxService {
	
	@Autowired
	private SecretsManagerAsyncClient secretManager;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public String getSecretValue(String secretName) {
		GetSecretValueRequest request = GetSecretValueRequest.builder().secretId(secretName).build();
		CompletableFuture<GetSecretValueResponse> futureSecret = secretManager.getSecretValue(request);
		Awaitility.await().until(() -> futureSecret.isDone());
		try {
			GetSecretValueResponse response = futureSecret.get();
			String actualSecret = response.secretString();
			return actualSecret;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	@SuppressWarnings("unchecked")
	public String putSecret(String secretName, String key, String value) {
		try {
			String secretValue = getSecretValue(secretName);
			HashMap<String, String> map = (HashMap<String, String>)mapper.readValue(secretValue, HashMap.class);
			map.put(key, value);
			String newSecret = mapper.writeValueAsString(map);
			UpdateSecretRequest updateRequest = UpdateSecretRequest.builder().secretId(secretName).secretString(newSecret).build();
			CompletableFuture<UpdateSecretResponse> futureResponse = secretManager.updateSecret(updateRequest);
			Awaitility.await().until(() -> futureResponse.isDone());
			UpdateSecretResponse response = futureResponse.get();
			String versionId = response.versionId();
			return versionId;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
