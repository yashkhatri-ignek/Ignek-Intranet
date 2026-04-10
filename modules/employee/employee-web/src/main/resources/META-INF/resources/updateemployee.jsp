<%@ include file="/init.jsp" %>

<portlet:actionURL name="/employee/update"  var="updateEmployeeActionURL"/>

<div class="add-employee">
<div class="container p-6">
<h2>Employee Form</h2>

<aui:form action="<%=updateEmployeeActionURL%>" name="employeeForm" method="POST"/>

<%
    long employeeId = Long.parseLong(renderRequest.getParameter("employeeId"));

	String firstName = renderRequest.getParameter("firstName");
	String lastName = renderRequest.getParameter("lastName");
	String designation = renderRequest.getParameter("designation");
	String emailAddress = renderRequest.getParameter("emailAddress");
	String phoneNumber = renderRequest.getParameter("phoneNumber");
	String addressLine1 = renderRequest.getParameter("addressLine1");
	String addressLine2 = renderRequest.getParameter("addressLine2");
	String city = renderRequest.getParameter("city");
	String zipCode = renderRequest.getParameter("zipCode");
%>

<aui:form action="<%= updateEmployeeActionURL %>" method="post">

<div class="row">
     <aui:input name="employeeId" type="hidden"
            value="<%= employeeId %>"/>

    <div class="col-md-6 mb-3">
	<aui:input name="firstName" type="text"
            value="<%= firstName %>">
	   <aui:validator name="required"/>
    </aui:input>
    </div>

     <div class="col-md-6 mb-3">
	<aui:input name="lastName" type="text"
	        value="<%= lastName %>">
	     <aui:validator name="required"/>
	</aui:input>
	 </div>

	<div class="col-12 mb-3">
	<aui:input name="designation" type="text"
            value="<%= designation %>" style="width: 904px;">
         <aui:validator name="required"/>
    </aui:input>
     </div>

     <div class="col-md-6 mb-3">
    <aui:input name="emailAddress" type="text"
            value="<%= emailAddress %>">
         <aui:validator name="required"/>
    </aui:input>
     </div>

     <div class="col-md-6 mb-3">
     <aui:input name="phoneNumber" type="text"
            value="<%= phoneNumber %>">
        <aui:validator name="required"/>
     </aui:input>
      </div>

     <div class="col-md-6 mb-3">
    <aui:input name="addressLine1" type="text"
            value="<%= addressLine1 %>">
        <aui:validator name="required"/>
    </aui:input>
     </div>

     <div class="col-md-6 mb-3">
    <aui:input name="addressLine2" type="text"
            value="<%= addressLine2 %>">
        <aui:validator name="required"/>
    </aui:input>
     </div>

    <div class="col-md-6 mb-3">
    <aui:input name="city" type="text"
            value="<%= city %>">
         <aui:validator name="required"/>
    </aui:input>
     </div>

    <div class="col-md-6 mb-3">
    <aui:input name="zipCode" type="text"
            value="<%= zipCode %>">
        <aui:validator name="required"/>
    </aui:input>
    </div>

	<div class="submit-btn">
	<aui:button type="submit" name="update" value="UPDATE" cssClass="btn"></aui:button>
    </div>

</div>
</aui:form>
</div>
</div>