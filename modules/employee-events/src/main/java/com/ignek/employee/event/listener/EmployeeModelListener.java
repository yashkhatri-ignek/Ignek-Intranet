package com.ignek.employee.event.listener;

import com.ignek.employee.event.EmployeeLoginEvent;
import com.ignek.employee.event.constants.EmployeeLoginConstants;
import com.ignek.employee.model.Employee;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
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
public class EmployeeModelListener extends BaseModelListener<Employee> {

    @Override
    public void onAfterCreate(Employee employee) throws ModelListenerException {
        _createActivityEntry(employee, EmployeeLoginConstants.ADD, employee.getEmailAddress());
    }

    @Override
    public void onAfterUpdate(Employee originalModel, Employee employee) throws ModelListenerException {
        _createActivityEntry(employee, EmployeeLoginConstants.UPDATE, employee.getEmailAddress());
    }

    @Override
    public void onAfterRemove(Employee employee) throws ModelListenerException {
        _createActivityEntry(employee, EmployeeLoginConstants.DELETE, employee.getEmailAddress());
    }

    private void _createActivityEntry(Employee employee, String activityType, String details) {

        String previousPrincipal = PrincipalThreadLocal.getName();

        try {

            long companyId = employee.getCompanyId();
            long userId = GetterUtil.getLong(PrincipalThreadLocal.getUserId());

            String IpAddress = _getIpAddress();

            ObjectDefinition objectDefinition = _objectDefinitionLocalService
                    .fetchObjectDefinitionByExternalReferenceCode(EmployeeLoginConstants.ACTIVITY, companyId);

            if (Validator.isNotNull(objectDefinition)) {

                Map<String, Serializable> values = new HashMap();
                values.put(EmployeeLoginConstants.ACTIVITY_TYPE, activityType);
                values.put(EmployeeLoginConstants.DETAILS, details);
                values.put(EmployeeLoginConstants.IP_ADDRESS, IpAddress);
                values.put(EmployeeLoginConstants.USER_ID, String.valueOf(userId));

                ObjectEntry objectEntry = _objectEntryLocalService
                        .addObjectEntry(userId, GetterUtil.DEFAULT_LONG,
                                objectDefinition.getObjectDefinitionId(), values, new ServiceContext());
            }

        } catch (Exception e) {
            log.info("Error occured while generation activity entry " + activityType, e);
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
                log.info("Resolved IP Address: " + ip);
                return ip;
            }

        } catch (Exception e) {
            log.warn("Could not resolve IP address: " + e.getMessage());
        }

        log.warn("Falling back to SYSTEM IP");
        return EmployeeLoginConstants.SYSTEM_IP;
    }

    private static final Log log = LogFactoryUtil.getLog(EmployeeLoginEvent.class);

    @Reference
    private ObjectEntryLocalService _objectEntryLocalService;

    @Reference
    private ObjectDefinitionLocalService _objectDefinitionLocalService;

    @Reference
    private Portal _portal;
}