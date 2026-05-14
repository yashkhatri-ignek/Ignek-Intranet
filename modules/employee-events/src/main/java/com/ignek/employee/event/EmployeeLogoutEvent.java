package com.ignek.employee.event;

import com.ignek.employee.event.constants.EmployeeLoginConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component(
        property = {
                "key=logout.events.post"
        },
        service = LifecycleAction.class
)
public class EmployeeLogoutEvent implements LifecycleAction {

    @Override
    public void processLifecycleEvent(LifecycleEvent lifecycleEvent) throws ActionException {

        try {
            HttpServletRequest request = _portal.getOriginalServletRequest(lifecycleEvent.getRequest());
            String ipAddress = request.getRemoteAddr();

            User user = _portal.getUser(request);

            long companyId = user.getCompanyId();
            long userId = user.getUserId();

            ObjectDefinition objectDefinition = _objectDefinitionLocalService
                    .fetchObjectDefinitionByExternalReferenceCode(EmployeeLoginConstants.ACTIVITY, companyId);

            if (Validator.isNotNull(objectDefinition)) {
                ServiceContext serviceContext = new ServiceContext();
                serviceContext.setUserId(userId);
                serviceContext.setRequest(request);
                Map<String, Serializable> values = new HashMap();

                values.put(EmployeeLoginConstants.ACTIVITY_TYPE, EmployeeLoginConstants.LOGOUT);
                values.put(EmployeeLoginConstants.DETAILS, _portal.getUser(request).getEmailAddress());
                values.put(EmployeeLoginConstants.IP_ADDRESS, ipAddress);
                values.put(EmployeeLoginConstants.USER_ID, String.valueOf(userId));

                ObjectEntry objectEntry = _objectEntryLocalService
                        .addObjectEntry(userId, 0L,
                                objectDefinition.getObjectDefinitionId(), values, serviceContext);
            }

        } catch (Exception e) {
            log.info("Error occured while generation activity entry " + e);
        }
    }

    private static final Log log = LogFactoryUtil.getLog(EmployeeLoginEvent.class);

    @Reference
    private ObjectEntryLocalService _objectEntryLocalService;

    @Reference
    private ObjectDefinitionLocalService _objectDefinitionLocalService;

    @Reference
    private GroupLocalService _groupLocalService;

    @Reference
    private Portal _portal;
}

