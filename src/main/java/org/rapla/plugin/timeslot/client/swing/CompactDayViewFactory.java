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
package org.rapla.plugin.timeslot.client.swing;

import java.awt.Component;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.Icon;

import org.rapla.RaplaResources;
import org.rapla.client.ReservationController;
import org.rapla.client.extensionpoints.ObjectMenuFactory;
import org.rapla.client.internal.RaplaClipboard;
import org.rapla.client.swing.InfoFactory;
import org.rapla.client.swing.MenuFactory;
import org.rapla.client.swing.SwingCalendarView;
import org.rapla.client.swing.extensionpoints.SwingViewFactory;
import org.rapla.client.swing.images.RaplaImages;
import org.rapla.client.swing.toolkit.DialogUI;
import org.rapla.client.swing.toolkit.DialogUI.DialogUiFactory;
import org.rapla.components.calendar.DateRenderer;
import org.rapla.components.iolayer.IOInterface;
import org.rapla.entities.domain.AppointmentFormater;
import org.rapla.entities.domain.permission.PermissionController;
import org.rapla.facade.CalendarModel;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.framework.logger.Logger;
import org.rapla.inject.Extension;
import org.rapla.plugin.timeslot.TimeslotPlugin;
import org.rapla.plugin.timeslot.TimeslotProvider;

@Singleton
@Extension(provides = SwingCalendarView.class, id = TimeslotPlugin.DAY_TIMESLOT)
public class CompactDayViewFactory implements SwingViewFactory
{
    private final Set<ObjectMenuFactory> objectMenuFactories;
    private final MenuFactory menuFactory;
    private final CalendarSelectionModel calendarSelectionModel;
    private final RaplaClipboard clipboard;
    private final TimeslotProvider timeslotProvider;
    private final ReservationController reservationController;
    private final InfoFactory infoFactory;
    private final RaplaImages raplaImages;
    private final DateRenderer dateRenderer;
    private final DialogUiFactory dialogUiFactory;
    private final PermissionController permissionController;
    private final ClientFacade facade;
    private final RaplaResources i18n;
    private final RaplaLocale raplaLocale;
    private final Logger logger;
    private final IOInterface ioInterface;
    private final AppointmentFormater appointmentFormater;

    @Inject
    public CompactDayViewFactory(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, Set<ObjectMenuFactory> objectMenuFactories,
            MenuFactory menuFactory, CalendarSelectionModel calendarSelectionModel, RaplaClipboard clipboard, TimeslotProvider timeslotProvider,
            ReservationController reservationController, InfoFactory infoFactory, RaplaImages raplaImages, DateRenderer dateRenderer,
            DialogUiFactory dialogUiFactory, PermissionController permissionController, IOInterface ioInterface, AppointmentFormater appointmentFormater)
    {
        this.facade = facade;
        this.i18n = i18n;
        this.raplaLocale = raplaLocale;
        this.logger = logger;
        this.objectMenuFactories = objectMenuFactories;
        this.menuFactory = menuFactory;
        this.calendarSelectionModel = calendarSelectionModel;
        this.clipboard = clipboard;
        this.timeslotProvider = timeslotProvider;
        this.reservationController = reservationController;
        this.infoFactory = infoFactory;
        this.raplaImages = raplaImages;
        this.dateRenderer = dateRenderer;
        this.dialogUiFactory = dialogUiFactory;
        this.permissionController = permissionController;
        this.ioInterface = ioInterface;
        this.appointmentFormater = appointmentFormater;
    }

    public SwingCalendarView createSwingView(CalendarModel model, boolean editable) throws RaplaException
    {
        return new SwingCompactDayCalendar(facade, i18n, raplaLocale, logger, model, editable, objectMenuFactories, menuFactory, calendarSelectionModel,
                clipboard, timeslotProvider, reservationController, infoFactory, raplaImages, dateRenderer, dialogUiFactory, permissionController, ioInterface, appointmentFormater);
    }

    public String getViewId()
    {
        return TimeslotPlugin.DAY_TIMESLOT;
    }

    public String getName()
    {
        return i18n.getString(TimeslotPlugin.DAY_TIMESLOT);
    }

    Icon icon;

    public Icon getIcon()
    {
        if (icon == null)
        {
            icon = RaplaImages.getIcon("/org/rapla/plugin/compactweekview/images/week_compact.png");
        }
        return icon;
    }

    public String getMenuSortKey()
    {
        return "B2";
    }

}
