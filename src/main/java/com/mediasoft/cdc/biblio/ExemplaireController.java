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
@Named("exemplaire")
@RequestScoped
public class ExemplaireController {

    public ExemplaireController() {
        pagingInfo = new PagingInfo();
        converter = new ExemplaireConverter();
    }
    private Exemplaire exemplaire = null;
    private List<Exemplaire> exemplaireItems = null;
    private ExemplaireJpaController jpaController = null;
    private ExemplaireConverter converter = null;
    private PagingInfo pagingInfo = null;
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    public PagingInfo getPagingInfo() {
        if (pagingInfo.getItemCount() == -1) {
            pagingInfo.setItemCount(getJpaController().getExemplaireCount());
        }
        return pagingInfo;
    }
    
    public boolean hasPrevious(){
        return pagingInfo.getLastItem() + pagingInfo.getBatchSize() <= pagingInfo.getItemCount();
    }
    
    public boolean hasNext(){
        return pagingInfo.getLastItem() < pagingInfo.getItemCount() && pagingInfo.getLastItem() + pagingInfo.getBatchSize() > pagingInfo.getItemCount();
    }

    public ExemplaireJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new ExemplaireJpaController(utx, emf);
        }
        return jpaController;
    }

    public SelectItem[] getExemplaireItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(getJpaController().findExemplaireEntities(), false);
    }

    public SelectItem[] getExemplaireItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findExemplaireEntities(), true);
    }

    public Exemplaire getExemplaire() {
        if (exemplaire == null) {
            exemplaire = (Exemplaire) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentExemplaire", converter, null);
        }
        if (exemplaire == null) {
            exemplaire = new Exemplaire();
        }
        return exemplaire;
    }

    public String listSetup() {
        reset(true);
        return "exemplaire_list";
    }

    public String createSetup() {
        reset(false);
        exemplaire = new Exemplaire();
        return "exemplaire_create";
    }

    public String create() {
        try {
            getJpaController().create(exemplaire);
            JsfUtil.addSuccessMessage("Exemplaire was successfully created.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return listSetup();
    }

    public String detailSetup() {
        return scalarSetup("exemplaire_detail");
    }

    public String editSetup() {
        return scalarSetup("exemplaire_edit");
    }

    private String scalarSetup(String destination) {
        reset(false);
        exemplaire = (Exemplaire) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentExemplaire", converter, null);
        if (exemplaire == null) {
            String requestExemplaireString = JsfUtil.getRequestParameter("jsfcrud.currentExemplaire");
            JsfUtil.addErrorMessage("The exemplaire with id " + requestExemplaireString + " no longer exists.");
            return relatedOrListOutcome();
        }
        return destination;
    }

    public String edit() {
        String exemplaireString = converter.getAsString(FacesContext.getCurrentInstance(), null, exemplaire);
        String currentExemplaireString = JsfUtil.getRequestParameter("jsfcrud.currentExemplaire");
        if (exemplaireString == null || exemplaireString.length() == 0 || !exemplaireString.equals(currentExemplaireString)) {
            String outcome = editSetup();
            if ("exemplaire_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit exemplaire. Try again.");
            }
            return outcome;
        }
        try {
            getJpaController().edit(exemplaire);
            JsfUtil.addSuccessMessage("Exemplaire was successfully updated.");
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
        String idAsString = JsfUtil.getRequestParameter("jsfcrud.currentExemplaire");
        Integer id = new Integer(idAsString);
        try {
            getJpaController().destroy(id);
            JsfUtil.addSuccessMessage("Exemplaire was successfully deleted.");
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

    public List<Exemplaire> getExemplaireItems() {
        if (exemplaireItems == null) {
            getPagingInfo();
            exemplaireItems = getJpaController().findExemplaireEntities(pagingInfo.getBatchSize(), pagingInfo.getFirstItem());
        }
        return exemplaireItems;
    }

    public String next() {
        reset(false);
        getPagingInfo().nextPage();
        return "exemplaire_list";
    }

    public String prev() {
        reset(false);
        getPagingInfo().previousPage();
        return "exemplaire_list";
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
        exemplaire = null;
        exemplaireItems = null;
        pagingInfo.setItemCount(-1);
        if (resetFirstItem) {
            pagingInfo.setFirstItem(0);
        }
    }

    public void validateCreate(FacesContext facesContext, UIComponent component, Object value) {
        Exemplaire newExemplaire = new Exemplaire();
        String newExemplaireString = converter.getAsString(FacesContext.getCurrentInstance(), null, newExemplaire);
        String exemplaireString = converter.getAsString(FacesContext.getCurrentInstance(), null, exemplaire);
        if (!newExemplaireString.equals(exemplaireString)) {
            createSetup();
        }
    }

    public Converter getConverter() {
        return converter;
    }
    
}
