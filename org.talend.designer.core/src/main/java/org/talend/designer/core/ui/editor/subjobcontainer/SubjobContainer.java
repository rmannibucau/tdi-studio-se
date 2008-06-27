// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.editor.subjobcontainer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.talend.core.model.process.EComponentCategory;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.Element;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.process.ISubjobContainer;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.ElementParameter;
import org.talend.designer.core.ui.editor.TalendEditor;
import org.talend.designer.core.ui.editor.nodecontainer.NodeContainer;
import org.talend.designer.core.ui.editor.nodes.Node;

/**
 * DOC nrousseau class global comment. Detailled comment
 */
public class SubjobContainer extends Element implements ISubjobContainer {

    public static final String UPDATE_SUBJOB_CONTENT = "UPDATE_SUBJOB_CONTENT";

    public static final String UPDATE_SUBJOB_DATA = "UPDATE_SUBJOB_DATA";

    protected List<NodeContainer> nodeContainers = new ArrayList<NodeContainer>();

    private IProcess2 process;

    public SubjobContainer(IProcess2 process) {
        // if the subjob is in collapse State.
        this.process = process;

        ElementParameter param = new ElementParameter(this);
        param.setName(EParameterName.COLLAPSED.getName());
        param.setValue(Boolean.FALSE);
        param.setDisplayName(EParameterName.COLLAPSED.getDisplayName());
        param.setField(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.BASIC);
        param.setNumRow(1);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(false);
        addElementParameter(param);

        param = new ElementParameter(this);
        param.setName(EParameterName.SHOW_SUBJOB_TITLE.getName());
        param.setValue(Boolean.FALSE);
        param.setDisplayName(EParameterName.SHOW_SUBJOB_TITLE.getDisplayName());
        param.setField(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.BASIC);
        param.setNumRow(2);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(true);
        addElementParameter(param);

        // Name of the subjob (title)
        param = new ElementParameter(this);
        param.setName(EParameterName.SUBJOB_TITLE.getName());
        param.setValue("");
        param.setDisplayName(EParameterName.SUBJOB_TITLE.getDisplayName());
        param.setField(EParameterFieldType.TEXT);
        param.setCategory(EComponentCategory.BASIC);
        param.setNumRow(2);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShowIf(EParameterName.SHOW_SUBJOB_TITLE.getName() + " == 'true'");
        addElementParameter(param);

        param = new ElementParameter(this);
        param.setName(EParameterName.SUBJOB_COLOR.getName());
        param.setValue("220;220;250"); // default subjob color
        param.setDisplayName(EParameterName.SUBJOB_COLOR.getDisplayName());
        param.setField(EParameterFieldType.COLOR);
        param.setCategory(EComponentCategory.BASIC);
        param.setNumRow(3);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(true);
        addElementParameter(param);

        // Unique name of the the start linked with this subjob.
        param = new ElementParameter(this);
        param.setName(EParameterName.UNIQUE_NAME.getName());
        param.setValue("");
        param.setDisplayName(EParameterName.UNIQUE_NAME.getDisplayName());
        param.setField(EParameterFieldType.TEXT);
        param.setCategory(EComponentCategory.TECHNICAL);
        param.setNumRow(2);
        param.setReadOnly(false);
        param.setRequired(false);
        param.setShow(false);
        addElementParameter(param);

        param = new ElementParameter(this);
        param.setName(EParameterName.UPDATE_COMPONENTS.getName());
        param.setValue(Boolean.FALSE);
        param.setDisplayName(EParameterName.UPDATE_COMPONENTS.getDisplayName());
        param.setField(EParameterFieldType.CHECK);
        param.setCategory(EComponentCategory.BASIC);
        param.setNumRow(5);
        param.setReadOnly(true);
        param.setRequired(false);
        param.setShow(false);
        addElementParameter(param);
    }

