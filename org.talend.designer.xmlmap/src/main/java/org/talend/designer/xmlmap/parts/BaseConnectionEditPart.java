// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.xmlmap.parts;

import org.eclipse.gef.editparts.AbstractConnectionEditPart;

/**
 * DOC talend class global comment. Detailled comment
 */
public abstract class BaseConnectionEditPart extends AbstractConnectionEditPart {

    private boolean nodeCollapsed = false;

    private boolean sourceConcained = true;

    private boolean targetContained = true;

    public boolean isDOTStyle() {
        return nodeCollapsed || !sourceConcained || !targetContained;
    }

    public void setNodeCollapsed(boolean nodeCollapsed) {
        this.nodeCollapsed = nodeCollapsed;
    }

    public void setSourceConcained(boolean sourceConcained) {
        this.sourceConcained = sourceConcained;
    }

    public void setTargetContained(boolean targetContained) {
        this.targetContained = targetContained;
    }
}
