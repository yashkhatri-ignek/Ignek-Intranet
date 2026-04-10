<!DOCTYPE html>

<#include init />

<html class="${root_css_class}" dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
	<title>${html_title}</title>

	<meta content="initial-scale=1.0, width=device-width" name="viewport" />

	<@liferay_util["include"] page=top_head_include />
</head>

<body class="${css_class}">

<@liferay_ui["quick-access"] contentId="#main-content" />

<@liferay_util["include"] page=body_top_include />

<@liferay.control_menu />

<div class="main-container" id="wrapper">
	<div class="sidebar-theme">
		<div class="sidebar">
			<div class="site-title">
				<h3>
					<#if show_site_name>
						<span class="site-name"
							  title="<@liferay.language_format arguments="${site_name}" key="go-to-x" />">
								${site_name}
						</span>
					</#if>
				</h3>
			</div>

			<#assign user = themeDisplay.getUser()/>

			<div class="user-profile">
				<img src=
					 "${user.getPortraitURL(themeDisplay)}"
					 alt="profile-image">
				<h4 class="user-name">
					${user.getFullName()}
				</h4>
				<span class="user-role">
					${user.getJobTitle()}
				</span>
			</div>

			<div class="sidebar-navigation">
				<#if has_navigation && is_setup_complete>
					<#include "${full_templates_path}/navigation.ftl" />
				</#if>
			</div>

			<div class="logout">
				<#if show_sign_out>
					<a href="${sign_out_url}">Logout</a>
					<img src="${images_folder}/sign-out.png" alt="Sign-out">
				</#if>
			</div>
		</div>

		<div class="navbar-container">
			<div class="search-bar">
				<@liferay.search_bar />
			</div>

			<div class="notification">
				<img src="${images_folder}/bell.png" alt="notification">
			</div>
		</div>
	</div>

	<section id="content">
		<#if selectable>
			<@liferay_util["include"] page=content_include />
		<#else>
			${portletDisplay.recycle()}

			${portletDisplay.setTitle(the_title)}

			<@liferay_theme["wrap-portlet"] page="portlet.ftl">
				<@liferay_util["include"] page=content_include />
			</@>
		</#if>
	</section>
</div>

<@liferay_util["include"] page=body_bottom_include />
<@liferay_util["include"] page=bottom_include />

<!-- inject:js -->
<!-- endinject -->

</body>
</html>