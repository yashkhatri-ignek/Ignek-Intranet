package com.ignek.data.initializer.action;

import com.liferay.layout.page.template.exception.NoSuchPageTemplateEntryException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.*;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
public class LayoutTemplateCreator implements BundleActivator {

    private static final String PROGRAMMING_LANGUAGE_MASTER = "PROGRAMMING_LANGUAGE_MASTER";
    private static final String MASTER_PAGE_NAME = "Programming Language Master";
    private static final int TYPE_MASTER_LAYOUT = 3;

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

        LayoutPageTemplateEntry masterPageEntry = null;

        try {
            masterPageEntry = LayoutPageTemplateEntryLocalServiceUtil
                    .getLayoutPageTemplateEntryByExternalReferenceCode(
                            PROGRAMMING_LANGUAGE_MASTER, groupId);
        } catch (NoSuchPageTemplateEntryException e) {
            _log.info("No master page found. Adding new one.");
        }

        if (Validator.isNotNull(masterPageEntry)) {
            try {
                LayoutPageTemplateEntryLocalServiceUtil.updateLayoutPageTemplateEntry(
                        masterPageEntry.getLayoutPageTemplateEntryId(), MASTER_PAGE_NAME);
                _log.info("Master page updated: " + PROGRAMMING_LANGUAGE_MASTER);
            } catch (Exception e) {
                _log.error("Error while updating master page", e);
            }
        } else {
            try {
                LayoutPageTemplateEntryLocalServiceUtil.addLayoutPageTemplateEntry(
                        PROGRAMMING_LANGUAGE_MASTER,
                        userId,
                        groupId,
                        0L,
                        0L,
                        0L,
                        MASTER_PAGE_NAME,
                        TYPE_MASTER_LAYOUT,
                        0L,
                        false,
                        0L,
                        0L,
                        0L,
                        WorkflowConstants.STATUS_APPROVED,
                        serviceContext
                );
                _log.info("Master page created: " + PROGRAMMING_LANGUAGE_MASTER);
            } catch (Exception e) {
                _log.error("Error while adding master page", e);
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    private static final Log _log = LogFactoryUtil.getLog(LayoutTemplateCreator.class);
}
