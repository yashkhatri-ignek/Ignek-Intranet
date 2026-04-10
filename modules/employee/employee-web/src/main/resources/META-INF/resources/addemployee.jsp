<%@ include file="/init.jsp" %>

<portlet:actionURL name="/employee/add"  var="addEmployeeActionURL"/>
<div class="add-employee">
<div class="container">
<h2>Employee Form</h2>

<aui:form action="<%=addEmployeeActionURL%>" name="employeeForm" method="POST">

<div class="row">

    <div class="col-md-6 mb-3">
    <aui:input name="firstName" placeholder="Enter your first name" label="First Name">
 		<aui:validator name="required"/>
 		<aui:validator name="alpha"/>
	</aui:input>
    </div>

    <div class="col-md-6 mb-3">
	<aui:input name="lastName" placeholder="Enter your last name" label="Last Name">
 		<aui:validator name="required"/>
 		<aui:validator name="alpha"/>
	</aui:input>
    </div>

    <div class="col-12 mb-3">
	<aui:input name="designation" placeholder="Enter your designation" placeholderCSS="font-size:30px;" label="Designation" style="width: 904px;">
 		<aui:validator name="required"/>
	</aui:input>
    </div>

    <div class="col-md-6 mb-3">
	<aui:input name="emailAddress" placeholder="Enter your email" label="Email">
 		<aui:validator name="required"/>
 		<aui:validator name="email"/>
	</aui:input>
    </div>

    <div class="col-md-6 mb-3">
	<aui:input name="phoneNumber" placeholder="Enter your phone number" label="Phone">
     	<aui:validator name="required"/>
     	<aui:validator name="digits"/>
    </aui:input>
    </div>

    <div class="col-md-6 mb-3" >
    <aui:input name="addressLine1" placeholder="Enter your house no / Appt." label="Address Line 1">
         <aui:validator name="required"/>
    </aui:input>
    </div>

    <div class="col-md-6 mb-3">
    <aui:input name="addressLine2" placeholder="Enter your street / area" label="Address Line 2">
         <aui:validator name="required"/>
    </aui:input>
    </div>

    <div class="col-md-6 mb-3">
    <aui:input name="city" placeholder="Enter your city" label="City">
         <aui:validator name="required"/>
         <aui:validator name="alpha"/>
    </aui:input>
    </div>

    <div class="col-md-6 mb-3">
    <aui:input name="zipCode" placeholder="Enter your post code/ zip code" label="Post Code/zip Code">
        <aui:validator name="required"/>
        <aui:validator name="digits"/>
    </aui:input>
    </div>

    <div class="submit-btn">
	<aui:button type="submit" name="" value="SUBMIT" cssClass="btn"></aui:button>
    </div>

</div>
</aui:form>
</div>
</div>