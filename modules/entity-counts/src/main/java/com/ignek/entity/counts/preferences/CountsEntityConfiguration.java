package com.ignek.entity.counts.preferences;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
        id = "com_ignek_entity_counts_countsentityconfiguration",
        localization = "content/Language",
        name = "counts-entity-configuration-name"
        )

public interface CountsEntityConfiguration {

        @Meta.AD(
               deflt = "journal",
                name = "counts-of",
                optionLabels = {
                        "Journal Articles",
                        "Documents",
                        "Users"
                },
                optionValues = {
                        "journal article",
                        "documents",
                        "users"
                },
                required = true
        )
        public String countsOf();
    }
