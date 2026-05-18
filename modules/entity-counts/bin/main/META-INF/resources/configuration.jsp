
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ include file="/init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationActionURL" />
<liferay-portlet:renderURL portletConfiguration="true" var="configurationRenderURL" />

<aui:form action="${configurationActionURL}" method="post" name="fm">
    <aui:input name="<%=Constants.CMD%>" type="hidden" value="<%=Constants.UPDATE%>" />
    <aui:input name="redirect" type="hidden" value="${configurationRenderURL}" />

    <aui:fieldset>
        <aui:select name="countsOf" label="Counts Of" value="<%=(String)portletPreferences.getValue("countsOf", "journal")%>">
            <aui:option value="journal article">Journal Articles</aui:option>
            <aui:option value="documents">Documents</aui:option>
            <aui:option value="users">Users</aui:option>
        </aui:select>
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit" value="Save" />
    </aui:button-row>

</aui:form>