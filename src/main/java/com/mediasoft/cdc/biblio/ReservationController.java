/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mediasoft.cdc.biblio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jakarta.faces.FacesException;
import com.mediasoft.cdc.biblio.util.JsfUtil;
import com.mediasoft.cdc.biblio.exceptions.NonexistentEntityException;
import com.mediasoft.cdc.biblio.util.PagingInfo;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.UserTransaction;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author inganzamarumpu
 */
@Named("reservation")
@RequestScoped
public class ReservationController{

    public ReservationController() {
        pagingInfo = new PagingInfo();
        converter = new ReservationConverter();
    }
    private Reservation reservation = null;
    private List<Reservation> reservationItems = null;
    private ReservationJpaController jpaController = null;
    private ReservationConverter converter = null;
    private PagingInfo pagingInfo = null;
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    public PagingInfo getPagingInfo() {
        if (pagingInfo.getItemCount() == -1) {
            pagingInfo.setItemCount(getJpaController().getReservationCount());
        }
        return pagingInfo;
    }
    
    public boolean hasPrevious(){
        return pagingInfo.getLastItem() + pagingInfo.getBatchSize() <= pagingInfo.getItemCount();
    }
    
    public boolean hasNext(){
        return pagingInfo.getLastItem() < pagingInfo.getItemCount() && pagingInfo.getLastItem() + pagingInfo.getBatchSize() > pagingInfo.getItemCount();
    }

    public ReservationJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new ReservationJpaController(utx, emf);
        }
        return jpaController;
    }

    public SelectItem[] getReservationItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(getJpaController().findReservationEntities(), false);
    }

    public SelectItem[] getReservationItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findReservationEntities(), true);
    }

    public Reservation getReservation() {
        if (reservation == null) {
            reservation = (Reservation) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentReservation", converter, null);
        }
        if (reservation == null) {
            reservation = new Reservation();
        }
        return reservation;
    }

    public String listSetup() {
        reset(true);
        return "reservation_list";
    }

    public String createSetup() {
        reset(false);
        reservation = new Reservation();
        return "reservation_create";
    }

    public String create() {
        try {
            getJpaController().create(reservation);
            JsfUtil.addSuccessMessage("Reservation was successfully created.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return listSetup();
    }

    public String detailSetup() {
        return scalarSetup("reservation_detail");
    }

    public String editSetup() {
        return scalarSetup("reservation_edit");
    }

    private String scalarSetup(String destination) {
        reset(false);
        reservation = (Reservation) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentReservation", converter, null);
        if (reservation == null) {
            String requestReservationString = JsfUtil.getRequestParameter("jsfcrud.currentReservation");
            JsfUtil.addErrorMessage("The reservation with id " + requestReservationString + " no longer exists.");
            return relatedOrListOutcome();
        }
        return destination;
    }

    public String edit() {
        String reservationString = converter.getAsString(FacesContext.getCurrentInstance(), null, reservation);
        String currentReservationString = JsfUtil.getRequestParameter("jsfcrud.currentReservation");
        if (reservationString == null || reservationString.length() == 0 || !reservationString.equals(currentReservationString)) {
            String outcome = editSetup();
            if ("reservation_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit reservation. Try again.");
            }
            return outcome;
        }
        try {
            getJpaController().edit(reservation);
            JsfUtil.addSuccessMessage("Reservation was successfully updated.");
        } catch (NonexistentEntityException ne) {
            JsfUtil.addErrorMessage(ne.getLocalizedMessage());
            return listSetup();
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return detailSetup();
    }

    public String destroy() {
        String idAsString = JsfUtil.getRequestParameter("jsfcrud.currentReservation");
        Integer id = new Integer(idAsString);
        try {
            getJpaController().destroy(id);
            JsfUtil.addSuccessMessage("Reservation was successfully deleted.");
        } catch (NonexistentEntityException ne) {
            JsfUtil.addErrorMessage(ne.getLocalizedMessage());
            return relatedOrListOutcome();
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return relatedOrListOutcome();
    }

    private String relatedOrListOutcome() {
        String relatedControllerOutcome = relatedControllerOutcome();
        if (relatedControllerOutcome != null) {
            return relatedControllerOutcome;
        }
        return listSetup();
    }

    public List<Reservation> getReservationItems() {
        if (reservationItems == null) {
            getPagingInfo();
            reservationItems = getJpaController().findReservationEntities(pagingInfo.getBatchSize(), pagingInfo.getFirstItem());
        }
        return reservationItems;
    }

    public String next() {
        reset(false);
        getPagingInfo().nextPage();
        return "reservation_list";
    }

    public String prev() {
        reset(false);
        getPagingInfo().previousPage();
        return "reservation_list";
    }

    private String relatedControllerOutcome() {
        String relatedControllerString = JsfUtil.getRequestParameter("jsfcrud.relatedController");
        String relatedControllerTypeString = JsfUtil.getRequestParameter("jsfcrud.relatedControllerType");
        if (relatedControllerString != null && relatedControllerTypeString != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            Object relatedController = context.getApplication().getELResolver().getValue(context.getELContext(), null, relatedControllerString);
            try {
                Class<?> relatedControllerType = Class.forName(relatedControllerTypeString);
                Method detailSetupMethod = relatedControllerType.getMethod("detailSetup");
                return (String) detailSetupMethod.invoke(relatedController);
            } catch (ClassNotFoundException e) {
                throw new FacesException(e);
            } catch (NoSuchMethodException e) {
                throw new FacesException(e);
            } catch (IllegalAccessException e) {
                throw new FacesException(e);
            } catch (InvocationTargetException e) {
                throw new FacesException(e);
            }
        }
        return null;
    }

    private void reset(boolean resetFirstItem) {
        reservation = null;
        reservationItems = null;
        pagingInfo.setItemCount(-1);
        if (resetFirstItem) {
            pagingInfo.setFirstItem(0);
        }
    }

    public void validateCreate(FacesContext facesContext, UIComponent component, Object value) {
        Reservation newReservation = new Reservation();
        String newReservationString = converter.getAsString(FacesContext.getCurrentInstance(), null, newReservation);
        String reservationString = converter.getAsString(FacesContext.getCurrentInstance(), null, reservation);
        if (!newReservationString.equals(reservationString)) {
            createSetup();
        }
    }

    public Converter getConverter() {
        return converter;
    }
    
}
