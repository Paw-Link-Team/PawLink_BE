package com.gdg.backend.global.oauth.factory;

import com.gdg.backend.global.oauth.service.SocialOauthService;
import com.gdg.backend.user.domain.Provider;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class SocialOauthServiceFactory {

    private final Map<Provider, SocialOauthService> oauthServiceMap = new EnumMap<Provider, SocialOauthService>(Provider.class);

    public SocialOauthServiceFactory(List<SocialOauthService> services) {
        for (SocialOauthService service : services) {
            oauthServiceMap.put(service.getProviderType(), service);
        }
    }

    public SocialOauthService get(Provider provider) {
        return oauthServiceMap.get(provider);
    }

}
