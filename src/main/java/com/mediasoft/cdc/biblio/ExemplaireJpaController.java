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
public class ExemplaireJpaController implements Serializable {

    public ExemplaireJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Exemplaire exemplaire) throws RollbackFailureException, Exception {
        if (exemplaire.getReservationCollection() == null) {
            exemplaire.setReservationCollection(new ArrayList<Reservation>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Biblioteque bibliotequeId = exemplaire.getBibliotequeId();
            if (bibliotequeId != null) {
                bibliotequeId = em.getReference(bibliotequeId.getClass(), bibliotequeId.getId());
                exemplaire.setBibliotequeId(bibliotequeId);
            }
            Livre livreId = exemplaire.getLivreId();
            if (livreId != null) {
                livreId = em.getReference(livreId.getClass(), livreId.getId());
                exemplaire.setLivreId(livreId);
            }
            Collection<Reservation> attachedReservationCollection = new ArrayList<Reservation>();
            for (Reservation reservationCollectionReservationToAttach : exemplaire.getReservationCollection()) {
                reservationCollectionReservationToAttach = em.getReference(reservationCollectionReservationToAttach.getClass(), reservationCollectionReservationToAttach.getId());
                attachedReservationCollection.add(reservationCollectionReservationToAttach);
            }
            exemplaire.setReservationCollection(attachedReservationCollection);
            em.persist(exemplaire);
            if (bibliotequeId != null) {
                bibliotequeId.getExemplaireCollection().add(exemplaire);
                bibliotequeId = em.merge(bibliotequeId);
            }
            if (livreId != null) {
                livreId.getExemplaireCollection().add(exemplaire);
                livreId = em.merge(livreId);
            }
            for (Reservation reservationCollectionReservation : exemplaire.getReservationCollection()) {
                Exemplaire oldExemplaireIdOfReservationCollectionReservation = reservationCollectionReservation.getExemplaireId();
                reservationCollectionReservation.setExemplaireId(exemplaire);
                reservationCollectionReservation = em.merge(reservationCollectionReservation);
                if (oldExemplaireIdOfReservationCollectionReservation != null) {
                    oldExemplaireIdOfReservationCollectionReservation.getReservationCollection().remove(reservationCollectionReservation);
                    oldExemplaireIdOfReservationCollectionReservation = em.merge(oldExemplaireIdOfReservationCollectionReservation);
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

    public void edit(Exemplaire exemplaire) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Exemplaire persistentExemplaire = em.find(Exemplaire.class, exemplaire.getId());
            Biblioteque bibliotequeIdOld = persistentExemplaire.getBibliotequeId();
            Biblioteque bibliotequeIdNew = exemplaire.getBibliotequeId();
            Livre livreIdOld = persistentExemplaire.getLivreId();
            Livre livreIdNew = exemplaire.getLivreId();
            Collection<Reservation> reservationCollectionOld = persistentExemplaire.getReservationCollection();
            Collection<Reservation> reservationCollectionNew = exemplaire.getReservationCollection();
            if (bibliotequeIdNew != null) {
                bibliotequeIdNew = em.getReference(bibliotequeIdNew.getClass(), bibliotequeIdNew.getId());
                exemplaire.setBibliotequeId(bibliotequeIdNew);
            }
            if (livreIdNew != null) {
                livreIdNew = em.getReference(livreIdNew.getClass(), livreIdNew.getId());
                exemplaire.setLivreId(livreIdNew);
            }
            Collection<Reservation> attachedReservationCollectionNew = new ArrayList<Reservation>();
            for (Reservation reservationCollectionNewReservationToAttach : reservationCollectionNew) {
                reservationCollectionNewReservationToAttach = em.getReference(reservationCollectionNewReservationToAttach.getClass(), reservationCollectionNewReservationToAttach.getId());
                attachedReservationCollectionNew.add(reservationCollectionNewReservationToAttach);
            }
            reservationCollectionNew = attachedReservationCollectionNew;
            exemplaire.setReservationCollection(reservationCollectionNew);
            exemplaire = em.merge(exemplaire);
            if (bibliotequeIdOld != null && !bibliotequeIdOld.equals(bibliotequeIdNew)) {
                bibliotequeIdOld.getExemplaireCollection().remove(exemplaire);
                bibliotequeIdOld = em.merge(bibliotequeIdOld);
            }
            if (bibliotequeIdNew != null && !bibliotequeIdNew.equals(bibliotequeIdOld)) {
                bibliotequeIdNew.getExemplaireCollection().add(exemplaire);
                bibliotequeIdNew = em.merge(bibliotequeIdNew);
            }
            if (livreIdOld != null && !livreIdOld.equals(livreIdNew)) {
                livreIdOld.getExemplaireCollection().remove(exemplaire);
                livreIdOld = em.merge(livreIdOld);
            }
            if (livreIdNew != null && !livreIdNew.equals(livreIdOld)) {
                livreIdNew.getExemplaireCollection().add(exemplaire);
                livreIdNew = em.merge(livreIdNew);
            }
            for (Reservation reservationCollectionOldReservation : reservationCollectionOld) {
                if (!reservationCollectionNew.contains(reservationCollectionOldReservation)) {
                    reservationCollectionOldReservation.setExemplaireId(null);
                    reservationCollectionOldReservation = em.merge(reservationCollectionOldReservation);
                }
            }
            for (Reservation reservationCollectionNewReservation : reservationCollectionNew) {
                if (!reservationCollectionOld.contains(reservationCollectionNewReservation)) {
                    Exemplaire oldExemplaireIdOfReservationCollectionNewReservation = reservationCollectionNewReservation.getExemplaireId();
                    reservationCollectionNewReservation.setExemplaireId(exemplaire);
                    reservationCollectionNewReservation = em.merge(reservationCollectionNewReservation);
                    if (oldExemplaireIdOfReservationCollectionNewReservation != null && !oldExemplaireIdOfReservationCollectionNewReservation.equals(exemplaire)) {
                        oldExemplaireIdOfReservationCollectionNewReservation.getReservationCollection().remove(reservationCollectionNewReservation);
                        oldExemplaireIdOfReservationCollectionNewReservation = em.merge(oldExemplaireIdOfReservationCollectionNewReservation);
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
                Integer id = exemplaire.getId();
                if (findExemplaire(id) == null) {
                    throw new NonexistentEntityException("The exemplaire with id " + id + " no longer exists.");
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
            Exemplaire exemplaire;
            try {
                exemplaire = em.getReference(Exemplaire.class, id);
                exemplaire.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The exemplaire with id " + id + " no longer exists.", enfe);
            }
            Biblioteque bibliotequeId = exemplaire.getBibliotequeId();
            if (bibliotequeId != null) {
                bibliotequeId.getExemplaireCollection().remove(exemplaire);
                bibliotequeId = em.merge(bibliotequeId);
            }
            Livre livreId = exemplaire.getLivreId();
            if (livreId != null) {
                livreId.getExemplaireCollection().remove(exemplaire);
                livreId = em.merge(livreId);
            }
            Collection<Reservation> reservationCollection = exemplaire.getReservationCollection();
            for (Reservation reservationCollectionReservation : reservationCollection) {
                reservationCollectionReservation.setExemplaireId(null);
                reservationCollectionReservation = em.merge(reservationCollectionReservation);
            }
            em.remove(exemplaire);
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

    public List<Exemplaire> findExemplaireEntities() {
        return findExemplaireEntities(true, -1, -1);
    }

    public List<Exemplaire> findExemplaireEntities(int maxResults, int firstResult) {
        return findExemplaireEntities(false, maxResults, firstResult);
    }

    private List<Exemplaire> findExemplaireEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Exemplaire.class));
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

    public Exemplaire findExemplaire(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Exemplaire.class, id);
        } finally {
            em.close();
        }
    }

    public int getExemplaireCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Exemplaire> rt = cq.from(Exemplaire.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
