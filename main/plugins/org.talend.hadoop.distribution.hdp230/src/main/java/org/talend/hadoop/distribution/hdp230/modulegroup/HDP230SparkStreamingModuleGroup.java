// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.hadoop.distribution.hdp230.modulegroup;

import java.util.HashSet;
import java.util.Set;

import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.condition.BasicExpression;
import org.talend.hadoop.distribution.condition.ComponentCondition;
import org.talend.hadoop.distribution.condition.EqualityOperator;
import org.talend.hadoop.distribution.condition.SimpleComponentCondition;
import org.talend.hadoop.distribution.constants.SparkStreamingConstant;

public class HDP230SparkStreamingModuleGroup {

    private static final String MODULE_GROUP_NAME = "SPARK-LIB-HDP_2_3"; //$NON-NLS-1$

    private final static ComponentCondition condition = new SimpleComponentCondition(new BasicExpression(
            SparkStreamingConstant.SPARKCONFIGURATION_IS_LOCAL_MODE_PARAMETER, "false", EqualityOperator.EQ)); //$NON-NLS-1$

    public static Set<DistributionModuleGroup> getModuleGroups() {
        Set<DistributionModuleGroup> hs = new HashSet<>();
        DistributionModuleGroup dmg = new DistributionModuleGroup(MODULE_GROUP_NAME, false, condition);
        hs.add(dmg);
        return hs;
    }

}
