package com.ignek.mail.scheduler.configuration;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(
        category = "infrastructure",
        scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
        id = "com.ignek.mail.scheduler.configuration.SchedulerConfiguration",
        localization = "content/Language",
        name = "mail-notification-Configuration"
)
public interface SchedulerConfiguration {

    @Meta.AD(
            deflt = "1",
            description = "employee-scheduler-interval",
            name = "Employee Scheduler Interval",
            required = false
    )

    public int interval();
}
