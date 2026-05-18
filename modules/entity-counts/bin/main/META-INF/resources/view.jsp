<%@ include file="/init.jsp" %>

<div class="entity-portlet">
    <div class="card">
        <div class="card-body">
            <div class="top-text">
                 <img src="<%= request.getContextPath() + "/images/logo.png" %>" alt="Logo" />
                 <p>
                    ${entityName}
                </p>
            </div>
            <div class="bottom-text">
                <h1>
                    ${entityCount}
                </h1>
            </div>
        </div>
    </div>
</div>
