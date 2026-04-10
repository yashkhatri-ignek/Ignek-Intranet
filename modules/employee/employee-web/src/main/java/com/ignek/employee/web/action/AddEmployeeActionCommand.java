package com.ignek.employee.web.action;

import com.ignek.employee.service.EmployeeLocalService;
import com.ignek.employee.web.constants.EmployeeConstants;
import com.ignek.employee.web.constants.EmployeePortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

@Component(
        property = {
                "javax.portlet.name=" + EmployeePortletKeys.EMPLOYEE,
                "mvc.command.name=/employee/add"
        },
        service = MVCActionCommand.class
)
public class AddEmployeeActionCommand extends BaseMVCActionCommand {

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        String firstName = ParamUtil.getString(actionRequest, EmployeeConstants.FIRSTNAME);
        String lastName = ParamUtil.getString(actionRequest,EmployeeConstants.LASTNAME);
        String designation = ParamUtil.getString(actionRequest,EmployeeConstants.DESIGNATION);
        String emailAddress = ParamUtil.getString(actionRequest,EmployeeConstants.EMAILADDRESS);
        String phoneNumber = ParamUtil.getString(actionRequest, EmployeeConstants.PHONENUMBER);
        String addressLine1 = ParamUtil.getString(actionRequest, EmployeeConstants.ADDRESSLINE1);
        String addressLine2 = ParamUtil.getString(actionRequest, EmployeeConstants.ADDRESSLINE2);
        String city = ParamUtil.getString(actionRequest, EmployeeConstants.CITY);
        String zipCode = ParamUtil.getString(actionRequest, EmployeeConstants.ZIPCODE);

        ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);

        _employeeLocalService.addEmployee(firstName, lastName, emailAddress, phoneNumber, addressLine1, addressLine2,city, zipCode, designation, serviceContext);

        _log.info("Employee added successfully!");
    }

    @Reference
    private EmployeeLocalService _employeeLocalService;

    private static final Log _log = LogFactoryUtil.getLog(AddEmployeeActionCommand.class);
}

