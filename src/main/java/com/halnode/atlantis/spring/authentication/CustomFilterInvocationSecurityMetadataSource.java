package com.halnode.atlantis.spring.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halnode.atlantis.spring.authorization.AuthorizationConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;

import static com.halnode.atlantis.util.Constants.URL_CONFIGURATIONS_FILE_LOCATION;

/**
 * Custom implementation of {@link FilterInvocationSecurityMetadataSource}
 * It will generate the Collection of ConfigAttribute(i.e., List<ConfigAttribute>)
 * which will be used by the {@link CustomAccessDecisionManager}.
 * The configurations/permissions for the urls are fetched from a json file
 *
 * @author gowtham
 */
@Log4j2
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private Map<RequestMatcher, Collection<ConfigAttribute>> configurationMap = null;

    /**
     * This function will read urls and it's required permission(i.e, roles) from a configuration json file.
     * The configurations are converted as a Map<RequestMatcher,Collection<ConfigAttribute>>
     * in order to decide the access for the requested url ( REQUESTED URL and it's info are fetched from HttpServletRequest).
     */
    private void loadConfigurationMap() {
        try (InputStream inputStream = getClass().getResourceAsStream(URL_CONFIGURATIONS_FILE_LOCATION)) {
            configurationMap = new LinkedHashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            List<AuthorizationConfig> inputConfigurations = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
            for (AuthorizationConfig config : inputConfigurations) {
                String accessRoles = StringUtils.collectionToDelimitedString(config.getRoles(), ",");
                List<ConfigAttribute> configAttributes = SecurityConfig.createListFromCommaDelimitedString(accessRoles);
                if (ObjectUtils.isEmpty(config.getMethods())) {
                    configurationMap.put(new AntPathRequestMatcher(config.getUrl(), null, false), configAttributes);
                } else {
                    for (String method : config.getMethods()) {
                        configurationMap.put(new AntPathRequestMatcher(config.getUrl(), method, false), configAttributes);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while reading url configurations", e);
        }
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (ObjectUtils.isEmpty(configurationMap)) {
            this.loadConfigurationMap();
        }
        HttpServletRequest httpServletRequest = ((FilterInvocation) object).getRequest();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : configurationMap
                .entrySet()) {
            if (entry.getKey().matches(httpServletRequest)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        if (ObjectUtils.isEmpty(configurationMap)) {
            loadConfigurationMap();
        }
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : configurationMap
                .entrySet()) {
            allAttributes.addAll(entry.getValue());
        }
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
