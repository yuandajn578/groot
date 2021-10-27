package com.choice.cloud.architect.groot.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApolloClientConfig {

    private String portalUrl;

    private String token;

    private List<String> envs;
}
