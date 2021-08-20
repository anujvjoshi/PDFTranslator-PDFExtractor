package com.sydac.pdfvalidator.app.python.api;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PythonApi {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private String pythonServiceIp = "http://punw1045.simulation.lan";

	private String pythonServicePort = "5000";

	private HttpHeaders headers;

	private PythonApi() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	public String translateText(String[] srcLanguage, String destLanguage, String inputText)
			throws UnsupportedEncodingException, URISyntaxException, JsonMappingException, JsonProcessingException {
		if (isInValidInput(inputText)) {
			return inputText;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("inputText", inputText.trim());
		jsonObject.put("srcLanguage", srcLanguage);
		jsonObject.put("destLanguage", destLanguage);

		final String baseUrl = pythonServiceIp + ":" + pythonServicePort + "/translation/with-language-info";

		return sendTranslationRequest(jsonObject, baseUrl);
	}

	private boolean isInValidInput(String inputText) {
		return inputText.trim().equals("") || inputText.trim().equals("");
	}

	private String sendTranslationRequest(JSONObject jsonObject, final String baseUrl)
			throws JsonProcessingException, JsonMappingException {

		String translatedText = "";
		try {

			HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(), headers);

			ResponseEntity<String> result = restTemplate.postForEntity(baseUrl, request, String.class);

			JsonNode root = objectMapper.readTree(result.getBody());

			translatedText = root.path("translatedText").asText().trim();

			System.out.println(jsonObject.getString("inputText") + " >> " + translatedText);

		} catch (Exception e) {
			System.out.println("PythonApi.sendTranslationRequest() url: " + baseUrl + "data: " + jsonObject);
			throw new RuntimeException(e);
		}
		return translatedText;
	}

	public String translateTextToEnglish(String inputText)
			throws UnsupportedEncodingException, URISyntaxException, JsonMappingException, JsonProcessingException {
		if (isInValidInput(inputText)) {
			return inputText;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("inputText", inputText.trim());

		final String baseUrl = pythonServiceIp + ":" + pythonServicePort
				+ "/translation/auto-detect-lang-and-translate-to-english/";

		return sendTranslationRequest(jsonObject, baseUrl);
	}
}
