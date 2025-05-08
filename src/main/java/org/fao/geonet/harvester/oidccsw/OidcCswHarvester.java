
package org.fao.geonet.harvester.oidccsw;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.fao.geonet.kernel.harvest.harvester.AbstractHarvester;

public class OidcCswHarvester extends AbstractHarvester {
    private OidcTokenService tokenService;

    public OidcCswHarvester(String clientId, String clientSecret, String tokenUrl) {
        this.tokenService = new OidcTokenService(clientId, clientSecret, tokenUrl);
    }

    @Override
    protected void doHarvest() throws Exception {
        String token = tokenService.getAccessToken();
        HttpGet cswRequest = new HttpGet("https://csw.service.com/?request=GetRecords");
        cswRequest.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(cswRequest)) {
            String xml = EntityUtils.toString(response.getEntity());
            ingestMetadata(xml);
        } catch (Exception e) {
            System.err.println("Error during CSW harvest: " + e.getMessage());
        }
    }

    private void ingestMetadata(String xml) {
        // Logic to parse and ingest metadata into GeoNetwork
    }
}
