package com.onetesthub.jmeter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.thoughtworks.xstream.mapper.Mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class OnetesthubAPIClient {

	private static final Logger log = LoggingManager.getLoggerForClass();
	public static final String COLOR_NONE = "none";
	public static final String[] colors = { COLOR_NONE, "red", "green", "blue",
			"gray", "orange", "violet", "cyan", "black" };
	public static final String STATUS_DONE = "4";
	private final HttpClient httpClient = new HttpClient();
	private final StatusNotifierCallback notifier;
	private final String project;
	private final String address;
	private final String token;
	private final String colorFlag;
	private final String title;
	private final static int TIMEOUT = 5;
	WebResource webResource;
	ObjectMapper mapper;

	public OnetesthubAPIClient(StatusNotifierCallback informer,
			String aAddress, String aToken, String projectName,
			String aColorFlag, String aTitle) {
		project = projectName;
		address = aAddress;
		token = aToken;
		notifier = informer;
		colorFlag = aColorFlag;
		title = aTitle;

		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT * 1000);
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(TIMEOUT * 1000);

		Client client = Client.create();

		webResource = client
				.resource("http://54.68.127.15:8086/db/performance/series?u=root&p=root");
		
		//mapper = new ObjectMapper();
	}

	public void sendOnlineData(List<InfluxObject> data) throws IOException {
		
		mapper = new ObjectMapper();
		
		String jsonobject = mapper.writeValueAsString(data);

		try {

			log.info("size of json final data to send to influx db is : "
					+ data.size());

			log.info("name of first object in final data to send to influx db is : "
					+ data.get(0).getName());

			ClientResponse response = webResource.type("application/json")
					.accept("application/json")
					.post(ClientResponse.class, jsonobject);

			log.info("#########json data in string is : " + jsonobject);
			log.info("#########response code is: " + response.getStatus());
			log.info("############response  is: " + response.getLength());

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			log.info("?????Output from Server .... \n");
			String output = response.getEntity(String.class);
			log.info("??????"+output);

		} catch (Exception e) {

			e.printStackTrace();

		}

		String dataStr = data.toString();
		log.debug("Sending active test data: " + dataStr);

		// restclient request to send data
	}

	// public String startOnline() throws IOException {
	// String uri = address + "api/active/receiver/start/";
	// LinkedList<Part> partsList = new LinkedList<Part>();
	// partsList.add(new StringPart("token", token));
	// partsList.add(new StringPart("projectKey", project));
	// partsList.add(new StringPart("title", title));
	// String[] res = multipartPost(partsList, uri, HttpStatus.SC_CREATED);
	// JSONObject obj = JSONObject.fromObject(res[0]);
	// return address + "gui/active/" + obj.optString("OnlineID", "N/A") + "/";
	// }

	// public void sendOnlineData(JSONArray data) throws IOException {
	// String uri = address + "api/active/receiver/data/";
	// LinkedList<Part> partsList = new LinkedList<Part>();
	// String dataStr = data.toString();
	// log.debug("Sending active test data: " + dataStr);
	// partsList.add(new StringPart("data", dataStr));
	// multipartPost(partsList, uri, HttpStatus.SC_ACCEPTED);
	// }

	// public void endOnline() throws IOException {
	// String uri = address + "api/active/receiver/stop/";
	// LinkedList<Part> partsList = new LinkedList<Part>();
	// multipartPost(partsList, uri, HttpStatus.SC_RESET_CONTENT);
	// }

	// private File gzipFile(File src) throws IOException {
	// // Never try to make it stream-like on the fly, because content-length
	// still required
	// // Create the GZIP output stream
	// String outFilename = src.getAbsolutePath() + ".gz";
	// notifier.notifyAbout("Gzipping " + src.getAbsolutePath());
	// GZIPOutputStream out = new GZIPOutputStream(new
	// FileOutputStream(outFilename));
	//
	// // Open the input file
	// FileInputStream in = new FileInputStream(src);
	//
	// // Transfer bytes from the input file to the GZIP output stream
	// byte[] buf = new byte[1024];
	// int len;
	// while ((len = in.read(buf)) > 0) {
	// out.write(buf, 0, len);
	// }
	// in.close();
	//
	// // Complete the GZIP file
	// out.finish();
	// out.close();
	//
	// src.delete();
	//
	// return new File(outFilename);
	// }

	// private void setTestTitle(int testID, String trim) throws IOException {
	// String uri = address + "api/test/edit/title/" + testID + "/?title=" +
	// URLEncoder.encode(trim, "UTF-8");
	// multipartPost(new LinkedList<Part>(), uri, HttpStatus.SC_NO_CONTENT);
	// }
	//
	// private void setTestColor(int testID, String colorFlag) throws
	// IOException {
	// String uri = address + "api/test/edit/color/" + testID + "/?color=" +
	// colorFlag;
	// multipartPost(new LinkedList<Part>(), uri, HttpStatus.SC_NO_CONTENT);
	// }
	//
	// private String getUploaderURI() {
	// return address + "api/file/upload/?format=csv";
	// }
	//
	// protected String[] getUploadStatus(int queueID) throws IOException {
	// String uri = address + "api/file/status/" + queueID + "/?format=csv";
	// return multipartPost(new LinkedList<Part>(), uri, HttpStatus.SC_OK);
	// }
	//
	// protected String[] multipartPost(LinkedList<Part> parts, String URL, int
	// expectedSC) throws IOException {
	// log.debug("Request POST: " + URL);
	// parts.add(new StringPart("token", token));
	//
	// PostMethod postRequest = new PostMethod(URL);
	// MultipartRequestEntity multipartRequest = new
	// MultipartRequestEntity(parts.toArray(new Part[parts.size()]),
	// postRequest.getParams());
	// postRequest.setRequestEntity(multipartRequest);
	// int result = httpClient.executeMethod(postRequest);
	// if (result != expectedSC) {
	// String fname = File.createTempFile("error_", ".html").getAbsolutePath();
	// notifier.notifyAbout("Saving server error response to: " + fname);
	// FileOutputStream fos = new FileOutputStream(fname);
	// FileChannel resultFile = fos.getChannel();
	// resultFile.write(ByteBuffer.wrap(postRequest.getResponseBody()));
	// resultFile.close();
	// throw new HttpException("Request returned not " + expectedSC +
	// " status code: " + result);
	// }
	// byte[] bytes = postRequest.getResponseBody();
	// if (bytes == null) {
	// bytes = new byte[0];
	// }
	// String response = new String(bytes);
	// return response.trim().split(";");
	// }
}
