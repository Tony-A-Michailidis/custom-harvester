
package org.fao.geonet.harvester.oidccsw;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class OidcTokenService {
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String accessToken;
    private long expiryTime;

    public OidcTokenService(String clientId, String clientSecret, String tokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
    }

    public String getAccessToken() throws IOException {
        if (System.currentTimeMillis() > expiryTime) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private void refreshAccessToken() throws IOException {
        HttpPost post = new HttpPost(tokenUrl);
        List<NameValuePair> params = List.of(
            new BasicNameValuePair("grant_type", "client_credentials"),
            new BasicNameValuePair("client_id", clientId),
            new BasicNameValuePair("client_secret", clientSecret),
            new BasicNameValuePair("scope", "openid")
        );
        post.setEntity(new UrlEncodedFormEntity(params));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {
            String json = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(json);
            accessToken = obj.getString("access_token");
            int expiresIn = obj.getInt("expires_in");
            expiryTime = System.currentTimeMillis() + (expiresIn - 30) * 1000;
        }
    }
}
