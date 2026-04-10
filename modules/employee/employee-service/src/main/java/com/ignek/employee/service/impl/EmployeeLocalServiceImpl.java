/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.ignek.employee.service.impl;

import com.ignek.employee.model.Employee;
import com.ignek.employee.service.EmployeeLocalService;
import com.ignek.employee.service.base.EmployeeLocalServiceBaseImpl;

import com.liferay.portal.aop.AopService;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Locale;

@Component(
	property = "model.class.name=com.ignek.employee.model.Employee",
	service = AopService.class
)
public class EmployeeLocalServiceImpl extends EmployeeLocalServiceBaseImpl {

	public Employee addEmployee(
			String firstName, String lastName, String emailAddress, String phoneNumber,
			String addressLine1, String addressLine2, String city, String zipCode,
			String designation, ServiceContext serviceContext)
			throws PortalException {

		User user = UserLocalServiceUtil.addUser(serviceContext.getUserId(), serviceContext.getCompanyId(), true, null, null, true, null, emailAddress, Locale.getDefault(), firstName, null, lastName, 0, 0, true, 1, 1, 1970, designation, 0, null, null, null, null, false, serviceContext);

		long employeeId = counterLocalService.increment(Employee.class.getName());
		Employee employee = createEmployee(employeeId);

		employee.setUserId(user.getUserId());
		employee.setUserName(user.getFullName());

		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setEmailAddress(emailAddress);
		employee.setPhoneNumber(phoneNumber);
		employee.setAddressLine1(addressLine1);
		employee.setAddressLine2(addressLine2);
		employee.setCity(city);
		employee.setZipCode(zipCode);
		employee.setDesignation(designation);

		return addEmployee(employee);
	}

	public Employee updateEmployee(
			long employeeId, String firstName, String lastName, String emailAddress,
			String phoneNumber, String addressLine1, String addressLine2, String city,
			String zipCode, String designation, ServiceContext serviceContext)
			throws PortalException {

		Employee employee = getEmployee(employeeId);

		User user = UserLocalServiceUtil.getUser(employee.getUserId());

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmailAddress(emailAddress);
		user.setJobTitle(designation);

		UserLocalServiceUtil.updateUser(user);

		employee.setUserName(user.getFullName());
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setEmailAddress(emailAddress);
		employee.setPhoneNumber(phoneNumber);
		employee.setAddressLine1(addressLine1);
		employee.setAddressLine2(addressLine2);
		employee.setCity(city);
		employee.setZipCode(zipCode);
		employee.setDesignation(designation);

		return updateEmployee(employee);
	}

	public Employee deleteEmployee(long employeeId) throws PortalException {

		Employee employee = getEmployee(employeeId);

		UserLocalServiceUtil.deleteUser(employee.getUserId());

		return deleteEmployee(employee);
	}
}