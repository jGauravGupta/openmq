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

package com.sun.messaging.visualvm.dataview;

import java.awt.Image;

import javax.management.MBeanServerConnection;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;

import org.openide.util.ImageUtilities;

import com.sun.messaging.visualvm.datasource.ClusterAccessUtils;
import com.sun.messaging.visualvm.datasource.DestinationsDataSource;
import com.sun.messaging.visualvm.ui.DestinationConfigDestinationList;
import com.sun.messaging.visualvm.ui.DestinationConfigList;
import com.sun.messaging.visualvm.ui.DestinationMonitorDestinationList;
import com.sun.messaging.visualvm.ui.DestinationMonitorList;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import com.sun.tools.visualvm.tools.jmx.JmxModel;
import com.sun.tools.visualvm.tools.jmx.JmxModelFactory;

public class DestinationsView extends DataSourceView {

    private DataViewComponent dvc;
    //Make sure there is an image at this location in your project:
    private static final Image NODE_ICON = ImageUtilities.loadImage(
            "com/sun/messaging/visualvm/ui/resources/folder.gif", true);

    public DestinationsView(DestinationsDataSource dds) {
        // isCloseable=true
        super(dds, "Destinations", NODE_ICON, 60, true);
    }

    @Override
	protected DataViewComponent createComponent() {
        DestinationsDataSource ds = (DestinationsDataSource)getDataSource();
        Application app = ds.getApplication();

        MBeanServerConnection mbsc = ClusterAccessUtils.getMBeanServerConnection(app);

        // create the data area for the master view
        JEditorPane generalDataArea = new JEditorPane();
        generalDataArea.setBorder(BorderFactory.createEmptyBorder(14, 8, 14, 8));
        
        // create the master view
        DataViewComponent.MasterView masterView = new DataViewComponent.MasterView("Destination", null, generalDataArea);
        
        // create the configuration for the master view
        boolean isMasterAreaResizable = false;
        DataViewComponent.MasterViewConfiguration masterConfiguration = new DataViewComponent.MasterViewConfiguration(isMasterAreaResizable);
           
        // Add the master view and configuration view to the component:
        dvc = new DataViewComponent(masterView, masterConfiguration);
 
        // 1. create the JPanel for the "Destination manager (monitor)" detail view
        DestinationMonitorList destinationMonitorList = new DestinationMonitorList(dvc);
        destinationMonitorList.setMBeanServerConnection(mbsc);

        // add the "Destination manager (monitor)" detail view to the data view component representing the master view  
        // DataViewComponent.TOP_LEFT
        dvc.addDetailsView(new DataViewComponent.DetailsView("Destination manager (monitor)", null, 10, destinationMonitorList, null), destinationMonitorList.getCorner());	
 
        // 2. create the JPanel for the "Destination manager (config)" detail view
        DestinationConfigList destinationConfigList = new DestinationConfigList(dvc);
        destinationConfigList.setMBeanServerConnection(mbsc);

        // add the "Destination manager (config)" detail view to the data view component representing the master view  
        // DataViewComponent.TOP_RIGHT
        dvc.addDetailsView(new DataViewComponent.DetailsView("Destination manager (config)", null, 10, destinationConfigList, null), destinationConfigList.getCorner());
        
        // 3. create the JPanel for the "Destination list (monitor)" detail view
        DestinationMonitorDestinationList destinationMonitorDestinationList = new DestinationMonitorDestinationList(dvc);
        destinationMonitorDestinationList.setMBeanServerConnection(mbsc);

        // add the "Destination list (monitor)" detail view to the data view component representing the master view  
        // DataViewComponent.BOTTOM_LEFT
        dvc.addDetailsView(new DataViewComponent.DetailsView("Destination list (monitor)", null, 10, destinationMonitorDestinationList, null), destinationMonitorDestinationList.getCorner());	
        
        // 4. create the JPanel for the "Destination list (config)" detail view
        DestinationConfigDestinationList destinationConfigDestinationList = new DestinationConfigDestinationList(dvc);
        destinationConfigDestinationList.setMBeanServerConnection(mbsc);

        // add the "Destination list (config)" detail view to the data view component representing the master view  
        // DataViewComponent.BOTTOM_RIGHT
        dvc.addDetailsView(new DataViewComponent.DetailsView("Destination list (config)", null, 10, destinationConfigDestinationList, null), destinationConfigDestinationList.getCorner() );   
        
        return dvc;

    }

}
