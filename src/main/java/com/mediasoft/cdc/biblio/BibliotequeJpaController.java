/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mediasoft.cdc.biblio;

import com.mediasoft.cdc.biblio.exceptions.NonexistentEntityException;
import com.mediasoft.cdc.biblio.exceptions.RollbackFailureException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author inganzamarumpu
 */
public class BibliotequeJpaController implements Serializable {

    public BibliotequeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Biblioteque biblioteque) throws RollbackFailureException, Exception {
        if (biblioteque.getExemplaireCollection() == null) {
            biblioteque.setExemplaireCollection(new ArrayList<Exemplaire>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Exemplaire> attachedExemplaireCollection = new ArrayList<Exemplaire>();
            for (Exemplaire exemplaireCollectionExemplaireToAttach : biblioteque.getExemplaireCollection()) {
                exemplaireCollectionExemplaireToAttach = em.getReference(exemplaireCollectionExemplaireToAttach.getClass(), exemplaireCollectionExemplaireToAttach.getId());
                attachedExemplaireCollection.add(exemplaireCollectionExemplaireToAttach);
            }
            biblioteque.setExemplaireCollection(attachedExemplaireCollection);
            em.persist(biblioteque);
            for (Exemplaire exemplaireCollectionExemplaire : biblioteque.getExemplaireCollection()) {
                Biblioteque oldBibliotequeIdOfExemplaireCollectionExemplaire = exemplaireCollectionExemplaire.getBibliotequeId();
                exemplaireCollectionExemplaire.setBibliotequeId(biblioteque);
                exemplaireCollectionExemplaire = em.merge(exemplaireCollectionExemplaire);
                if (oldBibliotequeIdOfExemplaireCollectionExemplaire != null) {
                    oldBibliotequeIdOfExemplaireCollectionExemplaire.getExemplaireCollection().remove(exemplaireCollectionExemplaire);
                    oldBibliotequeIdOfExemplaireCollectionExemplaire = em.merge(oldBibliotequeIdOfExemplaireCollectionExemplaire);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Biblioteque biblioteque) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Biblioteque persistentBiblioteque = em.find(Biblioteque.class, biblioteque.getId());
            Collection<Exemplaire> exemplaireCollectionOld = persistentBiblioteque.getExemplaireCollection();
            Collection<Exemplaire> exemplaireCollectionNew = biblioteque.getExemplaireCollection();
            Collection<Exemplaire> attachedExemplaireCollectionNew = new ArrayList<Exemplaire>();
            for (Exemplaire exemplaireCollectionNewExemplaireToAttach : exemplaireCollectionNew) {
                exemplaireCollectionNewExemplaireToAttach = em.getReference(exemplaireCollectionNewExemplaireToAttach.getClass(), exemplaireCollectionNewExemplaireToAttach.getId());
                attachedExemplaireCollectionNew.add(exemplaireCollectionNewExemplaireToAttach);
            }
            exemplaireCollectionNew = attachedExemplaireCollectionNew;
            biblioteque.setExemplaireCollection(exemplaireCollectionNew);
            biblioteque = em.merge(biblioteque);
            for (Exemplaire exemplaireCollectionOldExemplaire : exemplaireCollectionOld) {
                if (!exemplaireCollectionNew.contains(exemplaireCollectionOldExemplaire)) {
                    exemplaireCollectionOldExemplaire.setBibliotequeId(null);
                    exemplaireCollectionOldExemplaire = em.merge(exemplaireCollectionOldExemplaire);
                }
            }
            for (Exemplaire exemplaireCollectionNewExemplaire : exemplaireCollectionNew) {
                if (!exemplaireCollectionOld.contains(exemplaireCollectionNewExemplaire)) {
                    Biblioteque oldBibliotequeIdOfExemplaireCollectionNewExemplaire = exemplaireCollectionNewExemplaire.getBibliotequeId();
                    exemplaireCollectionNewExemplaire.setBibliotequeId(biblioteque);
                    exemplaireCollectionNewExemplaire = em.merge(exemplaireCollectionNewExemplaire);
                    if (oldBibliotequeIdOfExemplaireCollectionNewExemplaire != null && !oldBibliotequeIdOfExemplaireCollectionNewExemplaire.equals(biblioteque)) {
                        oldBibliotequeIdOfExemplaireCollectionNewExemplaire.getExemplaireCollection().remove(exemplaireCollectionNewExemplaire);
                        oldBibliotequeIdOfExemplaireCollectionNewExemplaire = em.merge(oldBibliotequeIdOfExemplaireCollectionNewExemplaire);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = biblioteque.getId();
                if (findBiblioteque(id) == null) {
                    throw new NonexistentEntityException("The biblioteque with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Biblioteque biblioteque;
            try {
                biblioteque = em.getReference(Biblioteque.class, id);
                biblioteque.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The biblioteque with id " + id + " no longer exists.", enfe);
            }
            Collection<Exemplaire> exemplaireCollection = biblioteque.getExemplaireCollection();
            for (Exemplaire exemplaireCollectionExemplaire : exemplaireCollection) {
                exemplaireCollectionExemplaire.setBibliotequeId(null);
                exemplaireCollectionExemplaire = em.merge(exemplaireCollectionExemplaire);
            }
            em.remove(biblioteque);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Biblioteque> findBibliotequeEntities() {
        return findBibliotequeEntities(true, -1, -1);
    }

    public List<Biblioteque> findBibliotequeEntities(int maxResults, int firstResult) {
        return findBibliotequeEntities(false, maxResults, firstResult);
    }

    private List<Biblioteque> findBibliotequeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Biblioteque.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Biblioteque findBiblioteque(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Biblioteque.class, id);
        } finally {
            em.close();
        }
    }

    public int getBibliotequeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Biblioteque> rt = cq.from(Biblioteque.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
