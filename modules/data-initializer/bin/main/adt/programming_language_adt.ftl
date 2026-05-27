<#if entries?has_content>
    <div class="programming-language-adt">
        <#list entries as entry>
            <#assign assetRenderer = entry.getAssetRenderer() />
            <#assign doc = assetRenderer.getArticle() />
            <div class="language-card">
                <h3>${entry.getTitle(locale)}</h3>
            </div>
        </#list>
    </div>
</#if>