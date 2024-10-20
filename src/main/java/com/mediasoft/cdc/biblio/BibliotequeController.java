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
import jakarta.faces.annotation.RequestMap;
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
@Named("biblioteque")
@RequestScoped
public class BibliotequeController {

    public BibliotequeController() {
        pagingInfo = new PagingInfo();
        converter = new BibliotequeConverter();
    }
    private Biblioteque biblioteque = null;
    private List<Biblioteque> bibliotequeItems = null;
    private BibliotequeJpaController jpaController = null;
    private BibliotequeConverter converter = null;
    private PagingInfo pagingInfo = null;
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    public PagingInfo getPagingInfo() {
        if (pagingInfo.getItemCount() == -1) {
            pagingInfo.setItemCount(getJpaController().getBibliotequeCount());
        }
        return pagingInfo;
    }
    
    public boolean hasPrevious(){
        return pagingInfo.getLastItem() + pagingInfo.getBatchSize() <= pagingInfo.getItemCount();
    }
    
    public boolean hasNext(){
        return pagingInfo.getLastItem() < pagingInfo.getItemCount() && pagingInfo.getLastItem() + pagingInfo.getBatchSize() > pagingInfo.getItemCount();
    }

    public BibliotequeJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new BibliotequeJpaController(utx, emf);
        }
        return jpaController;
    }

    public SelectItem[] getBibliotequeItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(getJpaController().findBibliotequeEntities(), false);
    }

    public SelectItem[] getBibliotequeItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findBibliotequeEntities(), true);
    }

    public Biblioteque getBiblioteque() {
        if (biblioteque == null) {
            biblioteque = (Biblioteque) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentBiblioteque", converter, null);
        }
        if (biblioteque == null) {
            biblioteque = new Biblioteque();
        }
        return biblioteque;
    }

    public String listSetup() {
        reset(true);
        return "biblioteque_list";
    }

    public String createSetup() {
        reset(false);
        biblioteque = new Biblioteque();
        return "biblioteque_create";
    }

    public String create() {
        try {
            getJpaController().create(biblioteque);
            JsfUtil.addSuccessMessage("Biblioteque was successfully created.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return listSetup();
    }

    public String detailSetup() {
        return scalarSetup("biblioteque_detail");
    }

    public String editSetup() {
        return scalarSetup("biblioteque_edit");
    }

    private String scalarSetup(String destination) {
        reset(false);
        biblioteque = (Biblioteque) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentBiblioteque", converter, null);
        if (biblioteque == null) {
            String requestBibliotequeString = JsfUtil.getRequestParameter("jsfcrud.currentBiblioteque");
            JsfUtil.addErrorMessage("The biblioteque with id " + requestBibliotequeString + " no longer exists.");
            return relatedOrListOutcome();
        }
        return destination;
    }

    public String edit() {
        String bibliotequeString = converter.getAsString(FacesContext.getCurrentInstance(), null, biblioteque);
        String currentBibliotequeString = JsfUtil.getRequestParameter("jsfcrud.currentBiblioteque");
        if (bibliotequeString == null || bibliotequeString.length() == 0 || !bibliotequeString.equals(currentBibliotequeString)) {
            String outcome = editSetup();
            if ("biblioteque_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit biblioteque. Try again.");
            }
            return outcome;
        }
        try {
            getJpaController().edit(biblioteque);
            JsfUtil.addSuccessMessage("Biblioteque was successfully updated.");
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
        String idAsString = JsfUtil.getRequestParameter("jsfcrud.currentBiblioteque");
        Integer id = new Integer(idAsString);
        try {
            getJpaController().destroy(id);
            JsfUtil.addSuccessMessage("Biblioteque was successfully deleted.");
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

    public List<Biblioteque> getBibliotequeItems() {
        if (bibliotequeItems == null) {
            getPagingInfo();
            bibliotequeItems = getJpaController().findBibliotequeEntities(pagingInfo.getBatchSize(), pagingInfo.getFirstItem());
        }
        return bibliotequeItems;
    }

    public String next() {
        reset(false);
        getPagingInfo().nextPage();
        return "biblioteque_list";
    }

    public String prev() {
        reset(false);
        getPagingInfo().previousPage();
        return "biblioteque_list";
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
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new FacesException(e);
            }
        }
        return null;
    }

    private void reset(boolean resetFirstItem) {
        biblioteque = null;
        bibliotequeItems = null;
        pagingInfo.setItemCount(-1);
        if (resetFirstItem) {
            pagingInfo.setFirstItem(0);
        }
    }

    public void validateCreate(FacesContext facesContext, UIComponent component, Object value) {
        Biblioteque newBiblioteque = new Biblioteque();
        String newBibliotequeString = converter.getAsString(FacesContext.getCurrentInstance(), null, newBiblioteque);
        String bibliotequeString = converter.getAsString(FacesContext.getCurrentInstance(), null, biblioteque);
        if (!newBibliotequeString.equals(bibliotequeString)) {
            createSetup();
        }
    }

    public Converter getConverter() {
        return converter;
    }
    
}
