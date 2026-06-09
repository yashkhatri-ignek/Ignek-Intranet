package com.ignek.data.initializer.action;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
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
public class TemplateCreator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {

        Company defaultCompany = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
        long companyId = defaultCompany.getCompanyId();
        long groupId = GroupLocalServiceUtil.getGroup(companyId, "Guest").getGroupId();
        long userId = UserLocalServiceUtil.getRoleUsers(
                RoleLocalServiceUtil.getRole(companyId, RoleConstants.ADMINISTRATOR).getRoleId(), 0, 1).get(0).getUserId();

        String script = getFileContent.getFileContents("templates/programming_language.ftl");

        String structureClassName = DDMStructure.class.getName();

        long structureClassNameId = ClassNameLocalServiceUtil.getClassNameId(structureClassName);
        long journalArticleClassNameId = ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class.getName());
        long ddmStructureClassNameId = ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class.getName());

        DDMStructure structure = null;
        try {
            structure = DDMStructureLocalServiceUtil.getStructure(groupId, ddmStructureClassNameId, PROGRAMMING_LANGUAGE_CARD);
        } catch (Exception e) {
            _log.error("Structure not found: " + PROGRAMMING_LANGUAGE_CARD, e);
            return;
        }

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setScopeGroupId(groupId);
        serviceContext.setUserId(userId);

        DDMTemplate programmingTemplate = null;
        try {
            programmingTemplate = DDMTemplateLocalServiceUtil.getDDMTemplateByExternalReferenceCode(PROGRAMMING_LANGUAGE, groupId);
        } catch (NoSuchTemplateException e) {
            _log.info("No template found Adding new one");
        }

        if (Validator.isNotNull(programmingTemplate)) {
            try {
                DDMTemplateLocalServiceUtil.updateTemplate(userId, programmingTemplate.getTemplateId(), structure.getStructureId(), programmingTemplate.getNameMap(), programmingTemplate.getDescriptionMap(), programmingTemplate.getType(), programmingTemplate.getMode(), programmingTemplate.getLanguage(), script, false, serviceContext);
            } catch (Exception e) {
                _log.error("Error while updating template");
            }
        } else {
            try {
                Map<Locale, String> nameMap = new HashMap<>();
                nameMap.put(Locale.US, PROGRAMMING_LANGUAGE);
                Map<Locale, String> descriptionMap = new HashMap<>();
                descriptionMap.put(Locale.US, PROGRAMMING_LANGUAGE);

                DDMTemplateLocalServiceUtil.addTemplate(PROGRAMMING_LANGUAGE, userId, groupId, structureClassNameId, structure.getStructureId(), journalArticleClassNameId, nameMap, descriptionMap, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, StringPool.BLANK, "ftl", script, serviceContext);
            } catch (Exception e) {
                _log.error("Error while adding a new template" + e);
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    private static final Log _log = LogFactoryUtil.getLog(TemplateCreator.class);
    private static final String PROGRAMMING_LANGUAGE = "PROGRAMMING LANGUAGE TEMPLATE";
    private static final String PROGRAMMING_LANGUAGE_CARD = "PROGRAMMING_LANGUAGE_CARD";
    GetFileContent getFileContent = new GetFileContent();
}
