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
@Named("livre")
@RequestScoped
public class LivreController {

    public LivreController() {
        pagingInfo = new PagingInfo();
        converter = new LivreConverter();
    }
    private Livre livre = null;
    private List<Livre> livreItems = null;
    private LivreJpaController jpaController = null;
    private LivreConverter converter = null;
    private PagingInfo pagingInfo = null;
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    public PagingInfo getPagingInfo() {
        if (pagingInfo.getItemCount() == -1) {
            pagingInfo.setItemCount(getJpaController().getLivreCount());
        }
        return pagingInfo;
    }
    
    public boolean hasPrevious(){
        return pagingInfo.getLastItem() + pagingInfo.getBatchSize() <= pagingInfo.getItemCount();
    }
    
    public boolean hasNext(){
        return pagingInfo.getLastItem() < pagingInfo.getItemCount() && pagingInfo.getLastItem() + pagingInfo.getBatchSize() > pagingInfo.getItemCount();
    }

    public LivreJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new LivreJpaController(utx, emf);
        }
        return jpaController;
    }

    public SelectItem[] getLivreItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(getJpaController().findLivreEntities(), false);
    }

    public SelectItem[] getLivreItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findLivreEntities(), true);
    }

    public Livre getLivre() {
        if (livre == null) {
            livre = (Livre) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentLivre", converter, null);
        }
        if (livre == null) {
            livre = new Livre();
        }
        return livre;
    }

    public String listSetup() {
        reset(true);
        return "livre_list";
    }

    public String createSetup() {
        reset(false);
        livre = new Livre();
        return "livre_create";
    }

    public String create() {
        try {
            getJpaController().create(livre);
            JsfUtil.addSuccessMessage("Livre was successfully created.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return listSetup();
    }

    public String detailSetup() {
        return scalarSetup("livre_detail");
    }

    public String editSetup() {
        return scalarSetup("livre_edit");
    }

    private String scalarSetup(String destination) {
        reset(false);
        livre = (Livre) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentLivre", converter, null);
        if (livre == null) {
            String requestLivreString = JsfUtil.getRequestParameter("jsfcrud.currentLivre");
            JsfUtil.addErrorMessage("The livre with id " + requestLivreString + " no longer exists.");
            return relatedOrListOutcome();
        }
        return destination;
    }

    public String edit() {
        String livreString = converter.getAsString(FacesContext.getCurrentInstance(), null, livre);
        String currentLivreString = JsfUtil.getRequestParameter("jsfcrud.currentLivre");
        if (livreString == null || livreString.length() == 0 || !livreString.equals(currentLivreString)) {
            String outcome = editSetup();
            if ("livre_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit livre. Try again.");
            }
            return outcome;
        }
        try {
            getJpaController().edit(livre);
            JsfUtil.addSuccessMessage("Livre was successfully updated.");
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
        String idAsString = JsfUtil.getRequestParameter("jsfcrud.currentLivre");
        Integer id = new Integer(idAsString);
        try {
            getJpaController().destroy(id);
            JsfUtil.addSuccessMessage("Livre was successfully deleted.");
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

    public List<Livre> getLivreItems() {
        if (livreItems == null) {
            getPagingInfo();
            livreItems = getJpaController().findLivreEntities(pagingInfo.getBatchSize(), pagingInfo.getFirstItem());
        }
        return livreItems;
    }

    public String next() {
        reset(false);
        getPagingInfo().nextPage();
        return "livre_list";
    }

    public String prev() {
        reset(false);
        getPagingInfo().previousPage();
        return "livre_list";
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
        livre = null;
        livreItems = null;
        pagingInfo.setItemCount(-1);
        if (resetFirstItem) {
            pagingInfo.setFirstItem(0);
        }
    }

    public void validateCreate(FacesContext facesContext, UIComponent component, Object value) {
        Livre newLivre = new Livre();
        String newLivreString = converter.getAsString(FacesContext.getCurrentInstance(), null, newLivre);
        String livreString = converter.getAsString(FacesContext.getCurrentInstance(), null, livre);
        if (!newLivreString.equals(livreString)) {
            createSetup();
        }
    }

    public Converter getConverter() {
        return converter;
    }
    
}
