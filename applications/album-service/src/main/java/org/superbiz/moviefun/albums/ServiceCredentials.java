package org.superbiz.moviefun.albums;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServiceCredentials {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String, List<VcapService>>> jsonType = new TypeReference<Map<String, List<VcapService>>>() {
    };

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final String vcapServicesJson;

    public ServiceCredentials(String vcapServicesJson) {
        this.vcapServicesJson = vcapServicesJson;
    }

    public String getCredential(String serviceName, String credentialKey) {

        Map<String, List<VcapService>> vcapServices;

        try {
            vcapServices = objectMapper.readValue(vcapServicesJson, jsonType);

            return vcapServices
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(service -> service.name.equalsIgnoreCase(serviceName))
                    .findFirst()
                    .map(service -> service.credentials)
                    .flatMap(credentials -> Optional.ofNullable((String) credentials.get(credentialKey)))
                    .orElseThrow(() -> new IllegalStateException("No " + serviceName + " found in VCAP_SERVICES"));

        } catch (IOException e) {
            throw new IllegalStateException("No VCAP_SERVICES found", e);
        }
    }

    static class VcapService {
        String name;
        Map<String, Object> credentials;

        void setName(String name) {
            this.name = name;
        }

        void setCredentials(Map<String, Object> credentials) {
            this.credentials = credentials;
        }
    }
}