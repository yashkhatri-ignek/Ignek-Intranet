package com.ignek.mail.scheduler;

import com.ignek.employee.model.Employee;
import com.ignek.employee.service.EmployeeLocalService;
import com.ignek.mail.scheduler.configuration.SchedulerConfiguration;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import javax.mail.internet.InternetAddress;
import java.io.*;
import java.util.List;
import java.util.Map;

@Component(
        configurationPid = "com.ignek.mail.scheduler.configuration.SchedulerConfiguration",
        service = SchedulerJobConfiguration.class
)
public class EmailSchedulerConfiguration implements SchedulerJobConfiguration {

    @Override
    public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
        return () -> {

            try {
                List<JournalArticle> articles = _journalArticleLocalService.getArticles(20117, 0);
                if (articles == null || articles.isEmpty()) {
                    _log.warn("No Web Content articles found! Skipping execution.");
                    return;
                }

                JournalArticle targetArticle = null;
                for (JournalArticle article : articles) {
                    String title = article.getTitle("en_US");
                    if (Validator.isNotNull(title) && title.toLowerCase().contains("employee policy update in 2026")) {
                        targetArticle = article;
                        break;
                    }
                }

                String articleTitle = targetArticle.getTitle("en_US");
                String rawXml = targetArticle.getContentByLocale("en_US");

                String parsedRuleContent = extractStructuredFieldValue(rawXml, "content");
                String cleanRuleText = HtmlUtil.stripHtml(parsedRuleContent);

                List<Employee> employeeList = _employeeLocalService.getEmployees(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

                if (employeeList == null || employeeList.isEmpty()) {
                    _log.warn("No employees found! Skipping email sending.");
                    return;
                }

                int successCount = 0;
                int failCount = 0;

                for (Employee employee : employeeList) {
                    try {
                        sendEmailWithWebContentPdf(employee, articleTitle, cleanRuleText);
                        successCount++;
                    } catch (Exception e) {
                        failCount++;
                        _log.error("Failed to send email to: " + employee.getEmailAddress() + " Reason: " + e.getMessage(), e);
                    }
                }
                _log.info("Scheduler completed! " + "Success: " + successCount + " Failed: " + failCount);

            } catch (Exception e) {
                _log.error("Scheduler error: " + e.getMessage(), e);
            }
        };
    }
    
    private void sendEmailWithWebContentPdf(Employee employee, String articleTitle, String ruleText) throws UnsupportedEncodingException {

        InternetAddress fromAddress = new InternetAddress("yash.khatri.ignek@gmail.com", "Ignek HR Team");
        InternetAddress toAddress = new InternetAddress(employee.getEmailAddress(), employee.getFirstName());

        String subject = "Employee Policy update from Ignek";
        String emailHtmlBody = "<html><body><h2>Hello, " + employee.getFirstName() + "!</h2>"
                + "<p>Please review the attached PDF document containing our weekly company profile notes: <b>" + "</b>.</p>"
                + "<br/><p>Best Regards,</p><p><b>Ignek HR Team</b></p></body></html>";

        File tempPdfFile = FileUtil.createTempFile("Employee Profile " + employee.getEmployeeId(), ".pdf");

        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(tempPdfFile)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            document.add(new Paragraph("OFFICIAL DOCUMENT SUMMARY: " + articleTitle.toUpperCase()));
            document.add(new Paragraph("Recipient Full Name: " + employee.getFirstName() + " " + employee.getLastName()));
            document.add(new Paragraph("Registered Core Designation: " + employee.getDesignation()));
            document.add(new Paragraph("--------------------------------------------------------------------------------------------------------"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(ruleText));

            document.close();

        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }

        MailMessage mailMessage = new MailMessage();
        mailMessage.setFrom(fromAddress);
        mailMessage.setTo(toAddress);
        mailMessage.setSubject(subject);
        mailMessage.setBody(emailHtmlBody);
        mailMessage.setHTMLFormat(true);

        mailMessage.addFileAttachment(tempPdfFile, "Policy_Update.pdf");

        _mailService.sendEmail(mailMessage);
    }

    private String extractStructuredFieldValue(String xmlContent, String fieldName) {
        try {
            if (Validator.isNull(xmlContent)) {
                return "";
            }
            com.liferay.portal.kernel.xml.Document xmlDocument = SAXReaderUtil.read(xmlContent);
            Node node = xmlDocument.selectSingleNode("/root/dynamic-element[@name='" + fieldName + "']/dynamic-content");
            if (node != null) {
                return node.getText();
            }
        } catch (Exception e) {
            _log.error("XML structural parsing failed for field name: " + fieldName + " Details: " + e.getMessage());
        }
        return "";
    }

    @Override
    public TriggerConfiguration getTriggerConfiguration() {
        return TriggerConfiguration.createTriggerConfiguration(_schedulerConfiguration.interval(), TimeUnit.WEEK);
    }

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) {
        _schedulerConfiguration = ConfigurableUtil.createConfigurable(SchedulerConfiguration.class, properties);
    }

    @Reference
    EmployeeLocalService _employeeLocalService;

    @Reference
    JournalArticleLocalService _journalArticleLocalService;

    @Reference
    private MailService _mailService;

    private SchedulerConfiguration _schedulerConfiguration;

    private static final Log _log = LogFactoryUtil.getLog(EmailSchedulerConfiguration.class);
}
