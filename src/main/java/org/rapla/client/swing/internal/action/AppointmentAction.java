/*--------------------------------------------------------------------------*
 | Copyright (C) 2014 Christopher Kohlhaas, Bettina Lademann                |
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
package org.rapla.client.swing.internal.action;
import java.awt.Component;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.rapla.client.ReservationController;
import org.rapla.client.ReservationEdit;
import org.rapla.entities.User;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.AppointmentBlock;
import org.rapla.entities.domain.permission.PermissionController;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.client.PopupContext;
import org.rapla.client.swing.InfoFactory;
import org.rapla.client.swing.RaplaAction;
import org.rapla.client.swing.toolkit.DialogUI;

public class AppointmentAction extends RaplaAction {
    public final static int DELETE = 1;
    public final static int COPY = 2;
    public final static int PASTE = 3;
    public final static int CUT = 4;
    public final static int EDIT = 6;
    public final static int VIEW = 7;
    public final static int CHANGE_ALLOCATABLE = 8;
    public final static int ADD_TO_RESERVATION = 9;
    public final static int PASTE_AS_NEW = 10;
    public final static int DELETE_SELECTION = 11;
    
    PopupContext popupContext;
    int type;
    AppointmentBlock appointmentBlock;

	ReservationEdit reservationEdit;
//    ReservationWizard wizard;
	private Collection<Allocatable> contextAllocatables;
	private final CalendarSelectionModel calendarSelectionModel;
    private final ReservationController reservationController;
    private final InfoFactory<Component, DialogUI> infoFactory;
    
	public AppointmentAction(RaplaContext context,PopupContext popupContext, CalendarSelectionModel calendarSelectionModel, ReservationController reservationController, InfoFactory<Component, DialogUI> infoFactory)
    {
        super( context);
        this.popupContext = popupContext;
        this.calendarSelectionModel = calendarSelectionModel;
        this.reservationController = reservationController;
        this.infoFactory = infoFactory;
    }

    public AppointmentAction setAddTo(ReservationEdit reservationEdit) 
    {
    	this.reservationEdit = reservationEdit;
        this.type = ADD_TO_RESERVATION;
        String name2 = getName(reservationEdit.getReservation());
		
        String value = name2.trim().length() > 0 ? "'" + name2 + "'" : getString("new_reservation");
		putValue(NAME, value);
        putValue(SMALL_ICON, getIcon("icon.new"));
        boolean canAllocate = canAllocate();
        setEnabled( canAllocate);
        return this;
    }

    public AppointmentAction setCopy(AppointmentBlock appointmentBlock, Collection<Allocatable> contextAllocatables) {
        this.appointmentBlock = appointmentBlock;
        this.type = COPY;
        this.contextAllocatables = contextAllocatables;
        putValue(NAME, getString("copy"));
        putValue(SMALL_ICON, getIcon("icon.copy"));
        setEnabled(canCreateReservation());
        return this;
    }

    public AppointmentAction setCut(AppointmentBlock appointmentBlock, Collection<Allocatable> contextAllocatables) {
        this.appointmentBlock = appointmentBlock;
        this.type = CUT;
        this.contextAllocatables = contextAllocatables;
        putValue(NAME, getString("cut"));
        putValue(SMALL_ICON, getIcon("icon.cut"));
        setEnabled(canCreateReservation());
        return this;
    }

    
    public AppointmentAction setPaste( ) {
        this.type = PASTE;
        putValue(NAME, getString("paste_into_existing_event"));
        putValue(SMALL_ICON, getIcon("icon.paste"));
        setEnabled(isAppointmentOnClipboard() && canCreateReservation());
        return this;
    }

    public AppointmentAction setPasteAsNew( ) {
        this.type = PASTE_AS_NEW;
        putValue(NAME, getString("paste_as") + " " + getString( "new_reservation" ) );
        putValue(SMALL_ICON, getIcon("icon.paste_new"));
        setEnabled(isAppointmentOnClipboard() && canCreateReservation());
        return this;
    }

    /**
     * Context menu entry to delete an appointment.
     */
    public AppointmentAction setDelete(AppointmentBlock appointmentBlock){
    	this.appointmentBlock = appointmentBlock;
    	Appointment appointment = appointmentBlock.getAppointment();
    	this.type = DELETE;
    	putValue(NAME, getI18n().format("delete.format", getString("appointment")));
    	putValue(SMALL_ICON, getIcon("icon.delete"));
    	setEnabled(canModify(appointment.getReservation()));
    	return this;
    }
    
    public AppointmentAction setDeleteSelection(Collection<AppointmentBlock> selection) {
        this.type = DELETE_SELECTION;
        putValue(NAME, getString("delete_selection"));
        putValue(SMALL_ICON, getIcon("icon.delete"));
        changeSelection( selection );
        return this;
    }

    Collection<AppointmentBlock> blockList;
    
    private void changeSelection(Collection<AppointmentBlock> blockList) {
    	
    	this.blockList = blockList;
    	if (type == DELETE_SELECTION) {
        	boolean enabled = true;
             if (blockList != null && blockList.size() > 0 ) {
                 Iterator<AppointmentBlock> it = blockList.iterator();
                 while (it.hasNext()) {
                     if (!canModify(it.next().getAppointment().getReservation())){
                         enabled = false;
                         break;
                     }
                 }
             } else {
                 enabled = false;
             }
             setEnabled(enabled);		
         }
	}

	public AppointmentAction setView(AppointmentBlock appointmentBlock) {
        this.appointmentBlock = appointmentBlock;
        Appointment appointment = appointmentBlock.getAppointment();
        this.type = VIEW;
        putValue(NAME, getString("view"));
        putValue(SMALL_ICON, getIcon("icon.help"));
        try 
        {
        	User user = getUser();
            boolean canRead = canRead(appointment, user, getEntityResolver(), permissionController);
            setEnabled( canRead);
        } 
        catch (RaplaException ex)
        {
            getLogger().error( "Can't get user",ex);
        }
        return this;
    }

    public AppointmentAction setEdit(AppointmentBlock appointmentBlock) {
        this.appointmentBlock = appointmentBlock;
        this.type = EDIT;
        putValue(SMALL_ICON, getIcon("icon.edit"));
        Appointment appointment = appointmentBlock.getAppointment();
        boolean canExchangeAllocatables = getQuery().canExchangeAllocatables(appointment.getReservation());
		boolean canModify = canModify(appointment.getReservation());
		String text = !canModify && canExchangeAllocatables ?  getString("exchange_allocatables") : getString("edit");
		putValue(NAME, text);
		setEnabled(canModify || canExchangeAllocatables );
        return this;
    }

    public void actionPerformed() {
        try {
            switch (type) {
            case DELETE: delete();break;
            case COPY: copy();break;
            case CUT: cut();break;
            case PASTE: paste(false);break;
            case PASTE_AS_NEW: paste( true);break;
          //  case NEW: newReservation();break;
            case ADD_TO_RESERVATION: addToReservation();break;
            case EDIT: edit();break;
            case VIEW: view();break;
            case DELETE_SELECTION: deleteSelection();break;
            }
        } catch (RaplaException ex) {
            showError(ex,popupContext);
        } // end of try-catch
    }
    
    protected ReservationController getReservationController()
    {
        return reservationController;
    }

    private void deleteSelection() throws RaplaException {
    	if ( this.blockList == null){
    		return;
    	}
    	 getReservationController().deleteBlocks(blockList,popupContext);
	}

    public void view() throws RaplaException {
        Appointment appointment = appointmentBlock.getAppointment();
    	infoFactory.showInfoDialog(appointment.getReservation(), popupContext);
    }

    public void edit() throws RaplaException {
        getReservationController().edit( appointmentBlock);
    }

    private void delete() throws RaplaException {
        getReservationController().deleteAppointment(appointmentBlock,popupContext);
    }

    private void copy() throws RaplaException 
    {
       getReservationController().copyAppointment(appointmentBlock,popupContext, contextAllocatables);
    }
    
    private void cut() throws RaplaException 
    {
       getReservationController().cutAppointment(appointmentBlock,popupContext, contextAllocatables);
    }

    private void paste(boolean asNewReservation) throws RaplaException {
        
		ReservationController reservationController = getReservationController();
    	Date start = getStartDate(calendarSelectionModel);
    	boolean keepTime = !calendarSelectionModel.isMarkedIntervalTimeEnabled();
    	reservationController.pasteAppointment(	start
                                               ,popupContext
                                               ,asNewReservation, keepTime);
    }

    private void addToReservation() throws RaplaException 
    {
    	Date start = getStartDate(calendarSelectionModel);
    	Date end = getEndDate(calendarSelectionModel, start);
    	reservationEdit.addAppointment(start,end);
    }

    public boolean isAppointmentOnClipboard() {
        return (getReservationController().isAppointmentOnClipboard());
    }
    



}
