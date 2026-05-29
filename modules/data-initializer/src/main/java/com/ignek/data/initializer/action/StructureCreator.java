package com.ignek.data.initializer.action;

import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
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
import org.osgi.framework.ServiceReference;
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
public class StructureCreator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {

        try {
            Company defaultCompany = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
            long companyId = defaultCompany.getCompanyId();
            long groupId = GroupLocalServiceUtil.getGroup(companyId, "Guest").getGroupId();
            long userId = UserLocalServiceUtil.getRoleUsers(RoleLocalServiceUtil.getRole(companyId, RoleConstants.ADMINISTRATOR).getRoleId(), 0, 1).get(0).getUserId();

            long classNameId = ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class.getName());

            String content = getFileContent.getFileContents("structure/programming_language_card.json");

            ServiceReference<DDMFormDeserializer> serviceReference = context.getServiceReference(DDMFormDeserializer.class);
            DDMFormDeserializer deserializer = context.getService(serviceReference);

            DDMFormDeserializerDeserializeRequest deserializeRequest = DDMFormDeserializerDeserializeRequest.Builder
                    .newBuilder(content)
                    .build();

            DDMForm ddmForm = deserializer.deserialize(deserializeRequest).getDDMForm();

            if (ddmForm == null || ddmForm.getDDMFormFields().isEmpty()) {
                _log.error("DDMForm has 0 fields - Check JSON schema definitions.");
                return;
            }

            DDMFormLayout ddmFormLayout = DDMUtil.getDefaultDDMFormLayout(ddmForm);

            ServiceContext serviceContext = new ServiceContext();
            serviceContext.setScopeGroupId(groupId);
            serviceContext.setCompanyId(companyId);
            serviceContext.setUserId(userId);

            DDMStructure structure = null;

            try {
                structure = DDMStructureLocalServiceUtil.getStructure(groupId, classNameId, PROGRAMMING_LANGUAGE_CARD);
            } catch (NoSuchStructureException e) {
                _log.info("No structure found. Adding new one.");
            }

            if (Validator.isNotNull(structure)) {
                try {
                    DDMStructureLocalServiceUtil.updateStructure(userId, structure.getStructureId(), ddmForm, ddmFormLayout, serviceContext);
                } catch (Exception e) {
                    _log.error("Error while updating structure", e);
                }
            } else {
                try {
                    Map<Locale, String> nameMap = new HashMap<>();
                    nameMap.put(Locale.US, PROGRAMMING_LANGUAGE_CARD);

                    Map<Locale, String> descriptionMap = new HashMap<>();
                    descriptionMap.put(Locale.US, PROGRAMMING_LANGUAGE_CARD);

                    DDMStructureLocalServiceUtil.addStructure(userId, groupId, StringPool.BLANK, classNameId, PROGRAMMING_LANGUAGE_CARD, nameMap, descriptionMap, ddmForm, ddmFormLayout, StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT, serviceContext);

                } catch (Exception e) {
                    _log.error("Error while adding new structure", e);
                }
            }
        } catch (Exception e) {
            _log.error("Error initializing the structure", e);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    private static final Log _log = LogFactoryUtil.getLog(StructureCreator.class);
    private static final String PROGRAMMING_LANGUAGE_CARD = "PROGRAMMING LANG STRUCTURE";
    GetFileContent getFileContent = new GetFileContent();
}
