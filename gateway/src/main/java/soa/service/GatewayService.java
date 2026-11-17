package soa.service;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.client.RestClient;

@Service
public class GatewayService {

    DiscoveryClient DC;
    int current = 0;

    public GatewayService(DiscoveryClient DC) {
        this.DC = DC;
    }

    public Object invokeInstance(
            HttpMethod method,
            String service,
            String endpoint,
            String token,
            Object data) {

        List<ServiceInstance> services = DC.getInstances(service);

        if (services.isEmpty()) {
            throw new RuntimeException("No service found");
        }

        ServiceInstance instance = services.get(current);
        current = (current + 1) % services.size();

        String url = String.format("%s/%s", instance.getUri(), endpoint);

        RestClient client = RestClient.create();

        if (method == HttpMethod.POST) {

            RestClient.RequestBodySpec request = client.post()
                    .uri(url);

            if (token != null && !token.isBlank()) {
                request.header(HttpHeaders.AUTHORIZATION, token);
            }

            return request
                    .body(data)
                    .retrieve()
                    .body(String.class);

        } else if (method == HttpMethod.PUT) {

            return client.put()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(data)
                    .retrieve()
                    .body(String.class);

        } else if (method == HttpMethod.DELETE) {

            return client.delete()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .body(String.class);

        } else {

            return client.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .body(String.class);
        }
    }
}