package com.ignek.employee.event.listener;

import com.ignek.employee.event.EmployeeLoginEvent;
import com.ignek.employee.event.constants.EmployeeLoginConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.*;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component(
        immediate = true,
        service = ModelListener.class
)
public class RoleModelListener extends BaseModelListener<UserGroupRole> {

    @Override
    public void onAfterCreate(UserGroupRole userGroupRole) throws ModelListenerException {
        _createActivityEntry(userGroupRole, EmployeeLoginConstants.ROLE_ASSIGNED);
    }

    @Override
    public void onAfterRemove(UserGroupRole userGroupRole) throws ModelListenerException {
        _createActivityEntry(userGroupRole, EmployeeLoginConstants.ROLE_REMOVED);
    }

    @Override
    public void onAfterUpdate(UserGroupRole userGroupRole, UserGroupRole model) throws ModelListenerException {
        _createActivityEntry(userGroupRole, EmployeeLoginConstants.ROLE_UPDATED);
    }

    public void _createActivityEntry(UserGroupRole userGroupRole, String activityType) {

        String previousPrincipal = PrincipalThreadLocal.getName();

        try {
            long companyId = userGroupRole.getCompanyId();
            long userId = GetterUtil.getLong(PrincipalThreadLocal.getUserId());

            String assignedToEmail = "unknown";
            try {
                User assignedUser = _userLocalService.fetchUserById(
                        userGroupRole.getUserId());
                if (Validator.isNotNull(assignedUser)) {
                    assignedToEmail = assignedUser.getEmailAddress();
                }
            } catch (Exception e) {
                log.warn("Could not fetch user for userId: "
                        + userGroupRole.getUserId());
            }

            String details = assignedToEmail;

            ObjectDefinition objectDefinition = _objectDefinitionLocalService
                    .fetchObjectDefinitionByExternalReferenceCode(
                            EmployeeLoginConstants.ACTIVITY, companyId);

            if (Validator.isNull(objectDefinition)) {
                log.warn("Activity ObjectDefinition not found | ERC: "
                        + EmployeeLoginConstants.ACTIVITY
                        + " | companyId: " + companyId);
                return;
            }

            String ipAddress = _getIpAddress();

            Map<String, Serializable> values = new HashMap<>();
            values.put(EmployeeLoginConstants.ACTIVITY_TYPE, activityType);
            values.put(EmployeeLoginConstants.USER_ID, String.valueOf(userId));
            values.put(EmployeeLoginConstants.DETAILS, details);
            values.put(EmployeeLoginConstants.IP_ADDRESS, ipAddress);

            ObjectEntry objectEntry = _objectEntryLocalService
                    .addObjectEntry(userId, GetterUtil.DEFAULT_LONG,
                            objectDefinition.getObjectDefinitionId(),
                            values, new ServiceContext());

        } catch (Exception e) {
            log.error("Error creating activity entry | type: "
                    + activityType, e);
        } finally {
            PrincipalThreadLocal.setName(previousPrincipal);
        }
    }

    private String _getIpAddress() {

        try {
            ServiceContext serviceContext =
                    ServiceContextThreadLocal.getServiceContext();

            if (Validator.isNotNull(serviceContext)
                    && Validator.isNotNull(serviceContext.getRequest())) {

                HttpServletRequest request =
                        _portal.getOriginalServletRequest(
                                serviceContext.getRequest());

                String ip = request.getRemoteAddr();
                return ip;
            }

        } catch (Exception e) {
            log.warn("Could not resolve IP address: " + e.getMessage());
        }
        return EmployeeLoginConstants.SYSTEM_IP;
    }

    private static final Log log = LogFactoryUtil.getLog(EmployeeLoginEvent.class);

    @Reference
    private ObjectEntryLocalService _objectEntryLocalService;

    @Reference
    private ObjectDefinitionLocalService _objectDefinitionLocalService;

    @Reference
    private Portal _portal;

    @Reference
    private UserLocalService _userLocalService;
}
