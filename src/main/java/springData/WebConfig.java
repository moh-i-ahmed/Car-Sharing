package springData;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

   public void addViewControllers(final ViewControllerRegistry registry) {
      registry.addViewController("/").setViewName("/login");
      registry.addViewController("/error").setViewName("404");
      registry.addViewController("/login").setViewName("login");
      registry.addViewController("/dashboard").setViewName("user/dashboard");
      registry.addViewController("/help").setViewName("/help");
      registry.addViewController("/404").setViewName("404");
      registry.addViewController("/forgot-password").setViewName("forgot-password");
   }

   @Override
   public void addResourceHandlers(final ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/css/**", "/vendor/**", "/img/**", "/js/**")
              .addResourceLocations(
                 "classpath:/static/css/",
                 "classpath:/static/vendor/",
                 "classpath:/static/img/",
                 "classpath:/static/js/");
   }

   @Bean
   public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
      final SimpleMappingExceptionResolver b = new SimpleMappingExceptionResolver();
      final Properties mappings = new Properties();
      mappings.put("springData.controller.SpringException", "ExceptionPage");
      mappings.put("defaultErrorView", "404");
      b.setExceptionMappings(mappings);
      return b;
   }

   /*
   @Bean
   public ClassLoaderTemplateResolver templateResolver() {
       ClassLoaderTemplateResolver secondaryTemplateResolver = new ClassLoaderTemplateResolver();
       secondaryTemplateResolver.setPrefix("templates/");
       secondaryTemplateResolver.setSuffix(".html");
       secondaryTemplateResolver.setTemplateMode(TemplateMode.HTML);
       secondaryTemplateResolver.setCharacterEncoding("UTF-8");
       secondaryTemplateResolver.setOrder(1);
       secondaryTemplateResolver.setCheckExistence(true);
            
       return secondaryTemplateResolver;
   }*/

   @Primary
   @Bean
   public FreeMarkerConfigurationFactoryBean factoryBean() {
      final FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
      bean.setTemplateLoaderPath("classpath:/templates/email/");
      return bean;
   }

}
// WebConfig
