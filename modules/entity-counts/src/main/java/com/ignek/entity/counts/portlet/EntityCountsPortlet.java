package com.ignek.entity.counts.portlet;

import com.ignek.entity.counts.constants.EntityCountsPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import javax.portlet.Portlet;
import org.osgi.service.component.annotations.Component;

import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import java.io.IOException;

@Component(
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=EntityCounts",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.config-template=/configuration.jsp",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + EntityCountsPortletKeys.ENTITYCOUNTS_ID,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class EntityCountsPortlet extends MVCPortlet {

   @Reference
   private JournalArticleLocalService journalArticleLocalService;

   @Reference
   private DLFileEntryLocalService dlFileEntryLocalService;

   @Reference
   private UserLocalService userLocalService;

	public void render(RenderRequest renderRequest,RenderResponse renderResponse)
			throws IOException, PortletException {

		String entity = renderRequest.getPreferences().getValue("countsOf", "journal");

		long count = 0;
		String entityName = "";

		if ("journal article".equals(entity)) {
			count = journalArticleLocalService.getJournalArticlesCount();
			entityName = "Journal Articles";
		}
		else if ("documents".equals(entity)) {
			count = dlFileEntryLocalService.getDLFileEntriesCount();
			entityName = "Documents";
		}
		else if ("users".equals(entity)) {
			count = userLocalService.getUsersCount();
			entityName = "Users";
		}

		renderRequest.setAttribute("entityCount",count);
		renderRequest.setAttribute("entityName",entityName);

		super.render(renderRequest,renderResponse);
	}
}