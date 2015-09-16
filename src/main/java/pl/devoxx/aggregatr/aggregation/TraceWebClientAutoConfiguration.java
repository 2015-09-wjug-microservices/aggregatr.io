package pl.devoxx.aggregatr.aggregation;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.instrument.web.client.TraceRestTemplateInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class TraceWebClientAutoConfiguration {

	@Configuration
	protected static class TraceInterceptorConfiguration {

		@Bean
		public TraceRestTemplateInterceptor traceRestTemplateInterceptor() {
			return new TraceRestTemplateInterceptor();
		}

		@Autowired(required = false)
		private Collection<RestOperations> restTemplates;

		@PostConstruct
		public void init() {
			if (this.restTemplates != null) {
				for (RestOperations restOperation : this.restTemplates) {
					RestTemplate restTemplate = getTargetObject(restOperation, RestTemplate.class);
					List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
					interceptors.add(traceRestTemplateInterceptor());
					restTemplate.setInterceptors(interceptors);
				}
			}
		}

		@SuppressWarnings({"unchecked"})
		private <T> T getTargetObject(Object proxy, Class<T> targetClass) {
			if (AopUtils.isJdkDynamicProxy(proxy)) {
				try {
					return (T) ((Advised)proxy).getTargetSource().getTarget();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
			}
		}
	}
}
