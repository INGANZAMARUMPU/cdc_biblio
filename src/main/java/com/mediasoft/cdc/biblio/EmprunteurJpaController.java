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
public class EmprunteurJpaController implements Serializable {

    public EmprunteurJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Emprunteur emprunteur) throws RollbackFailureException, Exception {
        if (emprunteur.getEmpruntCollection() == null) {
            emprunteur.setEmpruntCollection(new ArrayList<Emprunt>());
        }
        if (emprunteur.getReservationCollection() == null) {
            emprunteur.setReservationCollection(new ArrayList<Reservation>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Emprunt> attachedEmpruntCollection = new ArrayList<Emprunt>();
            for (Emprunt empruntCollectionEmpruntToAttach : emprunteur.getEmpruntCollection()) {
                empruntCollectionEmpruntToAttach = em.getReference(empruntCollectionEmpruntToAttach.getClass(), empruntCollectionEmpruntToAttach.getId());
                attachedEmpruntCollection.add(empruntCollectionEmpruntToAttach);
            }
            emprunteur.setEmpruntCollection(attachedEmpruntCollection);
            Collection<Reservation> attachedReservationCollection = new ArrayList<Reservation>();
            for (Reservation reservationCollectionReservationToAttach : emprunteur.getReservationCollection()) {
                reservationCollectionReservationToAttach = em.getReference(reservationCollectionReservationToAttach.getClass(), reservationCollectionReservationToAttach.getId());
                attachedReservationCollection.add(reservationCollectionReservationToAttach);
            }
            emprunteur.setReservationCollection(attachedReservationCollection);
            em.persist(emprunteur);
            for (Emprunt empruntCollectionEmprunt : emprunteur.getEmpruntCollection()) {
                Emprunteur oldEmprunteurIdOfEmpruntCollectionEmprunt = empruntCollectionEmprunt.getEmprunteurId();
                empruntCollectionEmprunt.setEmprunteurId(emprunteur);
                empruntCollectionEmprunt = em.merge(empruntCollectionEmprunt);
                if (oldEmprunteurIdOfEmpruntCollectionEmprunt != null) {
                    oldEmprunteurIdOfEmpruntCollectionEmprunt.getEmpruntCollection().remove(empruntCollectionEmprunt);
                    oldEmprunteurIdOfEmpruntCollectionEmprunt = em.merge(oldEmprunteurIdOfEmpruntCollectionEmprunt);
                }
            }
            for (Reservation reservationCollectionReservation : emprunteur.getReservationCollection()) {
                Emprunteur oldEmprunteurIdOfReservationCollectionReservation = reservationCollectionReservation.getEmprunteurId();
                reservationCollectionReservation.setEmprunteurId(emprunteur);
                reservationCollectionReservation = em.merge(reservationCollectionReservation);
                if (oldEmprunteurIdOfReservationCollectionReservation != null) {
                    oldEmprunteurIdOfReservationCollectionReservation.getReservationCollection().remove(reservationCollectionReservation);
                    oldEmprunteurIdOfReservationCollectionReservation = em.merge(oldEmprunteurIdOfReservationCollectionReservation);
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

    public void edit(Emprunteur emprunteur) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Emprunteur persistentEmprunteur = em.find(Emprunteur.class, emprunteur.getId());
            Collection<Emprunt> empruntCollectionOld = persistentEmprunteur.getEmpruntCollection();
            Collection<Emprunt> empruntCollectionNew = emprunteur.getEmpruntCollection();
            Collection<Reservation> reservationCollectionOld = persistentEmprunteur.getReservationCollection();
            Collection<Reservation> reservationCollectionNew = emprunteur.getReservationCollection();
            Collection<Emprunt> attachedEmpruntCollectionNew = new ArrayList<Emprunt>();
            for (Emprunt empruntCollectionNewEmpruntToAttach : empruntCollectionNew) {
                empruntCollectionNewEmpruntToAttach = em.getReference(empruntCollectionNewEmpruntToAttach.getClass(), empruntCollectionNewEmpruntToAttach.getId());
                attachedEmpruntCollectionNew.add(empruntCollectionNewEmpruntToAttach);
            }
            empruntCollectionNew = attachedEmpruntCollectionNew;
            emprunteur.setEmpruntCollection(empruntCollectionNew);
            Collection<Reservation> attachedReservationCollectionNew = new ArrayList<Reservation>();
            for (Reservation reservationCollectionNewReservationToAttach : reservationCollectionNew) {
                reservationCollectionNewReservationToAttach = em.getReference(reservationCollectionNewReservationToAttach.getClass(), reservationCollectionNewReservationToAttach.getId());
                attachedReservationCollectionNew.add(reservationCollectionNewReservationToAttach);
            }
            reservationCollectionNew = attachedReservationCollectionNew;
            emprunteur.setReservationCollection(reservationCollectionNew);
            emprunteur = em.merge(emprunteur);
            for (Emprunt empruntCollectionOldEmprunt : empruntCollectionOld) {
                if (!empruntCollectionNew.contains(empruntCollectionOldEmprunt)) {
                    empruntCollectionOldEmprunt.setEmprunteurId(null);
                    empruntCollectionOldEmprunt = em.merge(empruntCollectionOldEmprunt);
                }
            }
            for (Emprunt empruntCollectionNewEmprunt : empruntCollectionNew) {
                if (!empruntCollectionOld.contains(empruntCollectionNewEmprunt)) {
                    Emprunteur oldEmprunteurIdOfEmpruntCollectionNewEmprunt = empruntCollectionNewEmprunt.getEmprunteurId();
                    empruntCollectionNewEmprunt.setEmprunteurId(emprunteur);
                    empruntCollectionNewEmprunt = em.merge(empruntCollectionNewEmprunt);
                    if (oldEmprunteurIdOfEmpruntCollectionNewEmprunt != null && !oldEmprunteurIdOfEmpruntCollectionNewEmprunt.equals(emprunteur)) {
                        oldEmprunteurIdOfEmpruntCollectionNewEmprunt.getEmpruntCollection().remove(empruntCollectionNewEmprunt);
                        oldEmprunteurIdOfEmpruntCollectionNewEmprunt = em.merge(oldEmprunteurIdOfEmpruntCollectionNewEmprunt);
                    }
                }
            }
            for (Reservation reservationCollectionOldReservation : reservationCollectionOld) {
                if (!reservationCollectionNew.contains(reservationCollectionOldReservation)) {
                    reservationCollectionOldReservation.setEmprunteurId(null);
                    reservationCollectionOldReservation = em.merge(reservationCollectionOldReservation);
                }
            }
            for (Reservation reservationCollectionNewReservation : reservationCollectionNew) {
                if (!reservationCollectionOld.contains(reservationCollectionNewReservation)) {
                    Emprunteur oldEmprunteurIdOfReservationCollectionNewReservation = reservationCollectionNewReservation.getEmprunteurId();
                    reservationCollectionNewReservation.setEmprunteurId(emprunteur);
                    reservationCollectionNewReservation = em.merge(reservationCollectionNewReservation);
                    if (oldEmprunteurIdOfReservationCollectionNewReservation != null && !oldEmprunteurIdOfReservationCollectionNewReservation.equals(emprunteur)) {
                        oldEmprunteurIdOfReservationCollectionNewReservation.getReservationCollection().remove(reservationCollectionNewReservation);
                        oldEmprunteurIdOfReservationCollectionNewReservation = em.merge(oldEmprunteurIdOfReservationCollectionNewReservation);
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
                Integer id = emprunteur.getId();
                if (findEmprunteur(id) == null) {
                    throw new NonexistentEntityException("The emprunteur with id " + id + " no longer exists.");
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
            Emprunteur emprunteur;
            try {
                emprunteur = em.getReference(Emprunteur.class, id);
                emprunteur.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The emprunteur with id " + id + " no longer exists.", enfe);
            }
            Collection<Emprunt> empruntCollection = emprunteur.getEmpruntCollection();
            for (Emprunt empruntCollectionEmprunt : empruntCollection) {
                empruntCollectionEmprunt.setEmprunteurId(null);
                empruntCollectionEmprunt = em.merge(empruntCollectionEmprunt);
            }
            Collection<Reservation> reservationCollection = emprunteur.getReservationCollection();
            for (Reservation reservationCollectionReservation : reservationCollection) {
                reservationCollectionReservation.setEmprunteurId(null);
                reservationCollectionReservation = em.merge(reservationCollectionReservation);
            }
            em.remove(emprunteur);
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

    public List<Emprunteur> findEmprunteurEntities() {
        return findEmprunteurEntities(true, -1, -1);
    }

    public List<Emprunteur> findEmprunteurEntities(int maxResults, int firstResult) {
        return findEmprunteurEntities(false, maxResults, firstResult);
    }

    private List<Emprunteur> findEmprunteurEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Emprunteur.class));
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

    public Emprunteur findEmprunteur(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Emprunteur.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmprunteurCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Emprunteur> rt = cq.from(Emprunteur.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
