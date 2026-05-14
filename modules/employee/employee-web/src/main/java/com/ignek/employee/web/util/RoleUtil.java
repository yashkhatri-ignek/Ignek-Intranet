package com.ignek.employee.web.util;

import com.ignek.employee.web.constants.EmployeeConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import java.util.List;

public class RoleUtil {

    public static boolean isHR(ThemeDisplay themeDisplay) {
        try {
            long userId = themeDisplay.getUserId();
            long groupId = themeDisplay.getScopeGroupId();

            List<Role> siteRoles = RoleLocalServiceUtil.getUserGroupRoles(
                    userId, groupId);
            for (Role role : siteRoles) {
                if (role.getName().equalsIgnoreCase(EmployeeConstants.ROLE_HR)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isHROrAdmin(ThemeDisplay themeDisplay) {
        try {
            if (themeDisplay.getPermissionChecker().isCompanyAdmin()) {
                return true;
            }
            return isHR(themeDisplay);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
