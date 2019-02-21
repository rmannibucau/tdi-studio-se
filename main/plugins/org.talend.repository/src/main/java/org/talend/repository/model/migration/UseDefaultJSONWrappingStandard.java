package org.talend.repository.model.migration;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import org.talend.commons.exception.PersistenceException;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.components.ComponentUtilities;
import org.talend.core.model.components.ModifyComponentsAction;
import org.talend.core.model.components.conversions.IComponentConversion;
import org.talend.core.model.components.filters.IComponentFilter;
import org.talend.core.model.components.filters.NameComponentFilter;
import org.talend.core.model.migration.AbstractJobMigrationTask;
import org.talend.core.model.properties.Item;

public class UseDefaultJSONWrappingStandard extends AbstractJobMigrationTask {
    private static final String JSON_STANDARD_PROPERTY_NAME = "JSON_STANDARD";
    private static final String JSON_STANDARD_PROPERTY_TYPE = "CLOSED_LIST";
    private static final String JSON_STANDARD_MIGRATION_VALUE = "LEGACY";

    @Override
    public Date getOrder() {
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.FEBRUARY, 21, 14, 0, 0);
        return gc.getTime();
    }

    @Override
    public ExecutionResult execute(Item item) {
        org.talend.designer.core.model.utils.emf.talendfile.ProcessType processType = getProcessType(item);
        if (getProject().getLanguage() != ECodeLanguage.JAVA || processType == null) {
            return ExecutionResult.NOTHING_TO_DO;
        }
        IComponentConversion setOldStandardConversion = node -> {
            if (ComponentUtilities.getNodeProperty(node, JSON_STANDARD_PROPERTY_NAME) == null) {
                ComponentUtilities.addNodeProperty(node, JSON_STANDARD_PROPERTY_NAME, JSON_STANDARD_PROPERTY_TYPE);
            }

            ComponentUtilities.setNodeValue(node, JSON_STANDARD_PROPERTY_NAME, JSON_STANDARD_MIGRATION_VALUE);
        };
        IComponentFilter filter = new NameComponentFilter("tWriteJSONField");
        try {
            ModifyComponentsAction.searchAndModify(item, processType, filter, Collections.singletonList(setOldStandardConversion));
            return ExecutionResult.SUCCESS_NO_ALERT;
        } catch (PersistenceException e) {
            return ExecutionResult.FAILURE;
        }
    }
}
