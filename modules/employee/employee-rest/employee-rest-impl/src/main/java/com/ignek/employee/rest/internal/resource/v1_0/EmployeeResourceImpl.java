package com.ignek.employee.rest.internal.resource.v1_0;

import com.ignek.employee.rest.dto.v1_0.Employee;
import com.ignek.employee.rest.resource.v1_0.EmployeeResource;

import com.ignek.employee.service.EmployeeLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.vulcan.pagination.Page;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.util.ArrayList;
import java.util.List;

@Component(
        properties = "OSGI-INF/liferay/rest/v1_0/employee.properties",
        scope = ServiceScope.PROTOTYPE, service = EmployeeResource.class
)
public class EmployeeResourceImpl extends BaseEmployeeResourceImpl {


    @Override
    public Page<Employee> getEmployees() throws Exception {
        return Page.of(_getEmployeeList());
    }

    @Override
    public Employee postEmployee(Employee employee) throws Exception {

        com.ignek.employee.model.Employee addEmployee = null;

        try {
            ServiceContext serviceContext = new ServiceContext();
            serviceContext.setCompanyId(contextCompany.getCompanyId());
            serviceContext.setUserId(contextUser.getUserId());

            addEmployee = _employeeLocalService.addEmployee(
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmailAddress(),
                    employee.getPhoneNumber(),
                    employee.getAddressLine1(),
                    employee.getAddressLine2(),
                    employee.getCity(),
                    employee.getZipCode(),
                    employee.getDesignation(),
                    serviceContext);

            _log.info("Employee added successfully.........");

        } catch (Exception e) {
            _log.error("ERROR: " + e.getMessage(), e);
        }
        return _toEmployee(addEmployee);
    }

    @Override
    public Employee putEmployee(Long employeeId, Employee employee) throws Exception {

        com.ignek.employee.model.Employee putEmployee = null;
        ServiceContext serviceContext = new ServiceContext();

        try {
            putEmployee = _employeeLocalService.updateEmployee(
                    employeeId,
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmailAddress(),
                    employee.getPhoneNumber(),
                    employee.getAddressLine1(),
                    employee.getAddressLine2(),
                    employee.getCity(),
                    employee.getZipCode(),
                    employee.getDesignation(), serviceContext
            );

        } catch (Exception e) {
            _log.error("ERROR: " + e.getMessage(), e);
        }
        return _toEmployee(putEmployee);
    }

    @Override
    public Employee deleteEmployee(Long employeeId) throws Exception {

        com.ignek.employee.model.Employee deleteEmployee = null;

        try {
            deleteEmployee = _employeeLocalService.deleteEmployee(employeeId);
        } catch (Exception e) {
            _log.error("ERROR: " + e.getMessage(), e);
        }
        return _toEmployee(deleteEmployee);
    }

    @Override
    public Employee getEmployee(Long employeeId) throws Exception {

        com.ignek.employee.model.Employee getEmployee = null;

        try {
            getEmployee = _employeeLocalService.getEmployee(employeeId);

        } catch (Exception e) {
            _log.error("ERROR: " + e.getMessage(), e);
        }
        return _toEmployee(getEmployee);
    }

    private List<Employee> _getEmployeeList() {

        List<com.ignek.employee.model.Employee> employees;

        employees = _employeeLocalService.getEmployees(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

        List<Employee> employeeList = new ArrayList<>();

        for (com.ignek.employee.model.Employee employee : employees) {
            employeeList.add(_toEmployee(employee));
        }
        return employeeList;
    }


    private Employee _toEmployee(com.ignek.employee.model.Employee employeeFromModel) {
        Employee employee = new Employee();

        employee.setEmployeeId(employeeFromModel.getEmployeeId());
        employee.setFirstName(employeeFromModel.getFirstName());
        employee.setLastName(employeeFromModel.getLastName());
        employee.setEmailAddress(employeeFromModel.getEmailAddress());
        employee.setPhoneNumber(employeeFromModel.getPhoneNumber());
        employee.setAddressLine1(employeeFromModel.getAddressLine1());
        employee.setAddressLine2(employeeFromModel.getAddressLine2());
        employee.setCity(employeeFromModel.getCity());
        employee.setZipCode(employeeFromModel.getZipCode());
        employee.setDesignation(employeeFromModel.getDesignation());

        return employee;
    }

    @Reference
    private EmployeeLocalService _employeeLocalService;

    private static final Log _log = LogFactoryUtil.getLog(EmployeeResourceImpl.class);
}