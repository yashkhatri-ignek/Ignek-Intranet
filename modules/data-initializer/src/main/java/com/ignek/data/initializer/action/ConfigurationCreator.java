package com.ignek.data.initializer.action;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;

@Component(
        immediate = true,
        property = {
                "key=application.startup.events"
        },
        service = BundleActivator.class
)
public class ConfigurationCreator implements BundleActivator{

    @Override
    public void start(BundleContext context) throws Exception {

        new StructureCreator().start(context);
        new TemplateCreator().start(context);
        new ADTCreator().start(context);
        new RolesCreator().start(context);
        new LayoutTemplateCreator().start(context);
        new CardWebContentCreator().start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
