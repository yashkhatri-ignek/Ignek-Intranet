package com.ignek.employee.web.action;

import com.ignek.employee.model.Employee;
import com.ignek.employee.service.EmployeeLocalService;
import com.ignek.employee.web.constants.EmployeeConstants;
import com.ignek.employee.web.constants.EmployeePortletKeys;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.OutputStream;

@Component(
        property = {
                "javax.portlet.name=" + EmployeePortletKeys.EMPLOYEE,
                "mvc.command.name=/employee/download/pdf"
        },
        service = MVCResourceCommand.class
)
public class DownloadPdfResourceCommand implements MVCResourceCommand {

    @Override
    public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws PortletException {

        try {
            long employeeId = ParamUtil.getLong(resourceRequest, EmployeeConstants.EMPLOYEEID);

            Employee employee = _employeeLocalService.getEmployee(employeeId);

            resourceResponse.setContentType("application/pdf");
            resourceResponse.setProperty(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=employee_" + employeeId + ".pdf");

            OutputStream out = resourceResponse.getPortletOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("Employee Details"));
            document.add(new Paragraph("----------------------"));
            document.add(new Paragraph(EmployeeConstants.NAME + employee.getFirstName() + " " + employee.getLastName()));
            document.add(new Paragraph(EmployeeConstants.EMAILADDRESS + employee.getEmailAddress()));
            document.add(new Paragraph(EmployeeConstants.PHONENUMBER + employee.getPhoneNumber()));
            document.add(new Paragraph(EmployeeConstants.DESIGNATION + employee.getDesignation()));
            document.add(new Paragraph(EmployeeConstants.CITY + employee.getCity()));
            document.add(new Paragraph(EmployeeConstants.ADDRESSLINE1  + employee.getAddressLine1()));
            document.add(new Paragraph(EmployeeConstants.ADDRESSLINE2  + employee.getAddressLine2()));
            document.add(new Paragraph(EmployeeConstants.ZIPCODE  + employee.getZipCode()));

            document.close();

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Reference
    EmployeeLocalService _employeeLocalService;
}
