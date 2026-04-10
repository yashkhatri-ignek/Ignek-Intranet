package com.ignek.entity.counts.preferences.action;

import com.ignek.entity.counts.constants.EntityCountsPortletKeys;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

@Component(
        property = {
                "javax.portlet.name=" + EntityCountsPortletKeys.ENTITYCOUNTS_ID,
        },
        service = ConfigurationAction.class
)
public class CountsEntityConfigurationAction extends DefaultConfigurationAction {

    @Override
    public void processAction(
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        String countsOf = ParamUtil.getString(actionRequest, "countsOf");
        setPreference(actionRequest, "countsOf", countsOf);

        super.processAction(portletConfig,actionRequest,actionResponse);
    }
}
