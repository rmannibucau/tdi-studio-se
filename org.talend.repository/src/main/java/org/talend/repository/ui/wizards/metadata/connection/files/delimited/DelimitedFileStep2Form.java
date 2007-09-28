// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2007 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.repository.ui.wizards.metadata.connection.files.delimited;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.talend.commons.ui.swt.dialogs.ErrorDialogWidthDetailArea;
import org.talend.commons.ui.swt.formtools.Form;
import org.talend.commons.ui.swt.formtools.LabelledCheckboxCombo;
import org.talend.commons.ui.swt.formtools.LabelledCombo;
import org.talend.commons.ui.swt.formtools.LabelledText;
import org.talend.commons.ui.swt.formtools.UtilsButton;
import org.talend.commons.ui.swt.thread.SWTUIThreadProcessor;
import org.talend.core.model.metadata.EMetadataEncoding;
import org.talend.core.model.metadata.builder.connection.Escape;
import org.talend.core.model.metadata.builder.connection.FieldSeparator;
import org.talend.core.model.metadata.builder.connection.FileFormat;
import org.talend.core.model.metadata.builder.connection.RowSeparator;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.core.utils.CsvArray;
import org.talend.repository.i18n.Messages;
import org.talend.repository.preview.ProcessDescription;
import org.talend.repository.ui.swt.preview.ShadowProcessPreview;
import org.talend.repository.ui.swt.utils.AbstractDelimitedFileStepForm;
import org.talend.repository.ui.swt.utils.IRefreshable;
import org.talend.repository.ui.utils.ShadowProcessHelper;

/**
 * @author ocarbone
 * 
 */
public class DelimitedFileStep2Form extends AbstractDelimitedFileStepForm implements IRefreshable {

    private static Logger log = Logger.getLogger(DelimitedFileStep2Form.class);

    private static final String EMPTY_VALUE = Messages.getString("FileStep2.empty"); //$NON-NLS-1$

