package com.ignek.data.initializer.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.*;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
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
public class ADTCreator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {

        Company defaultCompany = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
        long companyId = defaultCompany.getCompanyId();
        long groupId = GroupLocalServiceUtil.getGroup(companyId, "Guest").getGroupId();
        long userId = UserLocalServiceUtil.getRoleUsers(RoleLocalServiceUtil.getRole(companyId, RoleConstants.ADMINISTRATOR).getRoleId(), 0, 1).get(0).getUserId();

        Long adtClassNameId = ClassNameLocalServiceUtil.getClassNameId(AssetEntry.class.getName());
        Long resourceClassNameId = ClassNameLocalServiceUtil.getClassNameId(PortletDisplayTemplate.class.getName());

        String script = getFileContent.getFileContents("adt/programming_language_adt.ftl");

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setScopeGroupId(groupId);
        serviceContext.setCompanyId(companyId);
        serviceContext.setUserId(userId);

        DDMTemplate adtTemplate = null;

        try {
            adtTemplate = DDMTemplateLocalServiceUtil.getDDMTemplateByExternalReferenceCode(PROGRAMMING_LANGUAGE_ADT, groupId);
        } catch (NoSuchTemplateException e) {
            _log.info("No ADT template found Adding new one" + e);
        }

        if (Validator.isNotNull(adtTemplate)) {
            try {
                DDMTemplateLocalServiceUtil.updateTemplate(userId, adtTemplate.getTemplateId(), 0L, adtTemplate.getNameMap(), adtTemplate.getDescriptionMap(), adtTemplate.getType(), adtTemplate.getMode(), adtTemplate.getLanguage(), script, false, serviceContext);
                _log.info("ADTTemplate updated: " + PROGRAMMING_LANGUAGE_ADT);
            } catch (Exception e) {
                _log.error("Error while updating template");
            }
        } else {
            try {
                Map<Locale, String> nameMap = new HashMap<Locale, String>();
                nameMap.put(Locale.US, PROGRAMMING_LANGUAGE_ADT);
                Map<Locale, String> descriptionMap = new HashMap<Locale, String>();
                descriptionMap.put(Locale.US, PROGRAMMING_LANGUAGE_ADT);

                DDMTemplateLocalServiceUtil.addTemplate(PROGRAMMING_LANGUAGE_ADT, userId, groupId, adtClassNameId, 0L, resourceClassNameId, nameMap, descriptionMap, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, StringPool.BLANK, "ftl", script, serviceContext);
            } catch (Exception e) {
                _log.error("Error while adding a new ADT template" + e);
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    private static final Log _log = LogFactoryUtil.getLog(ADTCreator.class);
    private static final String PROGRAMMING_LANGUAGE_ADT = "PROGRAMMING_LANGUAGE_ADT";
    GetFileContent getFileContent = new GetFileContent();
}
