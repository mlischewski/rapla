/*--------------------------------------------------------------------------*
 | Copyright (C) 2014 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.weekview.client.swing;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.Icon;

import org.rapla.RaplaResources;
import org.rapla.client.extensionpoints.ObjectMenuFactory;
import org.rapla.facade.CalendarModel;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.client.swing.MenuFactory;
import org.rapla.client.swing.SwingCalendarView;
import org.rapla.client.swing.extensionpoints.SwingViewFactory;
import org.rapla.client.swing.images.RaplaImages;
import org.rapla.components.calendar.DateRenderer;
import org.rapla.inject.Extension;
import org.rapla.plugin.weekview.WeekviewPlugin;

import java.util.Set;

@Extension(provides = SwingViewFactory.class,id = WeekviewPlugin.DAY_VIEW)
public class DayViewFactory extends RaplaComponent implements SwingViewFactory
{
    private final Set<ObjectMenuFactory> objectMenuFactories;
    private final MenuFactory menuFactory;
    private final Provider<DateRenderer> dateRendererProvider;
    private final RaplaResources resouces;

    @Inject
    public DayViewFactory(RaplaContext context, Set<ObjectMenuFactory> objectMenuFactories, MenuFactory menuFactory, RaplaResources resouces, Provider<DateRenderer> dateRendererProvider)
    {
        super( context );
        this.objectMenuFactories = objectMenuFactories;
        this.menuFactory = menuFactory;
        this.resouces = resouces;
        this.dateRendererProvider = dateRendererProvider;
    }


    public SwingCalendarView createSwingView(RaplaContext context, CalendarModel model, boolean editable) throws RaplaException
    {
        return new SwingDayCalendar( context, model, editable, objectMenuFactories, menuFactory, resouces, dateRendererProvider);
    }

    public String getViewId()
    {
        return WeekviewPlugin.DAY_VIEW;
    }

    public String getName()
    {
        return getString(WeekviewPlugin.DAY_VIEW);
    }

    Icon icon;
    public Icon getIcon()
    {
        if ( icon == null) {
            icon = RaplaImages.getIcon("/org/rapla/plugin/weekview/images/day.png");
        }
        return icon;
    }

    public String getMenuSortKey() {
        return "A";
    }


}

