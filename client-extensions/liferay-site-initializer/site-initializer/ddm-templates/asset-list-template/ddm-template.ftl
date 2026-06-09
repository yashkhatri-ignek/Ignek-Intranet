<#if entries?has_content>
    <div class="row">
        <#list entries as curEntry>
            <div class="col-md-4" >
                <#assign
                    assetRenderer = curEntry.getAssetRenderer()
                    journalArticle = assetRenderer.getAssetObject()
                 />
                <@liferay_journal["journal-article"]
                    articleId=journalArticle.getArticleId()
                    ddmTemplateKey=journalArticle.getDDMTemplateKey()
                    groupId=journalArticle.getGroupId()
                />
            </div>
        </#list>
    </div>
</#if>