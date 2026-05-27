package com.ignek.data.initializer.action;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.*;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.*;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
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
public class CardWebContentCreator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {

        Company defaultCompany = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
        long companyId = defaultCompany.getCompanyId();
        long groupId = GroupLocalServiceUtil.getGroup(companyId, "Guest").getGroupId();
        long userId = UserLocalServiceUtil.getRoleUsers(
                RoleLocalServiceUtil.getRole(companyId, RoleConstants.ADMINISTRATOR).getRoleId(), 0, 1).get(0).getUserId();

        long journalArticleClassNameId = ClassNameLocalServiceUtil.getClassNameId(
                JournalArticle.class.getName());

        DDMStructure structure = null;
        long ddmStructureId = 0L;
        try {
            structure = DDMStructureLocalServiceUtil.getStructure(groupId,journalArticleClassNameId,PROGRAMMING_LANGUAGE_CARD);
            ddmStructureId = structure.getStructureId();
        } catch (Exception e) {
            _log.error("Structure not found: " + PROGRAMMING_LANGUAGE_CARD + e);
            return;
        }

        try {
              DDMTemplateLocalServiceUtil.getDDMTemplateByExternalReferenceCode(PROGRAMMING_LANGUAGE,groupId);
        } catch (Exception e) {
            _log.error("Template not found: " + PROGRAMMING_LANGUAGE , e);
            return;
        }

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setScopeGroupId(groupId);
        serviceContext.setCompanyId(companyId);
        serviceContext.setUserId(userId);

        JournalArticle journalArticle = null;
        try {
            journalArticle = JournalArticleLocalServiceUtil.fetchLatestArticleByExternalReferenceCode(groupId,WEB_CONTENT_ID);

            if (Validator.isNotNull(journalArticle)) {
                _log.info("Article already exists. Skipping creation.");
                return;
            }
        } catch (Exception e) {
            _log.info("No web content found. Adding new one...........");
        }

        long imageFileEntryId = 0L;
        String imageTitle = "Liferay.png";
        try {
            FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, imageTitle);
            if (fileEntry != null) {
                imageFileEntryId = fileEntry.getFileEntryId();
            }
        } catch (Exception e) {
            _log.error("Could not find the image asset named: " + imageTitle, e);
        }

        DDMForm ddmForm = structure.getDDMForm();
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.setAvailableLocales(java.util.Set.of(Locale.US));
        ddmFormValues.setDefaultLocale(Locale.US);

        DDMFormFieldValue nameField = new DDMFormFieldValue();
        nameField.setName("programmingname721188");
        Value nameValue = new LocalizedValue(Locale.US);
        nameValue.addString(Locale.US, "Liferay");
        nameField.setValue(nameValue);
        ddmFormValues.addDDMFormFieldValue(nameField);

        DDMFormFieldValue imageField = new DDMFormFieldValue();
        imageField.setName("ProgrammingImage8399982");
        Value imageValue = new LocalizedValue(Locale.US);

        JSONObject imageJson = JSONFactoryUtil.createJSONObject();
        imageJson.put("groupId", String.valueOf(groupId));
        imageJson.put("id", String.valueOf(imageFileEntryId));
        imageJson.put("fileEntryId", String.valueOf(imageFileEntryId));
        imageJson.put("name", "Liferay.png");
        imageJson.put("title", "Liferay.png");
        imageJson.put("type", "document");

        String safeImagePayload = imageJson.toString();
        imageValue.addString(Locale.US, safeImagePayload);
        imageField.setValue(imageValue);
        ddmFormValues.addDDMFormFieldValue(imageField);

        DDMFormFieldValue descField = new DDMFormFieldValue();
        descField.setName("description47150899");
        Value descValue = new LocalizedValue(Locale.US);
        descValue.addString(Locale.US, "Liferay is an open-source enterprise software platform used to build, manage, and scale digital experiences like websites, intranets, and customer.");
        descField.setValue(descValue);
        ddmFormValues.addDDMFormFieldValue(descField);

        JournalConverter journalConverter = FrameworkUtil.getBundle(CardWebContentCreator.class)
                .getBundleContext()
                .getService(FrameworkUtil.getBundle(CardWebContentCreator.class).getBundleContext().getServiceReference(JournalConverter.class));

        DDMFormValuesToFieldsConverter valuesToFieldsConverter = FrameworkUtil.getBundle(CardWebContentCreator.class)
                .getBundleContext()
                .getService(FrameworkUtil.getBundle(CardWebContentCreator.class).getBundleContext().getServiceReference(DDMFormValuesToFieldsConverter.class));

        if (journalConverter == null || valuesToFieldsConverter == null) {
            _log.error("Unable to obtain standard DDM conversion services from the OSGi runtime container.");
            return;
        }

        Fields ddmFields = valuesToFieldsConverter.convert(structure, ddmFormValues);
        String xmlContentString = journalConverter.getContent(structure, ddmFields, groupId);

        Map<Locale, String> titleMap = new HashMap<>();
        titleMap.put(Locale.US, "Liferay web content");

        Map<Locale, String> descriptionMap = new HashMap<>();
        descriptionMap.put(Locale.US, "Object profile generated programmatically via deployment services initializer component.");

        Map<Locale, String> contentMap = new HashMap<>();
        contentMap.put(Locale.US, xmlContentString);

        journalArticle = JournalArticleLocalServiceUtil.addArticle(WEB_CONTENT_ID, userId,groupId,0L, titleMap, descriptionMap, xmlContentString, ddmStructureId, StringPool.BLANK, serviceContext);

        _log.info("Successfully published programmatic Web Content Article ");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
    private static final String PROGRAMMING_LANGUAGE_CARD = "PROGRAMMING_LANGUAGE_CARD";
    private static final String PROGRAMMING_LANGUAGE = "PROGRAMMING_LANGUAGE";
    private static final String WEB_CONTENT_ID = "PROGRAMMING_LANGUAGE_ARTICLES";

    private static final Log _log = LogFactoryUtil.getLog(CardWebContentCreator.class);
}
