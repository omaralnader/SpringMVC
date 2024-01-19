package com.mouri.test;

import java.nio.charset.Charset;


import javax.annotation.PostConstruct;

import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.springsecurity.AdapterDeploymentContextFactoryBean;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakSpringConfigResolverWrapper;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.representations.adapters.config.AdapterConfig;
//import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
//import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
//import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.LocaleResolver;
//import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.mouri.test.service.KeycloakService;
import com.mouri.test.service.TokenService;
import com.mouri.test.utils.RoleEnum;

@KeycloakConfiguration
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

	private CustomKeycloakConfigResolver keycloakConfigResolver;
	
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
//		return null;
	}

    @PostConstruct
    public void initkeycloakConfigResolver() throws Exception {
        keycloakConfigResolver = new CustomKeycloakConfigResolver();
        keycloakConfigResolver.initKeycloakResourceFile();
    }
    @Bean
    public AdapterConfig adapterConfig() throws Exception {
        return keycloakConfigResolver.getAdapterConfig();
    }

    @Override
    @Bean
    protected AdapterDeploymentContext adapterDeploymentContext() throws Exception {
        AdapterDeploymentContextFactoryBean factoryBean;
        factoryBean = new AdapterDeploymentContextFactoryBean(
            new KeycloakSpringConfigResolverWrapper(keycloakConfigResolver));

        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /************************************************************/
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        SimpleAuthorityMapper mapper = new SimpleAuthorityMapper();
        mapper.setConvertToUpperCase(true);
        return mapper;
    }   
    @Override
    protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        final KeycloakAuthenticationProvider provider = super.keycloakAuthenticationProvider();
        provider.setGrantedAuthoritiesMapper(grantedAuthoritiesMapper());
        return provider;
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }
    /************************************************************/
    
    @Override
    @SuppressWarnings("squid:S4502")
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable() //
            .authorizeRequests() //
            .antMatchers("/logoutFromApp/**").permitAll() //
            .antMatchers("/accessInfo/**").permitAll() //
            .antMatchers("/public/**").permitAll() //
            .antMatchers("/audit/**").hasRole(RoleEnum.SYSTEM_ADMIN.name()) //
            .antMatchers("/case/**") //
            .hasAnyRole(RoleEnum.SUPER_USER.name(), RoleEnum.SYSTEM_ADMIN.name(), RoleEnum.HQ_OFFICER.name()) //
            .antMatchers("/processResults/**") //
            .hasAnyRole(RoleEnum.SUPER_USER.name(), RoleEnum.HQ_OFFICER.name()) //
            .antMatchers("/setting/**").hasAnyRole(RoleEnum.SUPER_USER.name(), RoleEnum.SYSTEM_ADMIN.name()) //
            .antMatchers("/submission/**") //
            .hasAnyRole(RoleEnum.SUPER_USER.name(), RoleEnum.SYSTEM_ADMIN.name(), RoleEnum.HQ_OFFICER.name()) //
            .antMatchers("/user/**").hasAnyRole(RoleEnum.SUPER_USER.name(), RoleEnum.SYSTEM_ADMIN.name()) //
            .antMatchers("/hiringManager/**")
            .hasAnyRole(RoleEnum.HIRING_MANAGER.name(), RoleEnum.SUPER_USER.name(), RoleEnum.SYSTEM_ADMIN.name(),
                RoleEnum.HQ_OFFICER.name()) //
            .anyRequest().fullyAuthenticated() //
            .and() //
            .exceptionHandling().accessDeniedHandler(null);
    }

    /************************************************************/
    @Bean
    public RestTemplate restTemplate() {

        // We need proxy when running locally in Eclipse, and otherwise in OCP, we don't need proxy.
        RestTemplate restTemplate;

//        log.info("Creating restTemplate without proxy...");
        restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        return restTemplate;
    }
    @Bean
    public TokenService tokenService() throws Exception {
        return new TokenService(keycloakConfigResolver.getAdapterConfig(), restTemplate());
    }
    @Bean
    public KeycloakService keycloakService() throws Exception {
        return new KeycloakService(keycloakConfigResolver.getAdapterConfig(), tokenService(), restTemplate(), "PSSS");
    }
    /************************************************************/

    
}