    private static final String[] TEXT_ENCLOSURE_DATA = { EMPTY_VALUE,
            TalendTextUtils.addQuotes("\""), TalendTextUtils.addQuotes("\'"), TalendTextUtils.addQuotes("\\\\") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final String[] ESCAPE_CHAR_DATA = { EMPTY_VALUE,
            TalendTextUtils.addQuotes("\""), TalendTextUtils.addQuotes("\'"), TalendTextUtils.addQuotes("\\\\") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final String[] STRING_NUMBERS_DATA = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
            "14", "15", "16", "17", "18", "19", "20" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

    /**
     * Main Fields.
     */
    private LabelledCombo encodingCombo;

    private LabelledCombo fieldSeparatorCombo;

    private LabelledText fieldSeparatorText;

    private LabelledCombo rowSeparatorCombo;

    private LabelledCombo textEnclosureCombo;

    private LabelledCheckboxCombo rowsToSkipHeaderCheckboxCombo;

    private LabelledCheckboxCombo rowsToSkipLimitCheckboxCombo;

    private LabelledCheckboxCombo rowsToSkipFooterCheckboxCombo;

    private LabelledText rowSeparatorText;

    private LabelledCombo escapeCharCombo;

    private Button csvRadio;

    private Button delimitedRadio;

    private Button emptyRowsToSkipCheckbox;

    private Label escapeCharFlag;

    private Label textEnclosureFlag;

    /**
     * Fields use to preview.
     */

    private Group previewGroup;

    private Button firstRowIsCaptionCheckbox;

    private Button previewButton;

    private Label previewInformationLabel;

    private ShadowProcessPreview delimitedFilePreview;

    SWTUIThreadProcessor processor = new PreviewProcessor();

    /**
     * Another.
     */

    private UtilsButton cancelButton;

    private boolean readOnly;

    /**
     * Constructor to use by RCP Wizard.
     * 
     * @param Composite
     * @param Wizard
     * @param Style
     */
    public DelimitedFileStep2Form(Composite parent, ConnectionItem connectionItem) {
        super(parent, connectionItem);
        setupForm();
    }

    /**
     * 
     * Initialize value, forceFocus first field.
     */
    @Override
    protected void initialize() {

        // Fields to the Group Delimited File Settings
        if (getConnection().getEncoding() != null && !getConnection().getEncoding().equals("")) { //$NON-NLS-1$
            encodingCombo.setText(getConnection().getEncoding());
        } else {
            encodingCombo.select(0);
        }

        fieldSeparatorCombo.setText(getConnection().getFieldSeparatorType().getName());
        fieldSeparatorText.setText(getConnection().getFieldSeparatorValue());
        fieldSeparatorText.setEditable(false);

        rowSeparatorCombo.setText(getConnection().getRowSeparatorType().getLiteral());
        rowSeparatorText.setText(getConnection().getRowSeparatorValue());
        rowSeparatorText.setEditable(false);

        // adpat Separator Combo and Text
        fieldSeparatorManager();
        rowSeparatorManager();

        // Fields to the Group Rows To Skip
        int i = getConnection().getHeaderValue();
        if (i > 0) {
            rowsToSkipHeaderCheckboxCombo.setText("" + getConnection().getHeaderValue()); //$NON-NLS-1$
        }
        rowsToSkipHeaderCheckboxCombo.getCombo().setEnabled(i > 0);
        i = getConnection().getFooterValue();
        if (i > 0) {
            rowsToSkipFooterCheckboxCombo.setText("" + getConnection().getFooterValue()); //$NON-NLS-1$
        }
        rowsToSkipFooterCheckboxCombo.getCombo().setEnabled(i > 0);
        i = getConnection().getLimitValue();
        if (i > 0) {
            rowsToSkipLimitCheckboxCombo.setText("" + getConnection().getLimitValue()); //$NON-NLS-1$
        }
        rowsToSkipLimitCheckboxCombo.getCombo().setEnabled(i > 0);

        emptyRowsToSkipCheckbox.setSelection(getConnection().isRemoveEmptyRow());
        firstRowIsCaptionCheckbox.setSelection(getConnection().isFirstLineCaption());

        // Fields to the Group Escape Char Settings
        textEnclosureCombo.select(0);
        escapeCharCombo.select(0);

        if (Escape.DELIMITED_LITERAL.equals(getConnection().getEscapeType())) {
            csvRadio.setSelection(false);
            delimitedRadio.setSelection(true);
            textEnclosureCombo.setEnabled(false);
            escapeCharCombo.setEnabled(false);
        }
        if (Escape.CSV_LITERAL.equals(getConnection().getEscapeType())) {
            csvRadio.setSelection(true);
            delimitedRadio.setSelection(false);
        }

        String s = getConnection().getEscapeChar();
        if (!(s == null) && !s.equals("") && !s.equals(EMPTY_VALUE)) { //$NON-NLS-1$
            escapeCharCombo.setText(s);
        }
        s = getConnection().getTextEnclosure();
        if (!(s == null) && !s.equals("") && !s.equals(EMPTY_VALUE)) { //$NON-NLS-1$
            textEnclosureCombo.setText(s);
        }

        // clearSelection of the selected combo
        encodingCombo.clearSelection();
        fieldSeparatorCombo.clearSelection();
        rowSeparatorCombo.clearSelection();
        escapeCharCombo.clearSelection();
        textEnclosureCombo.clearSelection();

        emptyRowsToSkipCheckbox.setSelection(getConnection().isRemoveEmptyRow());
        checkFieldsValue();
    }

    /**
     * DOC ocarbone Comment method "adaptFormToReadOnly".
     */
    @Override
    protected void adaptFormToReadOnly() {
        readOnly = isReadOnly();
        encodingCombo.setReadOnly(isReadOnly());
        fieldSeparatorCombo.setReadOnly(isReadOnly());
        fieldSeparatorText.setReadOnly(isReadOnly());
        rowSeparatorCombo.setReadOnly(isReadOnly());
        rowSeparatorText.setReadOnly(isReadOnly());
        delimitedRadio.setEnabled(!isReadOnly());
        // rowsToSkipHeaderCheckboxCombo.setReadOnly(isReadOnly());
        // rowsToSkipFooterCheckboxCombo.setReadOnly(isReadOnly());
        // rowsToSkipLimitCheckboxCombo.setReadOnly(isReadOnly());
        emptyRowsToSkipCheckbox.setEnabled(!isReadOnly());
        firstRowIsCaptionCheckbox.setEnabled(!isReadOnly());
        if (isReadOnly()) {
            escapeCharCombo.setReadOnly(true);
            textEnclosureCombo.setReadOnly(true);
        } else {
            textEnclosureCombo.setEnabled(csvRadio.getSelection());
            escapeCharCombo.setEnabled(csvRadio.getSelection());
        }
    }

    /**
     * add Field to Group Delimited File Settings.
     * 
     * @param mainComposite
     * @param form
     * @param width
     * @param height
     */
    private void addGroupDelimitedFileSettings(final Composite mainComposite, final int width, final int height) {
        Group group = Form.createGroup(mainComposite, 2,
                Messages.getString("FileStep2.groupDelimitedFileSettings"), height); //$NON-NLS-1$
        Composite compositeFileDelimitor = Form.startNewDimensionnedGridLayout(group, 4, width, height);

        EMetadataEncoding[] values = EMetadataEncoding.values();
        String[] encodingData = new String[values.length];
        for (int j = 0; j < values.length; j++) {
            encodingData[j] = values[j].getName();
        }

        encodingCombo = new LabelledCombo(compositeFileDelimitor, Messages.getString("FileStep2.encoding"), Messages //$NON-NLS-1$
                .getString("FileStep2.encodingTip"), encodingData, 3, true, SWT.NONE); //$NON-NLS-1$

        // Goto Next Line
        // new Label(compositeFileDelimitor, SWT.NONE);
        // new Label(compositeFileDelimitor, SWT.NONE);

        // Field Separator Combo & Text
        String[] fieldSeparatorData = { FieldSeparator.SEMICOLON_LITERAL.getName(),
                FieldSeparator.COMMA_LITERAL.getName(), FieldSeparator.TABULATION_LITERAL.getName(),
                FieldSeparator.SPACE_LITERAL.getName(), FieldSeparator.ALT_65_LITERAL.getName(),
                FieldSeparator.CUSTOM_ANSI_LITERAL.getName(), FieldSeparator.CUSTOM_UTF8_LITERAL.getName(),
                FieldSeparator.CUSTOM_REG_EXP_LITERAL.getName() };
        fieldSeparatorCombo = new LabelledCombo(compositeFileDelimitor,
                Messages.getString("FileStep2.fieldSeparator"), Messages //$NON-NLS-1$
                        .getString("FileStep2.fieldSeparatorDelimitedTip"), fieldSeparatorData, 1, true, SWT.READ_ONLY); //$NON-NLS-1$

        fieldSeparatorText = new LabelledText(compositeFileDelimitor, "", 1, true, SWT.RIGHT); //$NON-NLS-1$

        // Dimension of columb of Separator Text
        GridData gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        gridData.minimumWidth = 80;
        fieldSeparatorText.setLayoutData(gridData);

        // Row Separator Combo & Text
        String[] rowSeparatorData = { RowSeparator.STANDART_EOL_LITERAL.getLiteral(),
                RowSeparator.CUSTOM_STRING_LITERAL.getLiteral() };
        rowSeparatorCombo = new LabelledCombo(compositeFileDelimitor,
                Messages.getString("FileStep2.rowSeparator"), Messages //$NON-NLS-1$
                        .getString("FileStep2.rowSeparatorTip"), rowSeparatorData, 1, true, SWT.READ_ONLY); //$NON-NLS-1$
        rowSeparatorText = new LabelledText(compositeFileDelimitor, "", 1, true, SWT.RIGHT); //$NON-NLS-1$
    }

    private void addGroupRowsToSkip(final Composite mainComposite, final int width, final int height) {
        // compositerowsToSkip Main Fields
        Group group = Form.createGroup(mainComposite, 1, Messages.getString("FileStep2.groupRowsToSkip"), height); //$NON-NLS-1$
        Composite compositeRowsToSkip = Form.startNewDimensionnedGridLayout(group, 3, width - 100, height);

        // Information rowsToSkip
        Label info = new Label(compositeRowsToSkip, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        info.setLayoutData(gridData);
        info.setText(Messages.getString("FileStep2.rowsToSkipTip")); //$NON-NLS-1$

        // Header
        rowsToSkipHeaderCheckboxCombo = new LabelledCheckboxCombo(compositeRowsToSkip, Messages
                .getString("FileStep2.header"), //$NON-NLS-1$
                Messages.getString("FileStep2.headerTip"), STRING_NUMBERS_DATA, 1, true, SWT.NONE); //$NON-NLS-1$

        // Footer
        rowsToSkipFooterCheckboxCombo = new LabelledCheckboxCombo(compositeRowsToSkip, Messages
                .getString("FileStep2.footer"), //$NON-NLS-1$
                Messages.getString("FileStep2.footerTip"), STRING_NUMBERS_DATA, 1, true, SWT.NONE); //$NON-NLS-1$

        // Empty row
        emptyRowsToSkipCheckbox = new Button(compositeRowsToSkip, SWT.CHECK);
        emptyRowsToSkipCheckbox.setText(Messages.getString("FileStep2.removeEmptyRow")); //$NON-NLS-1$
        emptyRowsToSkipCheckbox.setAlignment(SWT.LEFT);
        gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        gridData.horizontalSpan = 3;
        emptyRowsToSkipCheckbox.setLayoutData(gridData);

    }

    /**
     * add Field to Group Escape Char.
     * 
     * @param mainComposite
     * @param form
     * @param width
     * @param height
     */
    private void addGroupEscapeChar(final Composite mainComposite, final int width, final int height) {

        // Composite Escape Char
        Group group = Form.createGroup(mainComposite, 2,
                Messages.getString("FileStep2.groupEscapeCharSettings"), height); //$NON-NLS-1$
        Composite compositeEscapeChar = Form.startNewDimensionnedGridLayout(group, 3, width, height);

        // CSV or Positionel Radio
        csvRadio = new Button(compositeEscapeChar, SWT.RADIO);
        csvRadio.setText(Messages.getString("FileStep2.csv")); //$NON-NLS-1$
        delimitedRadio = new Button(compositeEscapeChar, SWT.RADIO);
        delimitedRadio.setText(Messages.getString("FileStep2.delimited")); //$NON-NLS-1$
        GridData gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        gridData.horizontalSpan = 2;
        delimitedRadio.setLayoutData(gridData);

        // escape Char Combo
        escapeCharCombo = new LabelledCombo(compositeEscapeChar, Messages.getString("FileStep2.escapeChar"), Messages //$NON-NLS-1$
                .getString("FileStep2.escapeCharTip"), ESCAPE_CHAR_DATA, 1, false, SWT.READ_ONLY); //$NON-NLS-1$
        escapeCharFlag = new Label(compositeEscapeChar, SWT.NONE);
        escapeCharFlag.setText("                            "); //$NON-NLS-1$

        // Text Enclosure Combo
        textEnclosureCombo = new LabelledCombo(compositeEscapeChar,
                Messages.getString("FileStep2.textEnclosure"), Messages //$NON-NLS-1$
                        .getString("FileStep2.textEnclosureTip"), TEXT_ENCLOSURE_DATA, 1, false, SWT.READ_ONLY); //$NON-NLS-1$
        textEnclosureFlag = new Label(compositeEscapeChar, SWT.NONE);
        textEnclosureFlag.setText("                            "); //$NON-NLS-1$

    }

    /**
     * add field to Group Limit.
     * 
     * @param mainComposite
     * @param form
     * @param width
     * @param height
     */
    private void addGroupLimit(final Composite mainComposite, final int width, final int height) {
        // Composite Limited rows
        Group group = Form.createGroup(mainComposite, 2, Messages.getString("FileStep2.groupLimitOfRows"), height); //$NON-NLS-1$
        Composite compositeLimit = Form.startNewDimensionnedGridLayout(group, 3, width, height);

        // Information Limit
        Label info = new Label(compositeLimit, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        info.setLayoutData(gridData);
        info.setText(Messages.getString("FileStep2.groupLimitOfRowsTip")); //$NON-NLS-1$

        // Limit
        rowsToSkipLimitCheckboxCombo = new LabelledCheckboxCombo(compositeLimit,
                Messages.getString("FileStep2.limit"), Messages //$NON-NLS-1$
                        .getString("FileStep2.limitTip"), STRING_NUMBERS_DATA, 1, true, SWT.NONE); //$NON-NLS-1$
    }

    /**
     * add Field to Group File Viewer.
     * 
     * @param parent
     * @param form
     * @param width
     * @param height
     */
    private void addGroupFileViewer(final Composite parent, final int width, int height) {
        // composite Delimited File Preview
        previewGroup = Form.createGroup(parent, 1, Messages.getString("FileStep2.groupPreview"), height); //$NON-NLS-1$
        Composite compositeDelimitedFilePreviewButton = Form.startNewDimensionnedGridLayout(previewGroup, 4, width,
                HEIGHT_BUTTON_PIXEL);
        height = height - HEIGHT_BUTTON_PIXEL - 15;

        // Delimited File Preview Info
        firstRowIsCaptionCheckbox = new Button(compositeDelimitedFilePreviewButton, SWT.CHECK);
        firstRowIsCaptionCheckbox.setText(Messages.getString("FileStep2.firstRowsIsCaption")); //$NON-NLS-1$
        firstRowIsCaptionCheckbox.setAlignment(SWT.LEFT);

        previewButton = new Button(compositeDelimitedFilePreviewButton, SWT.NONE);
        previewButton.setText(Messages.getString("FileStep2.refreshPreview")); //$NON-NLS-1$
        previewButton.setSize(WIDTH_BUTTON_PIXEL, HEIGHT_BUTTON_PIXEL);

        // simple space
        new Label(compositeDelimitedFilePreviewButton, SWT.NONE);
        // Information Label
        previewInformationLabel = new Label(compositeDelimitedFilePreviewButton, SWT.NONE);
        previewInformationLabel
                .setText("                                                                                                                        "); //$NON-NLS-1$
        previewInformationLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));

        Composite compositeDelimitedFilePreview = Form.startNewDimensionnedGridLayout(previewGroup, 1, width, height);

        // Delimited File Preview
        delimitedFilePreview = new ShadowProcessPreview(compositeDelimitedFilePreview, null, width, height - 10);
        delimitedFilePreview.newTablePreview();
    }

    @Override
    protected void addFields() {

        // compositeFileDelimitor Main Fields
        Composite mainComposite = Form.startNewGridLayout(this, 2);

        addGroupDelimitedFileSettings(mainComposite, 400, 110);
        addGroupRowsToSkip(mainComposite, 300, 110);
        addGroupEscapeChar(mainComposite, 400, 85);
        addGroupLimit(mainComposite, 300, 85);
        addGroupFileViewer(this, 700, 210);

        if (!isInWizard()) {
            // Bottom Button
            Composite compositeBottomButton = Form.startNewGridLayout(this, 2, false, SWT.CENTER, SWT.CENTER);
            // Button Cancel
            cancelButton = new UtilsButton(compositeBottomButton,
                    Messages.getString("CommonWizard.cancel"), WIDTH_BUTTON_PIXEL, //$NON-NLS-1$
                    HEIGHT_BUTTON_PIXEL);
        }
    }

    /**
     * create ProcessDescription and set it.
     * 
     * WARNING ::field FieldSeparator, RowSeparator, EscapeChar and TextEnclosure are surround by double quote.
     * 
     * @param getConnection()
     * 
     * @return processDescription
     */
    private ProcessDescription getProcessDescription() {

        ProcessDescription processDescription = ShadowProcessHelper.getProcessDescription(getConnection());

        // Adapt Header width firstRowIsCaption to preview the first line on caption or not
        Integer i = 0;
        if (rowsToSkipHeaderCheckboxCombo.isInteger()) {
            i = new Integer(rowsToSkipHeaderCheckboxCombo.getText());
        }
        if (firstRowIsCaptionCheckbox.getSelection()) {
            i--;
        }
        processDescription.setHeaderRow(i);

        // adapt the limit to the preview
        processDescription.setLimitRows(maximumRowsToPreview);
        if (rowsToSkipLimitCheckboxCombo.isInteger()) {
            i = new Integer(rowsToSkipLimitCheckboxCombo.getText());
            if (firstRowIsCaptionCheckbox.getSelection()) {
                i++;
            }
            if (i < maximumRowsToPreview) {
                processDescription.setLimitRows(i);
            }
        }

        return processDescription;
    }

    /**
     * clear the table preview.
     */
    void clearPreview() {
        delimitedFilePreview.clearTablePreview();
    }

    /**
     * refreshPreview use ShadowProcess to refresh the preview.
     */
    void refreshPreview() {
        processor.execute();
    }

    /**
     * Main Fields addControls.
     */
    @Override
    protected void addFieldsListeners() {
        addFieldsListenersGroupDelimitedFileSettings();
        addFieldsListenersGroupsRowToSkipAndLimit();
        addFieldsListenersGroupsEscapeChar();
        addFieldsListenersGroupsFileViewer();
    }

    /**
     * add Controls to group File Viewer.
     */
    private void addFieldsListenersGroupsFileViewer() {
        // Manage rowsToSkipHeader when firstRowIsCaption is checked
        firstRowIsCaptionCheckbox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                getConnection().setFirstLineCaption(firstRowIsCaptionCheckbox.getSelection());

                if (firstRowIsCaptionCheckbox.getSelection()) {
                    // when firstRowIsCaption is checked
                    if (rowsToSkipHeaderCheckboxCombo.isEmpty()) {
                        // at least, rowsToSkipHeader = 1
                        rowsToSkipHeaderCheckboxCombo.setText("1"); //$NON-NLS-1$
                        getConnection().setHeaderValue(1);
                    } else {
                        // rowsToSkipHeader ++
                        int value = new Integer(rowsToSkipHeaderCheckboxCombo.getText());
                        value++;
                        String newValue = new String("" + value); //$NON-NLS-1$
                        rowsToSkipHeaderCheckboxCombo.setText(newValue);
                        getConnection().setHeaderValue(new Integer(rowsToSkipHeaderCheckboxCombo.getText()));
                    }
                } else {
                    // when firstRowIsCaption isn't checked
                    if (rowsToSkipHeaderCheckboxCombo.getText().equals("1")) { //$NON-NLS-1$
                        // rowsToSkipHeader is unusable
                        rowsToSkipHeaderCheckboxCombo.deselectAll();
                        getConnection().setHeaderValue(0);
                    } else {
                        // rowsToSkipHeader --
                        int value = new Integer(rowsToSkipHeaderCheckboxCombo.getText());
                        value--;
                        String newValue = new String("" + value); //$NON-NLS-1$
                        rowsToSkipHeaderCheckboxCombo.setText(newValue);
                        getConnection().setHeaderValue(new Integer(rowsToSkipHeaderCheckboxCombo.getText()));
                    }
                }
                checkFieldsValue();
            }
        });
    }

    /**
     * add Controls to group Escape Char.
     */
    private void addFieldsListenersGroupsEscapeChar() {
        // Radio csvRadio/delimitedRadio : Event modify
        ArrayList<Button> radio2Control = new ArrayList<Button>();
        radio2Control.add(csvRadio);
        radio2Control.add(delimitedRadio);

        Iterator<Button> iButton;
        Button button;

        for (iButton = radio2Control.iterator(); iButton.hasNext();) {
            button = iButton.next();
            // RADIO ONLY
            button.addSelectionListener(new SelectionListener() {

                String escapeCharComboOldValue = ""; //$NON-NLS-1$

                String textEnclosureComboOldValue = ""; //$NON-NLS-1$

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {
                    Boolean b = !(csvRadio.getSelection());
                    getConnection().setEscapeType(b ? Escape.DELIMITED_LITERAL : Escape.CSV_LITERAL);
                    textEnclosureCombo.setEnabled(!b);
                    escapeCharCombo.setEnabled(!b);
                    if (b) {
                        escapeCharComboOldValue = escapeCharCombo.getText();
                        textEnclosureComboOldValue = textEnclosureCombo.getText();
                        textEnclosureCombo.select(0);
                        escapeCharCombo.select(0);
                        textEnclosureFlag.setText("                 "); //$NON-NLS-1$
                        escapeCharFlag.setText("                 "); //$NON-NLS-1$
                        checkFieldsValue();
                    } else {
                        // select the old value to the two fields
                        if ((!"".equals(escapeCharComboOldValue)) && (!escapeCharComboOldValue.equals(EMPTY_VALUE))) { //$NON-NLS-1$
                            escapeCharCombo.setText(escapeCharComboOldValue);
                            setCharFlag(escapeCharFlag, escapeCharCombo.getText());
                        }
                        if ((!"".equals(textEnclosureComboOldValue)) && (!textEnclosureComboOldValue.equals(EMPTY_VALUE))) { //$NON-NLS-1$
                            textEnclosureCombo.setText(textEnclosureComboOldValue);
                            setCharFlag(textEnclosureFlag, textEnclosureCombo.getText());
                        }
                    }
                }
            });
        }

        // Radio and Checkbox: Event modify
        emptyRowsToSkipCheckbox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getConnection().setRemoveEmptyRow(emptyRowsToSkipCheckbox.getSelection());
                checkFieldsValue();
            }
        });

        // Escape Char Combo
        escapeCharCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                if (escapeCharCombo.getText() != null && !("").equals(escapeCharCombo.getText()) //$NON-NLS-1$
                        && !(EMPTY_VALUE).equals(escapeCharCombo.getText())) {
                    getConnection().setEscapeChar(escapeCharCombo.getText());
                } else {
                    getConnection().setEscapeChar(null);
                }
                checkFieldsValue();
            }
        });
        textEnclosureCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                if (textEnclosureCombo.getText() != null && !("").equals(textEnclosureCombo.getText()) //$NON-NLS-1$
                        && !(EMPTY_VALUE).equals(textEnclosureCombo.getText())) {
                    getConnection().setTextEnclosure(textEnclosureCombo.getText());
                } else {
                    getConnection().setTextEnclosure(null);
                }
                checkFieldsValue();
            }
        });
    }

    /**
     * DOC ocarbone Comment method "setCharFlag".
     * 
     * @param escapeCharFlag2
     * @param text
     */
    protected void setCharFlag(Label label, String string) {
        // PTODO OCA : when is utils in the Flag ?
        label.setText(string);
    }

    /**
     * add Controls to group Row To Skip and Limit.
     */
    private void addFieldsListenersGroupsRowToSkipAndLimit() {
        // Event modify
        ArrayList<LabelledCheckboxCombo> labelledCheckboxCombo2Control = new ArrayList<LabelledCheckboxCombo>();
        labelledCheckboxCombo2Control.add(rowsToSkipHeaderCheckboxCombo);
        labelledCheckboxCombo2Control.add(rowsToSkipLimitCheckboxCombo);
        labelledCheckboxCombo2Control.add(rowsToSkipFooterCheckboxCombo);

        Iterator<LabelledCheckboxCombo> iCheckboxCombo;
        LabelledCheckboxCombo labelledCheckboxCombo;

        // Event : keyPressed
        for (iCheckboxCombo = labelledCheckboxCombo2Control.iterator(); iCheckboxCombo.hasNext();) {
            labelledCheckboxCombo = iCheckboxCombo.next();

            labelledCheckboxCombo.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {

                    String string = String.valueOf(e.character);
                    // Check if input is number, backspace key and delete key of keyboard.
                    if (!(string.matches("[0-9]*")) && e.keyCode != 8 && e.keyCode != SWT.DEL) {
                        e.doit = false;
                    }
                }
            });
        }

        // Event : Modify (to control the use of Ctrl V)
        rowsToSkipHeaderCheckboxCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                if (!rowsToSkipHeaderCheckboxCombo.isEmpty()) {
                    if (!rowsToSkipHeaderCheckboxCombo.isInteger()
                            || rowsToSkipHeaderCheckboxCombo.getText().trim().equals("0")) { //$NON-NLS-1$
                        rowsToSkipHeaderCheckboxCombo.deselectAll();
                        getConnection().setUseHeader(rowsToSkipHeaderCheckboxCombo.isChecked());
                        getConnection().setHeaderValue(0);

                        updateStatus(IStatus.ERROR, "Number allowed only.");
                        rowsToSkipHeaderCheckboxCombo.getCombo().setFocus();

                        // if rowsHeaderToSkip isn't integer or is equals to 0, the firstRowIsCaptionCheckbox is
                        // unusable.
                        firstRowIsCaptionCheckbox.setSelection(false);
                        getConnection().setFirstLineCaption(false);
                        return;
                    } else {
                        getConnection().setHeaderValue(new Integer(rowsToSkipHeaderCheckboxCombo.getText().trim()));
                        getConnection().setUseHeader(rowsToSkipHeaderCheckboxCombo.isChecked());
                        checkFieldsValue();
                    }
                } else {
                    getConnection().setUseHeader(rowsToSkipHeaderCheckboxCombo.isChecked());
                    getConnection().setHeaderValue(0);
                    checkFieldsValue();
                }
            }
        });

        rowsToSkipFooterCheckboxCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                if (!rowsToSkipFooterCheckboxCombo.isEmpty()) {
                    if (!rowsToSkipFooterCheckboxCombo.isInteger()
                            || rowsToSkipFooterCheckboxCombo.getText().trim().equals("0")) { //$NON-NLS-1$
                        rowsToSkipFooterCheckboxCombo.deselectAll();
                        getConnection().setUseFooter(rowsToSkipFooterCheckboxCombo.isChecked());
                        getConnection().setFooterValue(0);

                        updateStatus(IStatus.ERROR, "Number allowed only.");
                        rowsToSkipFooterCheckboxCombo.getCombo().setFocus();
                    } else {
                        getConnection().setFooterValue(new Integer(rowsToSkipFooterCheckboxCombo.getText().trim()));
                    }
                } else {
                    getConnection().setUseFooter(rowsToSkipFooterCheckboxCombo.isChecked());
                    getConnection().setFooterValue(0);
                }
                checkFieldsValue();
            }
        });

        rowsToSkipLimitCheckboxCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                if (!rowsToSkipLimitCheckboxCombo.isEmpty()) {
                    if (!rowsToSkipLimitCheckboxCombo.isInteger()
                            || rowsToSkipLimitCheckboxCombo.getText().trim().equals("0")) { //$NON-NLS-1$
                        rowsToSkipLimitCheckboxCombo.deselectAll();
                        getConnection().setUseLimit(rowsToSkipLimitCheckboxCombo.isChecked());
                        getConnection().setLimitValue(0);

                        updateStatus(IStatus.ERROR, "Number allowed only.");
                        rowsToSkipLimitCheckboxCombo.getCombo().setFocus();
                    } else {
                        getConnection().setLimitValue(new Integer(rowsToSkipLimitCheckboxCombo.getText()));
                    }
                } else {
                    getConnection().setUseLimit(rowsToSkipLimitCheckboxCombo.isChecked());
                    getConnection().setLimitValue(0);
                }
                checkFieldsValue();
            }
        });

        // If nothing in rowsToSkipHeader, the firstRowIsCaption mustn't be checked
        rowsToSkipHeaderCheckboxCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if ((!rowsToSkipHeaderCheckboxCombo.isChecked())
                        || rowsToSkipHeaderCheckboxCombo.getText().trim().equals("0")) { //$NON-NLS-1$
                    firstRowIsCaptionCheckbox.setSelection(false);
                    getConnection().setFirstLineCaption(false);
                }
            }
        });

        // empty Rows To Skip
        emptyRowsToSkipCheckbox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                getConnection().setRemoveEmptyRow(emptyRowsToSkipCheckbox.getSelection());
            }
        });
    }

    /**
     * add Controls of Group Delimited File Settings.
     */
    private void addFieldsListenersGroupDelimitedFileSettings() {
        // Event encodingCombo
        encodingCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                getConnection().setEncoding(encodingCombo.getText());
                checkFieldsValue();
            }
        });

        // Separator Combo (field and row)
        fieldSeparatorCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                // Label Custom of fieldSeparatorText
                fieldSeparatorManager();
            }
        });
        rowSeparatorCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                // Label Custom of rowSeparatorText
                rowSeparatorManager();
            }
        });

        // Separator Text (field and row)
        fieldSeparatorText.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                getConnection().setFieldSeparatorValue(fieldSeparatorText.getText());
                checkFieldsValue();
            }
        });
        fieldSeparatorText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                // Boolean quoteIsEscape = false;
                // if ((e.character) == Character.valueOf('"')) {
                // Point selection = fieldSeparatorText.getSelection();
                // if (selection.x > 0) {
                // if (fieldSeparatorText.getText().substring(selection.x - 1, selection.x).equals("\\")) {
                // //$NON-NLS-1$
                // quoteIsEscape = true;
                // }
                // }
                // if (!quoteIsEscape) {
                // updateStatus(IStatus.ERROR, Messages.getString("FileStep2.quoteDelimitedTip")); //$NON-NLS-1$
                // }
                // e.doit = quoteIsEscape;
                // }
            }
        });

        rowSeparatorText.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                getConnection().setRowSeparatorValue(rowSeparatorText.getText());
                checkFieldsValue();
            }
        });
        rowSeparatorText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                // Boolean quoteIsEscape = false;
                // if ((e.character) == Character.valueOf('"')) {
                // Point selection = rowSeparatorText.getSelection();
                // if (selection.x > 0) {
                // if (rowSeparatorText.getText().substring(selection.x - 1, selection.x).equals("\\")) { //$NON-NLS-1$
                // quoteIsEscape = true;
                // }
                // }
                // if (!quoteIsEscape) {
                // updateStatus(IStatus.ERROR, Messages.getString("FileStep2.quoteDelimitedTip")); //$NON-NLS-1$
                // }
                // e.doit = quoteIsEscape;
                // }
            }
        });

    }

    /**
     * rowSeparator : Adapt Custom Label and set the field Text.
     */
    protected void rowSeparatorManager() {
        RowSeparator separator = RowSeparator.getByName(rowSeparatorCombo.getText());
        getConnection().setRowSeparatorType(separator);

        if (rowSeparatorCombo.getSelectionIndex() == 1) {
            // Adapt Custom Label
            rowSeparatorText.setLabelText(rowSeparatorCombo.getText());
            rowSeparatorText.setEditable(true);
            rowSeparatorText.forceFocus();
        } else {
            // set the Flag width the char value of the Combo
            // { "Standard EOL", "Custom String" };
            if (rowSeparatorCombo.getSelectionIndex() == 0) {
                if (getConnection().getFormat().toString().equals(FileFormat.MAC_LITERAL.getName())) {
                    rowSeparatorText.setText(TalendTextUtils.QUOTATION_MARK + "\\r" + TalendTextUtils.QUOTATION_MARK); //$NON-NLS-1$
                } else {
                    rowSeparatorText.setText(TalendTextUtils.QUOTATION_MARK + "\\n" + TalendTextUtils.QUOTATION_MARK); //$NON-NLS-1$
                }
            }
            // Init Custom Label
            rowSeparatorText.setLabelText(Messages.getString("FileStep2.correspondingCharacter")); //$NON-NLS-1$
            getConnection().setRowSeparatorValue(rowSeparatorText.getText());
            rowSeparatorText.setEditable(true);
        }
    }

    /**
     * fieldSeparator : Adapt Custom Label and set the field Text.
     */
    protected void fieldSeparatorManager() {
        FieldSeparator seperator = FieldSeparator.getByName(fieldSeparatorCombo.getText());
        getConnection().setFieldSeparatorType(seperator);

        if (fieldSeparatorCombo.getSelectionIndex() >= 5) {
            // Adapt Custom Label
            fieldSeparatorText.setLabelText(fieldSeparatorCombo.getText());
            fieldSeparatorText.setEditable(true);
            fieldSeparatorText.setText(getConnection().getFieldSeparatorValue());
            fieldSeparatorText.forceFocus();
        } else {
            // set the Flag width the char value of the Combo
            // { "Tabulation", "Semicolon", "Comma", "Space", "''(Alt 65, #A4)", "Custom ANSI", "Custom UTF8",
            switch (fieldSeparatorCombo.getSelectionIndex()) {
            case 0:
                fieldSeparatorText.setText(TalendTextUtils.QUOTATION_MARK + ";" + TalendTextUtils.QUOTATION_MARK); //$NON-NLS-1$
                break;
            case 1:
                fieldSeparatorText.setText(TalendTextUtils.QUOTATION_MARK + "," + TalendTextUtils.QUOTATION_MARK); //$NON-NLS-1$
                break;
            case 2:
                fieldSeparatorText.setText(TalendTextUtils.QUOTATION_MARK + ("\\t") + TalendTextUtils.QUOTATION_MARK); //$NON-NLS-1$
                break;
            case 3:
                fieldSeparatorText.setText(TalendTextUtils.QUOTATION_MARK + " " + TalendTextUtils.QUOTATION_MARK); //$NON-NLS-1$
                break;
            case 4:
                fieldSeparatorText.setText(TalendTextUtils.QUOTATION_MARK + "''" + TalendTextUtils.QUOTATION_MARK); //$NON-NLS-1$
                break;
            default:
                break;
            }

            // Init Custom Label
            getConnection().setFieldSeparatorValue(fieldSeparatorText.getText());
            fieldSeparatorText.setLabelText(Messages.getString("FileStep2.correspondingCharacter")); //$NON-NLS-1$
            fieldSeparatorText.setEditable(true);
        }
    }

    /**
     * Ensures that fields are set. Update checkEnable / use to checkConnection().
     * 
     * @return
     */
    @Override
    protected boolean checkFieldsValue() {
        previewInformationLabel.setText("   " + Messages.getString("FileStep2.settingsIncomplete")); //$NON-NLS-1$ //$NON-NLS-2$
         updateStatus(IStatus.OK, null);
         previewButton.setEnabled(false);

        // Separator Combo (field and row)
        if ("".equals(fieldSeparatorText.getText())) { //$NON-NLS-1$
            updateStatus(IStatus.ERROR, Messages.getString("FileStep2.fieldSeparatorAlert")); //$NON-NLS-1$
            return false;
        }
        if (fieldSeparatorText.getText().equals("\\") || fieldSeparatorText.getText().endsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
            updateStatus(IStatus.ERROR, Messages.getString("FileStep2.fieldSeparatorIncomplete")); //$NON-NLS-1$
            return false;
        }

        if ("".equals(rowSeparatorText.getText())) { //$NON-NLS-1$
            updateStatus(IStatus.ERROR, Messages.getString("FileStep2.rowSeparatorAlert")); //$NON-NLS-1$
            return false;
        }
        if (rowSeparatorText.getText().equals("\\") || rowSeparatorText.getText().endsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
            updateStatus(IStatus.ERROR, Messages.getString("FileStep2.rowSeparatorIncomplete")); //$NON-NLS-1$
            return false;
        }

        // Labelled Checkbox Combo (Row to Skip and Limit)
        ArrayList<LabelledCheckboxCombo> labelledCheckboxCombo2Control = new ArrayList<LabelledCheckboxCombo>();
        labelledCheckboxCombo2Control.add(rowsToSkipHeaderCheckboxCombo);
        labelledCheckboxCombo2Control.add(rowsToSkipLimitCheckboxCombo);
        labelledCheckboxCombo2Control.add(rowsToSkipFooterCheckboxCombo);

        Iterator<LabelledCheckboxCombo> iCheckboxCombo;
        LabelledCheckboxCombo labelledCheckboxCombo;

        for (iCheckboxCombo = labelledCheckboxCombo2Control.iterator(); iCheckboxCombo.hasNext();) {
            labelledCheckboxCombo = iCheckboxCombo.next();
            // if the checkbox is checked, check Numeric value
            if (labelledCheckboxCombo.getCheckbox().getSelection()) {
                if (labelledCheckboxCombo.getText() == "") { //$NON-NLS-1$
                    updateStatus(IStatus.ERROR, labelledCheckboxCombo.getLabelText()
                            + " " + Messages.getString("FileStep2.mustBePrecised")); //$NON-NLS-1$
                    return false;
                }
            }
        }

        // escape Char Combo
        if (escapeCharCombo.getText() == "") { // || escapeCharCombo.getText().equals("\\") || //$NON-NLS-1$
            // escapeCharCombo.getText().endsWith("\\")
            updateStatus(IStatus.ERROR, Messages.getString("FileStep2.escapeCharAlert")); //$NON-NLS-1$
            return false;
        }
        if (textEnclosureCombo.getText() == "") { // || textEnclosureCombo.getText().equals("\\") || //$NON-NLS-1$
            // textEnclosureCombo.getText().endsWith("\\")
            updateStatus(IStatus.ERROR, Messages.getString("FileStep2.textEnclosureAlert")); //$NON-NLS-1$
            return false;
        }

        previewInformationLabel.setText(""); //$NON-NLS-1$
        previewButton.setEnabled(true);
        updateStatus(IStatus.OK, null);
        return true;
    }

    /**
     * Subclass of SWTUIThreadProcessor to process the preview event. <br/>
     * 
     * $Id$
     * 
     */
    class PreviewProcessor extends SWTUIThreadProcessor {

        String previewInformationLabelMsg = null;

        CsvArray csvArray = null;

        ProcessDescription processDescription = null;

        boolean firstRowIsCatption = false;

        public boolean preProcessStart() {
            previewButton.setText(Messages.getString("FileStep2.stop"));

            clearPreview();

            // if no file, the process don't be executed
            if (getConnection().getFilePath() == null || getConnection().getFilePath().equals("")) { //$NON-NLS-1$
                previewInformationLabel.setText("   " + Messages.getString("FileStep2.filePathIncomplete")); //$NON-NLS-1$ //$NON-NLS-2$
                return false;
            }

            // if incomplete settings, , the process don't be executed
            if (!checkFieldsValue()) {
                previewInformationLabel.setText("   " + Messages.getString("FileStep2.settingsIncomplete")); //$NON-NLS-1$ //$NON-NLS-2$
                return false;
            }

            previewInformationLabel.setText("   " + Messages.getString("FileStep2.previewProgress")); //$NON-NLS-1$ //$NON-NLS-2$
            firstRowIsCatption = firstRowIsCaptionCheckbox.getSelection();
            processDescription = getProcessDescription();
            return true;
        }

        public void nonUIProcessInThread() {
            // get the XmlArray width an adapt ProcessDescription
            try {
                if (Escape.CSV_LITERAL.equals(getConnection().getEscapeType())) {

                    csvArray = ShadowProcessHelper.getCsvArray(processDescription, "FILE_CSV"); //$NON-NLS-1$
                    if (csvArray == null) {
                        previewInformationLabelMsg = "   " + Messages.getString("FileStep2.previewFailure"); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        previewInformationLabelMsg = "   " + Messages.getString("FileStep2.previewIsDone"); //$NON-NLS-1$ //$NON-NLS-2$
                        // refresh TablePreview on this step
                        previewInformationLabelMsg = ""; //$NON-NLS-1$
                    }
                } else {
                    csvArray = ShadowProcessHelper.getCsvArray(processDescription, "FILE_DELIMITED"); //$NON-NLS-1$
                    if (csvArray == null) {
                        previewInformationLabelMsg = "   " + Messages.getString("FileStep2.previewFailure"); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        previewInformationLabelMsg = "   " + Messages.getString("FileStep2.previewIsDone"); //$NON-NLS-1$ //$NON-NLS-2$

                        // refresh TablePreview on this step
                        previewInformationLabelMsg = ""; //$NON-NLS-1$
                    }
                }

            } catch (CoreException ex) {
                setException(ex);
                previewInformationLabelMsg = "   " + Messages.getString("FileStep2.previewFailure"); //$NON-NLS-1$ //$NON-NLS-2$
                log.error(Messages.getString("FileStep2.previewFailure") + " " + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        public void updateUIInThreadIfThreadIsCanceled() {
            if (!previewInformationLabel.isDisposed()) {
                previewInformationLabel.setText("");
            }
        }

        public void updateUIInThreadIfThreadIsNotCanceled() {
            if (previewInformationLabel.isDisposed()) {
                return;
            }
            previewInformationLabel.setText(previewInformationLabelMsg);
            if (getException() != null) {
                new ErrorDialogWidthDetailArea(getShell(), PID,
                        Messages.getString("FileStep2.previewFailure"), getException().getMessage()); //$NON-NLS-1$
            }
            if (csvArray != null) {
                delimitedFilePreview.refreshTablePreview(csvArray, firstRowIsCatption);
            }
        }

        public void updateUIInThreadIfThreadFinally() {
            if (!previewButton.isDisposed()) {
                previewButton.setText(Messages.getString("FileStep2.refreshPreview"));
                previewButton.setEnabled(true);

            }
        }

        public void postProcessCancle() {
            previewButton.setEnabled(false);
        }
    }

    /**
     * addButtonControls.
     * 
     * @param cancelButton
     */
    @Override
    protected void addUtilsButtonListeners() {

        // Event PreviewButton
        previewButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                processor.execute();
            }
        });

        if (cancelButton != null) {
            // Event CancelButton
            cancelButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    getShell().close();
                }
            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
     * 
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (super.isVisible()) {
            // Adapt the UI rowSeparator to the file format
            rowSeparatorManager();

            // Fields to the Group Delimited File Settings
            if (getConnection().getEncoding() != null && !getConnection().getEncoding().equals("")) { //$NON-NLS-1$
                encodingCombo.setText(getConnection().getEncoding());
            } else {
                encodingCombo.select(0);
            }

            // Refresh the preview width the adapted rowSeparator
            // If metadata exist, refreshMetadata
            if ((!"".equals(getConnection().getFilePath())) && (getConnection().getFilePath() != null)) { //$NON-NLS-1$
                refreshPreview();
            }
            if (isReadOnly() != readOnly) {
                adaptFormToReadOnly();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.IRefreshable#refresh()
     */
    public void refresh() {
        refreshPreview();
    }
}
