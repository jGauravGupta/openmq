/*
 * Copyright (c) 2000, 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * @(#)ObjStorePropsDialog.java	1.17 06/27/07
 */

package com.sun.messaging.jmq.admin.apps.console;

import java.awt.Frame;
import javax.swing.JOptionPane;

import com.sun.messaging.jmq.admin.util.Globals;
import com.sun.messaging.jmq.admin.resources.AdminConsoleResources;
import com.sun.messaging.jmq.admin.apps.console.event.ObjAdminEvent;
import com.sun.messaging.jmq.admin.objstore.ObjStore;
import com.sun.messaging.jmq.admin.objstore.ObjStoreAttrs;

/**
 * The inspector component of the admin GUI displays attributes of the currently selected item. It does not know or care
 * about what is currently selected. It is basically told what to display i.e. what object's attributes to display.
 * <P>
 * There are a variety of objects that can be <EM>inspected</EM>:
 * <UL>
 * <LI>Collection of object stores
 * <LI>Individual object stores
 * <LI>Collection of brokers
 * <LI>Individual brokers
 * <LI>Collection of services
 * <LI>Individual services
 * <LI>Collection of Topics
 * <LI>Individual Topics
 * <LI>Collection of Queues
 * <LI>Individual Queues
 * <LI>
 * </UL>
 *
 * For each of the object types above, a different inspector panel is potentially needed for displaying the object's
 * attributes. This will be implemented by having a main panel stacking all the different property panels in CardLayout.
 * For each object that needs to be inspected, the object needs to be passed in to the inspector as well as it's type.
 */
public class ObjStorePropsDialog extends ObjStoreDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -8353001964928282780L;
    private static AdminConsoleResources acr = Globals.getAdminConsoleResources();
    private static String close[] = { acr.getString(acr.I_DIALOG_CLOSE) };
    private ObjStoreCObj osCObj;

    public ObjStorePropsDialog(Frame parent, ObjStoreListCObj oslCObj) {
        super(parent, acr.getString(acr.I_OBJSTORE_PROPS), (OK | CANCEL | CLOSE | HELP), oslCObj);
        setHelpId(ConsoleHelpID.OBJECT_STORE_PROPS);
    }

    @Override
    public void doOK() {

        String osName = null;

        // if (osTextButton.isSelected()) {

        osName = osText.getText();
        osName = osName.trim();
        //
        // Make sure store name is not empty.
        //
        if (osName.equals("")) {
            JOptionPane.showOptionDialog(this, acr.getString(acr.E_NO_OBJSTORE_NAME),
                    acr.getString(acr.I_OBJSTORE_PROPS) + ": " + acr.getString(acr.I_ERROR_CODE, AdminConsoleResources.E_NO_OBJSTORE_NAME),
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, close, close[0]);
            osText.requestFocus();
            return;
        }
        /*
         * } else if (urlButton.isSelected()) { // Make sure a provider.url property was set. osName =
         * jndiProps.getProperty(Context.PROVIDER_URL); if (osName == null || osName.equals("")) {
         * JOptionPane.showOptionDialog(this, acr.getString(acr.E_NO_PROVIDER_URL, Context.PROVIDER_URL),
         * acr.getString(acr.I_OBJSTORE_PROPS) + ": " + acr.getString(acr.I_ERROR_CODE,
         * AdminConsoleResources.E_NO_PROVIDER_URL), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, close,
         * close[0]); comboBox.setSelectedItem(Context.PROVIDER_URL); valueText.requestFocus(); return; }
         *
         * }
         */

        ObjAdminEvent oae;
        ObjStoreAttrs osa = constructAttrs(osName);

        if (osa == null) {
            return;
        }

        oae = new ObjAdminEvent(this, ObjAdminEvent.UPDATE_OBJSTORE);
        oae.setObjStoreID(osName);
        oae.setObjStore((this.osCObj).getObjStore());
        oae.setObjStoreAttrs(osa);
        // oae.setConnectAttempt(checkBox.isSelected());
        oae.setConnectAttempt(false);
        oae.setOKAction(true);
        fireAdminEventDispatched(oae);
    }

    @Override
    public void doApply() {
    }

    @Override
    public void doReset() {
    }

    @Override
    public void doCancel() {
        hide();
    }

    @Override
    public void doClose() {
        hide();
    }

    @Override
    public void doClear() {
    }

    public void show(ObjStoreCObj osCObj) {
        this.osCObj = osCObj;
        ObjStore os = osCObj.getObjStore();

        checkBox.setSelected(true);

        ObjStoreAttrs attrs = os.getObjStoreAttrs();
        jndiProps.clear();
        if (attrs != null) {
            for (java.util.Enumeration e = attrs.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                jndiProps.setProperty(key, (String) attrs.get(key));
            }
            model.fireTableRowsInserted(0, attrs.size() - 1);
            // Select the first one in the list.
            if (attrs.size() >= 1) {
                table.setRowSelectionInterval(0, 0);
            }
        } else {
            comboBox.setSelectedIndex(0);
            delButton.setEnabled(false);
            chgButton.setEnabled(false);
            model.fireTableDataChanged();
            valueText.requestFocus();
        }

        // If Provider URL = object store name, then
        // select provider url button.
        // Otherwise, select use own name button and fill in.
        //
        /*
         * String urlName = jndiProps.getProperty(Context.PROVIDER_URL); if (urlName != null && urlName.equals(os.getID())) {
         * urlButton.setSelected(true); doUrlButton(); osText.setText(""); urlButton.requestFocus(); } else {
         * osTextButton.setSelected(true); doOsTextButton(); osText.setText(os.getID()); osTextButton.requestFocus(); }
         */
        osText.setText(os.getID());
        if (os.isOpen()) {
            setEditable(false);
        } else {
            setEditable(true);
        }

        super.show();
    }

    private ObjStoreAttrs constructAttrs(String osName) {

        ObjStore os = osCObj.getObjStore();
        //
        // If they changed the os name to something else,
        // make sure it doesn't already exist.
        //
        String prevOsName = os.getID();

        if (!prevOsName.equals(osName) && // they changed it
                osMgr != null && osMgr.getStore(osName) != null) {

            JOptionPane.showOptionDialog(this, acr.getString(acr.E_OBJSTORE_NAME_IN_USE, osName),
                    acr.getString(acr.I_OBJSTORE_PROPS) + ": " + acr.getString(acr.I_ERROR_CODE, AdminConsoleResources.E_OBJSTORE_NAME_IN_USE),
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, close, close[0]);

            osText.requestFocus();
            osText.selectAll();

            return (null);
        }

        ObjStoreAttrs osa = new ObjStoreAttrs(osName, osName);

        if (jndiProps == null) {
            return (osa);
        }

        // Check for any properties that MUST be set.
        if (checkMandatoryProps() == 0) {
            return null;
        }

        for (java.util.Enumeration e = jndiProps.propertyNames(); e.hasMoreElements();) {
            String propName = (String) e.nextElement();
            osa.put(propName, jndiProps.getProperty(propName));
        }

        return (osa);
    }

    @Override
    protected void setEditable(boolean editable) {

        if (editable) {
            okButton.setVisible(true);
            closeButton.setVisible(false);
            cancelButton.setVisible(true);
            buttonPanel.doLayout();

        } else {
            okButton.setVisible(false);
            closeButton.setVisible(true);
            cancelButton.setVisible(false);
            buttonPanel.doLayout();
        }

        super.setEditable(editable);
    }
}
