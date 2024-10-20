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
import java.util.List;

/**
 *
 * @author inganzamarumpu
 */
@Named("emprunt")
@RequestScoped
public class EmpruntController {

    public EmpruntController() {
        pagingInfo = new PagingInfo();
        converter = new EmpruntConverter();
    }
    private Emprunt emprunt = null;
    private List<Emprunt> empruntItems = null;
    private EmpruntJpaController jpaController = null;
    private EmpruntConverter converter = null;
    private PagingInfo pagingInfo = null;
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    public PagingInfo getPagingInfo() {
        if (pagingInfo.getItemCount() == -1) {
            pagingInfo.setItemCount(getJpaController().getEmpruntCount());
        }
        return pagingInfo;
    }
    
    public boolean hasPrevious(){
        return pagingInfo.getLastItem() + pagingInfo.getBatchSize() <= pagingInfo.getItemCount();
    }
    
    public boolean hasNext(){
        return pagingInfo.getLastItem() < pagingInfo.getItemCount() && pagingInfo.getLastItem() + pagingInfo.getBatchSize() > pagingInfo.getItemCount();
    }

    public EmpruntJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new EmpruntJpaController(utx, emf);
        }
        return jpaController;
    }

    public SelectItem[] getEmpruntItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(getJpaController().findEmpruntEntities(), false);
    }

    public SelectItem[] getEmpruntItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findEmpruntEntities(), true);
    }

    public Emprunt getEmprunt() {
        if (emprunt == null) {
            emprunt = (Emprunt) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentEmprunt", converter, null);
        }
        if (emprunt == null) {
            emprunt = new Emprunt();
        }
        return emprunt;
    }

    public String listSetup() {
        reset(true);
        return "emprunt_list";
    }

    public String createSetup() {
        reset(false);
        emprunt = new Emprunt();
        return "emprunt_create";
    }

    public String create() {
        try {
            getJpaController().create(emprunt);
            JsfUtil.addSuccessMessage("Emprunt was successfully created.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return listSetup();
    }

    public String detailSetup() {
        return scalarSetup("emprunt_detail");
    }

    public String editSetup() {
        return scalarSetup("emprunt_edit");
    }

    private String scalarSetup(String destination) {
        reset(false);
        emprunt = (Emprunt) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentEmprunt", converter, null);
        if (emprunt == null) {
            String requestEmpruntString = JsfUtil.getRequestParameter("jsfcrud.currentEmprunt");
            JsfUtil.addErrorMessage("The emprunt with id " + requestEmpruntString + " no longer exists.");
            return relatedOrListOutcome();
        }
        return destination;
    }

    public String edit() {
        String empruntString = converter.getAsString(FacesContext.getCurrentInstance(), null, emprunt);
        String currentEmpruntString = JsfUtil.getRequestParameter("jsfcrud.currentEmprunt");
        if (empruntString == null || empruntString.length() == 0 || !empruntString.equals(currentEmpruntString)) {
            String outcome = editSetup();
            if ("emprunt_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit emprunt. Try again.");
            }
            return outcome;
        }
        try {
            getJpaController().edit(emprunt);
            JsfUtil.addSuccessMessage("Emprunt was successfully updated.");
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
        String idAsString = JsfUtil.getRequestParameter("jsfcrud.currentEmprunt");
        Long id = new Long(idAsString);
        try {
            getJpaController().destroy(id);
            JsfUtil.addSuccessMessage("Emprunt was successfully deleted.");
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

    public List<Emprunt> getEmpruntItems() {
        if (empruntItems == null) {
            getPagingInfo();
            empruntItems = getJpaController().findEmpruntEntities(pagingInfo.getBatchSize(), pagingInfo.getFirstItem());
        }
        return empruntItems;
    }

    public String next() {
        reset(false);
        getPagingInfo().nextPage();
        return "emprunt_list";
    }

    public String prev() {
        reset(false);
        getPagingInfo().previousPage();
        return "emprunt_list";
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
        emprunt = null;
        empruntItems = null;
        pagingInfo.setItemCount(-1);
        if (resetFirstItem) {
            pagingInfo.setFirstItem(0);
        }
    }

    public void validateCreate(FacesContext facesContext, UIComponent component, Object value) {
        Emprunt newEmprunt = new Emprunt();
        String newEmpruntString = converter.getAsString(FacesContext.getCurrentInstance(), null, newEmprunt);
        String empruntString = converter.getAsString(FacesContext.getCurrentInstance(), null, emprunt);
        if (!newEmpruntString.equals(empruntString)) {
            createSetup();
        }
    }

    public Converter getConverter() {
        return converter;
    }
    
}
