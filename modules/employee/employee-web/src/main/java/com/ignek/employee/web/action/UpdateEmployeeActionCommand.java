package com.ignek.employee.web.action;

import com.ignek.employee.model.Employee;
import com.ignek.employee.service.EmployeeLocalService;
import com.ignek.employee.web.constants.EmployeeConstants;
import com.ignek.employee.web.constants.EmployeePortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

@Component(
        property = {
                "javax.portlet.name=" + EmployeePortletKeys.EMPLOYEE,
                "mvc.command.name=/employee/update"
        },
        service = MVCActionCommand.class
)
public class UpdateEmployeeActionCommand extends BaseMVCActionCommand {

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long employeeId = ParamUtil.getLong(actionRequest, EmployeeConstants.EMPLOYEEID, GetterUtil.DEFAULT_LONG);
        String firstName = ParamUtil.getString(actionRequest, EmployeeConstants.FIRSTNAME, GetterUtil.DEFAULT_STRING);
        String lastName = ParamUtil.getString(actionRequest, EmployeeConstants.LASTNAME, GetterUtil.DEFAULT_STRING);
        String designation = ParamUtil.getString(actionRequest, EmployeeConstants.DESIGNATION, GetterUtil.DEFAULT_STRING);
        String emailAddress = ParamUtil.getString(actionRequest, EmployeeConstants.EMAILADDRESS, GetterUtil.DEFAULT_STRING);
        String phoneNumber = ParamUtil.getString(actionRequest, EmployeeConstants.PHONENUMBER, GetterUtil.DEFAULT_STRING);
        String addressLine1 = ParamUtil.getString(actionRequest, EmployeeConstants.ADDRESSLINE1, GetterUtil.DEFAULT_STRING);
        String addressLine2 = ParamUtil.getString(actionRequest, EmployeeConstants.ADDRESSLINE2, GetterUtil.DEFAULT_STRING);
        String city = ParamUtil.getString(actionRequest, EmployeeConstants.CITY, GetterUtil.DEFAULT_STRING);
        String zipCode = ParamUtil.getString(actionRequest, EmployeeConstants.ZIPCODE, GetterUtil.DEFAULT_STRING);

        Employee employee = null;

        try {
            employee = _employeeLocalService.getEmployee(employeeId);
        } catch (Exception e) {
            _log.error(e.getCause(), e);
        }

        if (Validator.isNotNull(employee)) {
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setDesignation(designation);
            employee.setEmailAddress(emailAddress);
            employee.setPhoneNumber(phoneNumber);
            employee.setAddressLine1(addressLine1);
            employee.setAddressLine2(addressLine2);
            employee.setCity(city);
            employee.setZipCode(zipCode);

            ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);

            _employeeLocalService.updateEmployee(employeeId, firstName, lastName, emailAddress, phoneNumber, addressLine1, addressLine2, city, zipCode, designation, serviceContext);

            _log.info("Employee updated successfully! ID: " + employeeId);
        }
    }

    @Reference
    private EmployeeLocalService _employeeLocalService;

    private static final Log _log = LogFactoryUtil.getLog(UpdateEmployeeActionCommand.class);
}
