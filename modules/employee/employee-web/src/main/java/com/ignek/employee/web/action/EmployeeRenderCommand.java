package com.ignek.employee.web.action;

import com.ignek.employee.model.Employee;
import com.ignek.employee.service.EmployeeLocalService;
import com.ignek.employee.web.constants.EmployeeConstants;
import com.ignek.employee.web.constants.EmployeePortletKeys;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.List;

@Component(
        property = {
                "javax.portlet.name=" + EmployeePortletKeys.EMPLOYEE,
                "mvc.command.name=/"
        },
        service = MVCRenderCommand.class
)
public class EmployeeRenderCommand implements MVCRenderCommand {

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        int currentPageNo  = ParamUtil.getInteger(renderRequest, "cur", 1);
        int delta = ParamUtil.getInteger(renderRequest, "delta", 2);

        int start = (currentPageNo - 1) * delta;
        int end = start + delta;

        List<Employee> employeeList = _employeeLocalService.getEmployees(start, end);
        int total = _employeeLocalService.getEmployeesCount();

        renderRequest.setAttribute(EmployeeConstants.EMPLOYEELIST, employeeList);
        renderRequest.setAttribute("total", total);
        renderRequest.setAttribute("delta", delta);
        renderRequest.setAttribute("currentPageNo",currentPageNo);

//        List<Employee> employeeList = _employeeLocalService.getEmployees(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
//        renderRequest.setAttribute(EmployeeConstants.EMPLOYEELIST, employeeList);
        return "/view.jsp";
    }

    @Reference
    private EmployeeLocalService _employeeLocalService;
}
