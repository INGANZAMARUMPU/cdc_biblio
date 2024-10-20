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
import java.util.List;

/**
 *
 * @author inganzamarumpu
 */
public class EmpruntJpaController implements Serializable {

    public EmpruntJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Emprunt emprunt) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Emprunteur emprunteurId = emprunt.getEmprunteurId();
            if (emprunteurId != null) {
                emprunteurId = em.getReference(emprunteurId.getClass(), emprunteurId.getId());
                emprunt.setEmprunteurId(emprunteurId);
            }
            Livre livreId = emprunt.getLivreId();
            if (livreId != null) {
                livreId = em.getReference(livreId.getClass(), livreId.getId());
                emprunt.setLivreId(livreId);
            }
            em.persist(emprunt);
            if (emprunteurId != null) {
                emprunteurId.getEmpruntCollection().add(emprunt);
                emprunteurId = em.merge(emprunteurId);
            }
            if (livreId != null) {
                livreId.getEmpruntCollection().add(emprunt);
                livreId = em.merge(livreId);
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

    public void edit(Emprunt emprunt) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Emprunt persistentEmprunt = em.find(Emprunt.class, emprunt.getId());
            Emprunteur emprunteurIdOld = persistentEmprunt.getEmprunteurId();
            Emprunteur emprunteurIdNew = emprunt.getEmprunteurId();
            Livre livreIdOld = persistentEmprunt.getLivreId();
            Livre livreIdNew = emprunt.getLivreId();
            if (emprunteurIdNew != null) {
                emprunteurIdNew = em.getReference(emprunteurIdNew.getClass(), emprunteurIdNew.getId());
                emprunt.setEmprunteurId(emprunteurIdNew);
            }
            if (livreIdNew != null) {
                livreIdNew = em.getReference(livreIdNew.getClass(), livreIdNew.getId());
                emprunt.setLivreId(livreIdNew);
            }
            emprunt = em.merge(emprunt);
            if (emprunteurIdOld != null && !emprunteurIdOld.equals(emprunteurIdNew)) {
                emprunteurIdOld.getEmpruntCollection().remove(emprunt);
                emprunteurIdOld = em.merge(emprunteurIdOld);
            }
            if (emprunteurIdNew != null && !emprunteurIdNew.equals(emprunteurIdOld)) {
                emprunteurIdNew.getEmpruntCollection().add(emprunt);
                emprunteurIdNew = em.merge(emprunteurIdNew);
            }
            if (livreIdOld != null && !livreIdOld.equals(livreIdNew)) {
                livreIdOld.getEmpruntCollection().remove(emprunt);
                livreIdOld = em.merge(livreIdOld);
            }
            if (livreIdNew != null && !livreIdNew.equals(livreIdOld)) {
                livreIdNew.getEmpruntCollection().add(emprunt);
                livreIdNew = em.merge(livreIdNew);
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
                Long id = emprunt.getId();
                if (findEmprunt(id) == null) {
                    throw new NonexistentEntityException("The emprunt with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Emprunt emprunt;
            try {
                emprunt = em.getReference(Emprunt.class, id);
                emprunt.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The emprunt with id " + id + " no longer exists.", enfe);
            }
            Emprunteur emprunteurId = emprunt.getEmprunteurId();
            if (emprunteurId != null) {
                emprunteurId.getEmpruntCollection().remove(emprunt);
                emprunteurId = em.merge(emprunteurId);
            }
            Livre livreId = emprunt.getLivreId();
            if (livreId != null) {
                livreId.getEmpruntCollection().remove(emprunt);
                livreId = em.merge(livreId);
            }
            em.remove(emprunt);
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

    public List<Emprunt> findEmpruntEntities() {
        return findEmpruntEntities(true, -1, -1);
    }

    public List<Emprunt> findEmpruntEntities(int maxResults, int firstResult) {
        return findEmpruntEntities(false, maxResults, firstResult);
    }

    private List<Emprunt> findEmpruntEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Emprunt.class));
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

    public Emprunt findEmprunt(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Emprunt.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpruntCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Emprunt> rt = cq.from(Emprunt.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
