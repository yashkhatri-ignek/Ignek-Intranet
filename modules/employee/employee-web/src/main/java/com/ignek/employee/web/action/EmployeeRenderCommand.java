package com.ignek.employee.web.action;

import com.ignek.employee.model.Employee;
import com.ignek.employee.service.EmployeeLocalService;
import com.ignek.employee.web.util.RoleUtil;
import com.ignek.employee.web.constants.EmployeeConstants;
import com.ignek.employee.web.constants.EmployeePortletKeys;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

        ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

        boolean isHRorAdmin = RoleUtil.isHROrAdmin(themeDisplay);

        int currentPageNo  = ParamUtil.getInteger(renderRequest, SearchContainer.DEFAULT_CUR_PARAM, 1);
        int delta = ParamUtil.getInteger(renderRequest, SearchContainer.DEFAULT_DELTA_PARAM, 2);

        int start = (currentPageNo - 1) * delta;
        int end = start + delta;

//        List<Employee> employeeList = _employeeLocalService.getEmployees(start, end);
//        int total = _employeeLocalService.getEmployeesCount();

//        String keywords = ParamUtil.getString(renderRequest, "keywords", "");

        List<Employee> employeeList = new ArrayList<>();
        int total = 0;

        try {

            HttpServletRequest httpRequest = PortalUtil
                    .getHttpServletRequest(renderRequest);

            SearchContext searchContext = SearchContextFactory
                    .getInstance(httpRequest);

            searchContext.setCompanyId(themeDisplay.getCompanyId());
            searchContext.setStart(start);
            searchContext.setEnd(end);

            searchContext.setGroupIds(new long[]{});

            searchContext.setEntryClassNames(
                    new String[]{Employee.class.getName()});

            // Get Employee Indexer
            Indexer<Employee> indexer = IndexerRegistryUtil
                    .nullSafeGetIndexer(Employee.class);

            // Search Elasticsearch
            Hits hits = indexer.search(searchContext);

            // Fetch Employee objects using IDs from Elasticsearch docs
            for (Document document : hits.getDocs()) {

                long employeeId = GetterUtil.getLong(
                        document.get(Field.ENTRY_CLASS_PK));

                try {
                    Employee employee = _employeeLocalService
                            .getEmployee(employeeId);
                    employeeList.add(employee);

                } catch (PortalException e) {
                    _log.error("Employee not found for ID: " + employeeId);
                }
            }

        } catch  (Exception e) {
            _log.error("Elasticsearch search error: " + e.getMessage());
        }

        renderRequest.setAttribute(EmployeeConstants.EMPLOYEELIST, employeeList);

        renderRequest.setAttribute("total", total);
        renderRequest.setAttribute("delta", delta);
        renderRequest.setAttribute("isHRorAdmin",isHRorAdmin);

        return "/view.jsp";
    }

        @Reference
        private EmployeeLocalService _employeeLocalService;

        private static final Log _log = LogFactoryUtil.getLog(EmployeeRenderCommand.class);
    }
