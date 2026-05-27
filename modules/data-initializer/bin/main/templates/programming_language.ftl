<div class="cards template-card">
    <div class="card">
        <div class="card-heading">
            <#if (cardimg.getData())?? && cardimg.getData() != "">
	            <img alt="${cardimg.getAttribute("alt")}" data-fileentryid="${cardimg.getAttribute("fileEntryId")}" src="${cardimg.getData()}" />
			</#if>
            <h3 class="card-title">
                <#if (title.getData())??>
                    ${title.getData()}
			    </#if>
			</h3>
        </div>
    </div>
</div>
