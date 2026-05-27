package com.ignek.data.initializer.action;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.*;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component(
        immediate = true,
        property = {
                "key=application.startup.events"
        },
        service = BundleActivator.class
)
public class RolesCreator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {

        Company defaultCompany = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
        long companyId = defaultCompany.getCompanyId();
        long groupId = GroupLocalServiceUtil.getGroup(companyId, "Guest").getGroupId();
        long userId = UserLocalServiceUtil.getRoleUsers(
                RoleLocalServiceUtil.getRole(companyId, RoleConstants.ADMINISTRATOR).getRoleId(), 0, 1).get(0).getUserId();

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setScopeGroupId(groupId);
        serviceContext.setCompanyId(companyId);
        serviceContext.setUserId(userId);

        try {
            Role role = RoleLocalServiceUtil.fetchRole(companyId, "Web Content Manager");
            Map<Locale, String> nameMap = new HashMap<>();
            nameMap.put(Locale.US, WEB_CONTENT_MANAGER);
            Map<Locale, String> descriptionMap = new HashMap<>();
            descriptionMap.put(Locale.US, "This role is for web content management");
            if (Validator.isNull(role)) {
                role = RoleLocalServiceUtil.addRole(WEB_CONTENT_MANAGER, userId, Role.class.getName(), 0, WEB_CONTENT_MANAGER,
                        nameMap, descriptionMap, RoleConstants.TYPE_REGULAR, null, serviceContext);
            }
        } catch (Exception e) {
            _log.error("Error while adding role");
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    private static final String WEB_CONTENT_MANAGER = "Web Content Manager";
    private static final Log _log = LogFactoryUtil.getLog(RolesCreator.class);
}
