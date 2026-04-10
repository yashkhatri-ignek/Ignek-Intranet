package com.ignek.employee.web.action;

import com.ignek.employee.service.EmployeeLocalService;
import com.ignek.employee.web.constants.EmployeeConstants;
import com.ignek.employee.web.constants.EmployeePortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

@Component(
        property = {
                "javax.portlet.name=" + EmployeePortletKeys.EMPLOYEE,
                "mvc.command.name=/employee/delete"
        },
        service = MVCActionCommand.class
)
public class DeleteEmployeeActionCommand extends BaseMVCActionCommand {

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long employeeId = ParamUtil.getLong(actionRequest, EmployeeConstants.EMPLOYEEID, GetterUtil.DEFAULT_LONG);
        try {
            _employeeLocalService.deleteEmployee(employeeId);
        } catch (Exception e) {
            _log.error(e.getMessage(), e);
        }
    }
    @Reference
    private EmployeeLocalService _employeeLocalService;
    private static final Log _log = LogFactoryUtil.getLog(UpdateEmployeeActionCommand.class);
}
