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
@Named("emprunteur")
@RequestScoped
public class EmprunteurController {

    public EmprunteurController() {
        pagingInfo = new PagingInfo();
        converter = new EmprunteurConverter();
    }
    private Emprunteur emprunteur = null;
    private List<Emprunteur> emprunteurItems = null;
    private EmprunteurJpaController jpaController = null;
    private EmprunteurConverter converter = null;
    private PagingInfo pagingInfo = null;
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    public PagingInfo getPagingInfo() {
        if (pagingInfo.getItemCount() == -1) {
            pagingInfo.setItemCount(getJpaController().getEmprunteurCount());
        }
        return pagingInfo;
    }
    
    public boolean hasPrevious(){
        return pagingInfo.getLastItem() + pagingInfo.getBatchSize() <= pagingInfo.getItemCount();
    }
    
    public boolean hasNext(){
        return pagingInfo.getLastItem() < pagingInfo.getItemCount() && pagingInfo.getLastItem() + pagingInfo.getBatchSize() > pagingInfo.getItemCount();
    }

    public EmprunteurJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new EmprunteurJpaController(utx, emf);
        }
        return jpaController;
    }

    public SelectItem[] getEmprunteurItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(getJpaController().findEmprunteurEntities(), false);
    }

    public SelectItem[] getEmprunteurItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findEmprunteurEntities(), true);
    }

    public Emprunteur getEmprunteur() {
        if (emprunteur == null) {
            emprunteur = (Emprunteur) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentEmprunteur", converter, null);
        }
        if (emprunteur == null) {
            emprunteur = new Emprunteur();
        }
        return emprunteur;
    }

    public String listSetup() {
        reset(true);
        return "emprunteur_list";
    }

    public String createSetup() {
        reset(false);
        emprunteur = new Emprunteur();
        return "emprunteur_create";
    }

    public String create() {
        try {
            getJpaController().create(emprunteur);
            JsfUtil.addSuccessMessage("Emprunteur was successfully created.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return listSetup();
    }

    public String detailSetup() {
        return scalarSetup("emprunteur_detail");
    }

    public String editSetup() {
        return scalarSetup("emprunteur_edit");
    }

    private String scalarSetup(String destination) {
        reset(false);
        emprunteur = (Emprunteur) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentEmprunteur", converter, null);
        if (emprunteur == null) {
            String requestEmprunteurString = JsfUtil.getRequestParameter("jsfcrud.currentEmprunteur");
            JsfUtil.addErrorMessage("The emprunteur with id " + requestEmprunteurString + " no longer exists.");
            return relatedOrListOutcome();
        }
        return destination;
    }

    public String edit() {
        String emprunteurString = converter.getAsString(FacesContext.getCurrentInstance(), null, emprunteur);
        String currentEmprunteurString = JsfUtil.getRequestParameter("jsfcrud.currentEmprunteur");
        if (emprunteurString == null || emprunteurString.length() == 0 || !emprunteurString.equals(currentEmprunteurString)) {
            String outcome = editSetup();
            if ("emprunteur_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit emprunteur. Try again.");
            }
            return outcome;
        }
        try {
            getJpaController().edit(emprunteur);
            JsfUtil.addSuccessMessage("Emprunteur was successfully updated.");
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
        String idAsString = JsfUtil.getRequestParameter("jsfcrud.currentEmprunteur");
        Integer id = new Integer(idAsString);
        try {
            getJpaController().destroy(id);
            JsfUtil.addSuccessMessage("Emprunteur was successfully deleted.");
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

    public List<Emprunteur> getEmprunteurItems() {
        if (emprunteurItems == null) {
            getPagingInfo();
            emprunteurItems = getJpaController().findEmprunteurEntities(pagingInfo.getBatchSize(), pagingInfo.getFirstItem());
        }
        return emprunteurItems;
    }

    public String next() {
        reset(false);
        getPagingInfo().nextPage();
        return "emprunteur_list";
    }

    public String prev() {
        reset(false);
        getPagingInfo().previousPage();
        return "emprunteur_list";
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
        emprunteur = null;
        emprunteurItems = null;
        pagingInfo.setItemCount(-1);
        if (resetFirstItem) {
            pagingInfo.setFirstItem(0);
        }
    }

    public void validateCreate(FacesContext facesContext, UIComponent component, Object value) {
        Emprunteur newEmprunteur = new Emprunteur();
        String newEmprunteurString = converter.getAsString(FacesContext.getCurrentInstance(), null, newEmprunteur);
        String emprunteurString = converter.getAsString(FacesContext.getCurrentInstance(), null, emprunteur);
        if (!newEmprunteurString.equals(emprunteurString)) {
            createSetup();
        }
    }

    public Converter getConverter() {
        return converter;
    }
    
}
