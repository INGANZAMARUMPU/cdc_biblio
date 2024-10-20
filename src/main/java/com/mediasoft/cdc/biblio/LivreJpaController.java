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
public class LivreJpaController implements Serializable {

    public LivreJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Livre livre) throws RollbackFailureException, Exception {
        System.out.println(livre.getTitre());
        if(livre == null) livre = new Livre();
        if (livre.getEmpruntCollection() == null) {
            livre.setEmpruntCollection(new ArrayList<Emprunt>());
        }
        if (livre.getExemplaireCollection() == null) {
            livre.setExemplaireCollection(new ArrayList<Exemplaire>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Emprunt> attachedEmpruntCollection = new ArrayList<Emprunt>();
            for (Emprunt empruntCollectionEmpruntToAttach : livre.getEmpruntCollection()) {
                empruntCollectionEmpruntToAttach = em.getReference(empruntCollectionEmpruntToAttach.getClass(), empruntCollectionEmpruntToAttach.getId());
                attachedEmpruntCollection.add(empruntCollectionEmpruntToAttach);
            }
            livre.setEmpruntCollection(attachedEmpruntCollection);
            Collection<Exemplaire> attachedExemplaireCollection = new ArrayList<Exemplaire>();
            for (Exemplaire exemplaireCollectionExemplaireToAttach : livre.getExemplaireCollection()) {
                exemplaireCollectionExemplaireToAttach = em.getReference(exemplaireCollectionExemplaireToAttach.getClass(), exemplaireCollectionExemplaireToAttach.getId());
                attachedExemplaireCollection.add(exemplaireCollectionExemplaireToAttach);
            }
            livre.setExemplaireCollection(attachedExemplaireCollection);
            em.persist(livre);
            for (Emprunt empruntCollectionEmprunt : livre.getEmpruntCollection()) {
                Livre oldLivreIdOfEmpruntCollectionEmprunt = empruntCollectionEmprunt.getLivreId();
                empruntCollectionEmprunt.setLivreId(livre);
                empruntCollectionEmprunt = em.merge(empruntCollectionEmprunt);
                if (oldLivreIdOfEmpruntCollectionEmprunt != null) {
                    oldLivreIdOfEmpruntCollectionEmprunt.getEmpruntCollection().remove(empruntCollectionEmprunt);
                    oldLivreIdOfEmpruntCollectionEmprunt = em.merge(oldLivreIdOfEmpruntCollectionEmprunt);
                }
            }
            for (Exemplaire exemplaireCollectionExemplaire : livre.getExemplaireCollection()) {
                Livre oldLivreIdOfExemplaireCollectionExemplaire = exemplaireCollectionExemplaire.getLivreId();
                exemplaireCollectionExemplaire.setLivreId(livre);
                exemplaireCollectionExemplaire = em.merge(exemplaireCollectionExemplaire);
                if (oldLivreIdOfExemplaireCollectionExemplaire != null) {
                    oldLivreIdOfExemplaireCollectionExemplaire.getExemplaireCollection().remove(exemplaireCollectionExemplaire);
                    oldLivreIdOfExemplaireCollectionExemplaire = em.merge(oldLivreIdOfExemplaireCollectionExemplaire);
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

    public void edit(Livre livre) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Livre persistentLivre = em.find(Livre.class, livre.getId());
            Collection<Emprunt> empruntCollectionOld = persistentLivre.getEmpruntCollection();
            Collection<Emprunt> empruntCollectionNew = livre.getEmpruntCollection();
            Collection<Exemplaire> exemplaireCollectionOld = persistentLivre.getExemplaireCollection();
            Collection<Exemplaire> exemplaireCollectionNew = livre.getExemplaireCollection();
            Collection<Emprunt> attachedEmpruntCollectionNew = new ArrayList<Emprunt>();
            for (Emprunt empruntCollectionNewEmpruntToAttach : empruntCollectionNew) {
                empruntCollectionNewEmpruntToAttach = em.getReference(empruntCollectionNewEmpruntToAttach.getClass(), empruntCollectionNewEmpruntToAttach.getId());
                attachedEmpruntCollectionNew.add(empruntCollectionNewEmpruntToAttach);
            }
            empruntCollectionNew = attachedEmpruntCollectionNew;
            livre.setEmpruntCollection(empruntCollectionNew);
            Collection<Exemplaire> attachedExemplaireCollectionNew = new ArrayList<Exemplaire>();
            for (Exemplaire exemplaireCollectionNewExemplaireToAttach : exemplaireCollectionNew) {
                exemplaireCollectionNewExemplaireToAttach = em.getReference(exemplaireCollectionNewExemplaireToAttach.getClass(), exemplaireCollectionNewExemplaireToAttach.getId());
                attachedExemplaireCollectionNew.add(exemplaireCollectionNewExemplaireToAttach);
            }
            exemplaireCollectionNew = attachedExemplaireCollectionNew;
            livre.setExemplaireCollection(exemplaireCollectionNew);
            livre = em.merge(livre);
            for (Emprunt empruntCollectionOldEmprunt : empruntCollectionOld) {
                if (!empruntCollectionNew.contains(empruntCollectionOldEmprunt)) {
                    empruntCollectionOldEmprunt.setLivreId(null);
                    empruntCollectionOldEmprunt = em.merge(empruntCollectionOldEmprunt);
                }
            }
            for (Emprunt empruntCollectionNewEmprunt : empruntCollectionNew) {
                if (!empruntCollectionOld.contains(empruntCollectionNewEmprunt)) {
                    Livre oldLivreIdOfEmpruntCollectionNewEmprunt = empruntCollectionNewEmprunt.getLivreId();
                    empruntCollectionNewEmprunt.setLivreId(livre);
                    empruntCollectionNewEmprunt = em.merge(empruntCollectionNewEmprunt);
                    if (oldLivreIdOfEmpruntCollectionNewEmprunt != null && !oldLivreIdOfEmpruntCollectionNewEmprunt.equals(livre)) {
                        oldLivreIdOfEmpruntCollectionNewEmprunt.getEmpruntCollection().remove(empruntCollectionNewEmprunt);
                        oldLivreIdOfEmpruntCollectionNewEmprunt = em.merge(oldLivreIdOfEmpruntCollectionNewEmprunt);
                    }
                }
            }
            for (Exemplaire exemplaireCollectionOldExemplaire : exemplaireCollectionOld) {
                if (!exemplaireCollectionNew.contains(exemplaireCollectionOldExemplaire)) {
                    exemplaireCollectionOldExemplaire.setLivreId(null);
                    exemplaireCollectionOldExemplaire = em.merge(exemplaireCollectionOldExemplaire);
                }
            }
            for (Exemplaire exemplaireCollectionNewExemplaire : exemplaireCollectionNew) {
                if (!exemplaireCollectionOld.contains(exemplaireCollectionNewExemplaire)) {
                    Livre oldLivreIdOfExemplaireCollectionNewExemplaire = exemplaireCollectionNewExemplaire.getLivreId();
                    exemplaireCollectionNewExemplaire.setLivreId(livre);
                    exemplaireCollectionNewExemplaire = em.merge(exemplaireCollectionNewExemplaire);
                    if (oldLivreIdOfExemplaireCollectionNewExemplaire != null && !oldLivreIdOfExemplaireCollectionNewExemplaire.equals(livre)) {
                        oldLivreIdOfExemplaireCollectionNewExemplaire.getExemplaireCollection().remove(exemplaireCollectionNewExemplaire);
                        oldLivreIdOfExemplaireCollectionNewExemplaire = em.merge(oldLivreIdOfExemplaireCollectionNewExemplaire);
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
                Integer id = livre.getId();
                if (findLivre(id) == null) {
                    throw new NonexistentEntityException("The livre with id " + id + " no longer exists.");
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
            Livre livre;
            try {
                livre = em.getReference(Livre.class, id);
                livre.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The livre with id " + id + " no longer exists.", enfe);
            }
            Collection<Emprunt> empruntCollection = livre.getEmpruntCollection();
            for (Emprunt empruntCollectionEmprunt : empruntCollection) {
                empruntCollectionEmprunt.setLivreId(null);
                empruntCollectionEmprunt = em.merge(empruntCollectionEmprunt);
            }
            Collection<Exemplaire> exemplaireCollection = livre.getExemplaireCollection();
            for (Exemplaire exemplaireCollectionExemplaire : exemplaireCollection) {
                exemplaireCollectionExemplaire.setLivreId(null);
                exemplaireCollectionExemplaire = em.merge(exemplaireCollectionExemplaire);
            }
            em.remove(livre);
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

    public List<Livre> findLivreEntities() {
        return findLivreEntities(true, -1, -1);
    }

    public List<Livre> findLivreEntities(int maxResults, int firstResult) {
        return findLivreEntities(false, maxResults, firstResult);
    }

    private List<Livre> findLivreEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Livre.class));
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

    public Livre findLivre(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Livre.class, id);
        } finally {
            em.close();
        }
    }

    public int getLivreCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Livre> rt = cq.from(Livre.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
