/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jnctn.bulkupload.service.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import com.jnctn.bulkupload.util.LogFactory;

/**
 *
 * @author martin
 */
public class HttpConnector {

    private static final Logger logger = Logger.getLogger(HttpConnector.class);

    public String sendRequest(String url, Map<String, String> stringParams) throws IOException {

        HttpClient client = new HttpClient();
        //clientParams.set
        PostMethod method = new PostMethod(url);
        byte[] responseBody = null;

        try {
            //Header
            Header header = new Header("Accept", "application/json");
            method.setRequestHeader(header);
            //Parameters
            HttpMethodParams methodParams = new HttpMethodParams();
            methodParams.setContentCharset("UTF-8");
            //Body
            List<NameValuePair> body = new ArrayList<NameValuePair>(stringParams.size());
            for (String paramName : stringParams.keySet()) {
                logger.info("Name : " + paramName + ", " + stringParams.get(paramName));
                body.add(new NameValuePair(paramName, stringParams.get(paramName)));
            }
            method.setRequestBody(body.toArray(new NameValuePair[]{}));
            int response = client.executeMethod(method);
            if (response != HttpStatus.SC_OK) {
                throw new IllegalStateException("Http request failed: " + response);
            }
            responseBody = method.getResponseBody();
            // System.out.println("got response body " + new String(responseBody, "UTF-8"));
        } finally {
            method.releaseConnection();
        }

        return new String(responseBody, "UTF-8");
    }
}