    public void addNodeContainer(NodeContainer nodeContainer) {
        nodeContainers.add(nodeContainer);
        nodeContainer.setSubjobContainer(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.Element#getElementName()
     */
    @Override
    public String getElementName() {
        return (String) getPropertyValue(EParameterName.SUBJOB_TITLE.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IElement#isReadOnly()
     */
    public boolean isReadOnly() {
        return process.isReadOnly();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IElement#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) {
        // TODO Auto-generated method stub

    }

    /**
     * Getter for nodeContainers.
     * 
     * @return the nodeContainers
     */
    public List<NodeContainer> getNodeContainers() {
        return this.nodeContainers;
    }

    public boolean deleteNodeContainer(NodeContainer nodeContainer) {
        if (getNodeContainers().contains(nodeContainer)) {
            getNodeContainers().remove(nodeContainer);
            updateSubjobContainer();
            return true;
        }
        return false;
    }

    /**
     * DOC nrousseau Comment method "getSubjobContainerRectangle".
     * 
     * @return
     */
    public Rectangle getSubjobContainerRectangle() {
        Rectangle totalRectangle = null;
        boolean collapsed = isCollapsed();
        for (NodeContainer container : nodeContainers) {
            Rectangle curRect = container.getNodeContainerRectangle();
            if (collapsed && container.getNode().isDesignSubjobStartNode()) {
                totalRectangle = curRect.getCopy();
            } else if (!collapsed) {
                if (totalRectangle == null) {
                    totalRectangle = curRect.getCopy();
                } else {
                    totalRectangle = totalRectangle.getUnion(curRect);
                }
            }
        }

        if (totalRectangle == null) {
            return null;
        }

        Point location = totalRectangle.getLocation();
        Point newLocation = new Point();
        newLocation.x = (location.x / TalendEditor.GRID_SIZE) * TalendEditor.GRID_SIZE;
        newLocation.y = (location.y / TalendEditor.GRID_SIZE) * TalendEditor.GRID_SIZE;
        totalRectangle.setLocation(newLocation);
        Dimension diff = location.getDifference(newLocation);
        Dimension size = totalRectangle.getSize().expand(diff);
        if ((size.height % TalendEditor.GRID_SIZE) == 0) {
            size.height = (size.height / TalendEditor.GRID_SIZE) * TalendEditor.GRID_SIZE;
        } else {
            size.height = ((size.height / TalendEditor.GRID_SIZE) + 1) * TalendEditor.GRID_SIZE;
        }
        if ((size.width % TalendEditor.GRID_SIZE) == 0) {
            size.width = (size.width / TalendEditor.GRID_SIZE) * TalendEditor.GRID_SIZE;
        } else {
            size.width = ((size.width / TalendEditor.GRID_SIZE) + 1) * TalendEditor.GRID_SIZE;
        }
        totalRectangle.setSize(size);
        return totalRectangle;
    }

    /**
     * Getter for collapsed.
     * 
     * @return the collapsed
     */
    public boolean isCollapsed() {
        return (Boolean) getPropertyValue(EParameterName.COLLAPSED.getName());
    }

    /**
     * Sets the collapsed.
     * 
     * @param collapsed the collapsed to set
     */
    public void setCollapsed(boolean collapsed) {
        setPropertyValue(EParameterName.COLLAPSED.getName(), new Boolean(collapsed));
    }

    public Node getSubjobStartNode() {
        String subjobStartUniqueName = (String) getPropertyValue(EParameterName.UNIQUE_NAME.getName());

        for (Node node : (List<Node>) process.getGraphicalNodes()) {
            if (node.getUniqueName().equals(subjobStartUniqueName)) {
                return node;
            }
        }

        return null;
    }

    public void setSubjobStartNode(Node node) {
        setPropertyValue(EParameterName.UNIQUE_NAME.getName(), node.getUniqueName());
        if (node.getComponent().getName().equals("tPrejob") || node.getComponent().getName().equals("tPostjob")) {
            setPropertyValue(EParameterName.SUBJOB_COLOR.getName(), "255;220;180");
            setPropertyValue(EParameterName.SHOW_SUBJOB_TITLE.getName(), Boolean.TRUE);
            getElementParameter(EParameterName.SHOW_SUBJOB_TITLE.getName()).setShow(false);
        } else {
            getElementParameter(EParameterName.SHOW_SUBJOB_TITLE.getName()).setShow(true);
        }
    }

    public void updateSubjobContainer() {
        fireStructureChange(UPDATE_SUBJOB_CONTENT, this);
    }

    /**
     * Getter for process.
     * 
     * @return the process
     */
    public IProcess2 getProcess() {
        return this.process;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.Element#setPropertyValue(java.lang.String, java.lang.Object)
     */
    @Override
    public void setPropertyValue(String id, Object value) {
        super.setPropertyValue(id, value);
        if (id.equals(EParameterName.COLLAPSED.getName())) {
            updateSubjobContainer();
        } else if (id.equals(EParameterName.SUBJOB_COLOR.getName()) || id.equals(EParameterName.SHOW_SUBJOB_TITLE.getName())
                || id.equals(EParameterName.SUBJOB_TITLE.getName())) {
            fireStructureChange(UPDATE_SUBJOB_DATA, this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String nodes = "";
        for (NodeContainer nodeContainer : nodeContainers) {
            nodes += nodeContainer.getNode().toString();
            if (nodeContainers.indexOf(nodeContainer) != (nodeContainers.size() - 1)) {
                nodes += ", ";
            }
        }
        return "SubjobContainer [" + nodes + "]";
    }
}
