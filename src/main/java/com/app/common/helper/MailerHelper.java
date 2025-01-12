/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.common.helper;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 *
 * @author inuHa
 */
public class MailerHelper {

    private JSONArray data;

    public MailerHelper() {
        Dotenv.configure().systemProperties().load();
    }

    public JSONArray getData() {
        return data;
    }

    public boolean send(String to, String title, String body) {
        MailjetRequest request;
        MailjetResponse response;

        ClientOptions options = ClientOptions.builder()
                .apiKey(System.getProperty("MJ_APIKEY_PUBLIC"))
                .apiSecretKey(System.getProperty("MJ_APIKEY_PRIVATE"))
                .build();

        MailjetClient client = new MailjetClient(options);

        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", System.getProperty("MJ_FROM_EMAIL"))
                                        .put("Name", System.getProperty("MJ_FROM_NAME")))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", to)))
                                .put(Emailv31.Message.SUBJECT, title)
                                .put(Emailv31.Message.HTMLPART, body)));
        try {
            response = client.post(request);
            this.data = response.getData();
            
            return response.getStatus() == 200;
        } catch (MailjetException e) {
            e.printStackTrace();
        }
        return false;
    }
}
