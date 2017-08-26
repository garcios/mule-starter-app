package org.oscar.app;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.context.MuleContextBuilder;
import org.mule.config.DefaultMuleConfiguration;
import org.mule.config.PropertiesMuleConfigurationFactory;
import org.mule.config.builders.ExtensionsManagerConfigurationBuilder;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;



@SpringBootApplication
public class MuleBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MuleBootstrap.class);

    @Autowired
    private ApplicationContext context;
    
    //@Autowired
    //private Environment env;
    
    @Override
    public void run(String... strings) throws Exception {
        DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
        SpringXmlConfigurationBuilder springXmlConfigurationBuilder = null;
        String[] configFiles = new String[] {"mule-config.xml"};
        
        try {
        	List<ConfigurationBuilder> configurationBuilders = new LinkedList<>();
        	
        	// load extensions builder first!
			ExtensionsManagerConfigurationBuilder extensionsManagerConfigurationBuilder = new ExtensionsManagerConfigurationBuilder();
            configurationBuilders.add(extensionsManagerConfigurationBuilder);
			
            StaticApplicationContext staticApplicationContext = new StaticApplicationContext(context);
            
            
            log.info("Loading config - " + ArrayUtils.toString(configFiles));
			
            springXmlConfigurationBuilder = new SpringXmlConfigurationBuilder(configFiles);
            staticApplicationContext.refresh();
            springXmlConfigurationBuilder.setParentContext(staticApplicationContext);
            configurationBuilders.add(springXmlConfigurationBuilder);
            
            DefaultMuleConfiguration muleConfiguration = new PropertiesMuleConfigurationFactory("org.mule.app.config").createConfiguration();
            MuleContextBuilder muleContextBuilder = new DefaultMuleContextBuilder();
            muleContextBuilder.setMuleConfiguration(muleConfiguration);

            MuleContext muleContext = muleContextFactory.createMuleContext(configurationBuilders, muleContextBuilder);
            muleContext.start();
            
            log.info("Started Mule!");
        } catch (Exception e) {
            log.error("Error starting Mule...",e);
        }
    }
    
   

    public static void main(String... args) {
        log.info("Starting SpringApplication...");
        
        SpringApplication app = new SpringApplication(MuleBootstrap.class);
        app.setWebEnvironment(false);
        app.run(args);
        
        log.info("SpringApplication has started...");
    }

}