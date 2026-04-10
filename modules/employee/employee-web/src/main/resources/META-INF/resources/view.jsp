<%@ include file="/init.jsp" %>

<%  List<Employee> employeeList = (List<Employee>)request.getAttribute("employeeList"); %>

<portlet:renderURL var="addEmployeeRenderURL">
    <portlet:param name="mvcPath" value="/addemployee.jsp"/>
</portlet:renderURL>

<liferay-portlet:renderURL varImpl="iteratorURL">
    <portlet:param name="mvcPath" value="/view.jsp" />
</liferay-portlet:renderURL>

<div class="employee-list">
    <div class="header">
        <h2> Employees </h2>
            <a href="<%= addEmployeeRenderURL %>" class="btn btn-default">
                Add New Employee
            </a>
    </div>

        <portlet:renderURL var="updateEmployeeRenderURL">
            portlet:param name="mvcPath" value="/updateemployee.jsp"/>
            <portlet:param name="employeeId" value="${employee.employeeId}"/>
            <portlet:param name="firstName" value="${employee.firstName}"/>
            <portlet:param name="lastName" value="${employee.lastName}"/>
            <portlet:param name="designation" value="${employee.designation}"/>
            <portlet:param name="emailAddress" value="${employee.emailAddress}"/>
            <portlet:param name="phoneNumber" value="${employee.phoneNumber}"/>
            <portlet:param name="addressLine1" value="${employee.addressLine1}"/>
            <portlet:param name="addressLine2" value="${employee.addressLine2}"/>
            <portlet:param name="city" value="${employee.city}"/>
            <portlet:param name="zipCode" value="${employee.zipCode}"/>
        </portlet:renderURL>

        <portlet:actionURL name="/employee/delete" var="deleteEmployeeActionURL">
            <portlet:param name="employeeId" value="${employee.getEmployeeId()}"/>
        </portlet:actionURL>


        <liferay-ui:search-container total="${total}" delta="2" emptyResultsMessage="no-entries-found" iteratorURL="<%= iteratorURL %>">

            <liferay-ui:search-container-results results="${employeeList}" />

                <liferay-ui:search-container-row className="com.ignek.employee.model.Employee" modelVar="employee" indexVar="rowIndex">

                    <liferay-ui:search-container-column-text name="Sr No">
                        ${searchContainer.start + rowIndex + 1}
                    </liferay-ui:search-container-column-text>

                    <liferay-ui:search-container-column-text name="Name"
                        value="${employee.getFirstName()} ${employee.getLastName()}" />

                    <liferay-ui:search-container-column-text name="Designation"
                         value="${employee.getDesignation()}" />

                    <liferay-ui:search-container-column-text name="PhoneNumber"
                            value="${employee.getPhoneNumber()}" />

                    <liferay-ui:search-container-column-text name="EmailAddress"
                            value="${employee.getEmailAddress()}" />

                    <liferay-ui:search-container-column-text name="City"
                        value="${employee.getCity()}" />

                   <liferay-ui:search-container-column-text name="Action">

                        <a href="<%=updateEmployeeRenderURL%>" class="update-link">
                            <img src="<%= request.getContextPath() + "/images/pen.png" %>" alt="update" />
                        </a>

                        <a href="<%=deleteEmployeeActionURL%>" class="delete-link"
                            onclick="return confirm('Are you sure you want to delete this item?');">
                            <img src="<%= request.getContextPath() + "/images/trash.png" %>" alt="delete" />
                        </a>

                         <a href="" class="download-link">
                             <img src="<%= request.getContextPath() + "/images/download.png" %>" alt="download" />
                         </a>

                   </liferay-ui:search-container-column-text>

                </liferay-ui:search-container-row>

                <liferay-ui:search-iterator markupView="lexicon" paginate="true"/>
        </liferay-ui:search-container>
</div>