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
public class ReservationJpaController implements Serializable {

    public ReservationJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Reservation reservation) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Emprunteur emprunteurId = reservation.getEmprunteurId();
            if (emprunteurId != null) {
                emprunteurId = em.getReference(emprunteurId.getClass(), emprunteurId.getId());
                reservation.setEmprunteurId(emprunteurId);
            }
            Exemplaire exemplaireId = reservation.getExemplaireId();
            if (exemplaireId != null) {
                exemplaireId = em.getReference(exemplaireId.getClass(), exemplaireId.getId());
                reservation.setExemplaireId(exemplaireId);
            }
            em.persist(reservation);
            if (emprunteurId != null) {
                emprunteurId.getReservationCollection().add(reservation);
                emprunteurId = em.merge(emprunteurId);
            }
            if (exemplaireId != null) {
                exemplaireId.getReservationCollection().add(reservation);
                exemplaireId = em.merge(exemplaireId);
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

    public void edit(Reservation reservation) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Reservation persistentReservation = em.find(Reservation.class, reservation.getId());
            Emprunteur emprunteurIdOld = persistentReservation.getEmprunteurId();
            Emprunteur emprunteurIdNew = reservation.getEmprunteurId();
            Exemplaire exemplaireIdOld = persistentReservation.getExemplaireId();
            Exemplaire exemplaireIdNew = reservation.getExemplaireId();
            if (emprunteurIdNew != null) {
                emprunteurIdNew = em.getReference(emprunteurIdNew.getClass(), emprunteurIdNew.getId());
                reservation.setEmprunteurId(emprunteurIdNew);
            }
            if (exemplaireIdNew != null) {
                exemplaireIdNew = em.getReference(exemplaireIdNew.getClass(), exemplaireIdNew.getId());
                reservation.setExemplaireId(exemplaireIdNew);
            }
            reservation = em.merge(reservation);
            if (emprunteurIdOld != null && !emprunteurIdOld.equals(emprunteurIdNew)) {
                emprunteurIdOld.getReservationCollection().remove(reservation);
                emprunteurIdOld = em.merge(emprunteurIdOld);
            }
            if (emprunteurIdNew != null && !emprunteurIdNew.equals(emprunteurIdOld)) {
                emprunteurIdNew.getReservationCollection().add(reservation);
                emprunteurIdNew = em.merge(emprunteurIdNew);
            }
            if (exemplaireIdOld != null && !exemplaireIdOld.equals(exemplaireIdNew)) {
                exemplaireIdOld.getReservationCollection().remove(reservation);
                exemplaireIdOld = em.merge(exemplaireIdOld);
            }
            if (exemplaireIdNew != null && !exemplaireIdNew.equals(exemplaireIdOld)) {
                exemplaireIdNew.getReservationCollection().add(reservation);
                exemplaireIdNew = em.merge(exemplaireIdNew);
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
                Integer id = reservation.getId();
                if (findReservation(id) == null) {
                    throw new NonexistentEntityException("The reservation with id " + id + " no longer exists.");
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
            Reservation reservation;
            try {
                reservation = em.getReference(Reservation.class, id);
                reservation.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reservation with id " + id + " no longer exists.", enfe);
            }
            Emprunteur emprunteurId = reservation.getEmprunteurId();
            if (emprunteurId != null) {
                emprunteurId.getReservationCollection().remove(reservation);
                emprunteurId = em.merge(emprunteurId);
            }
            Exemplaire exemplaireId = reservation.getExemplaireId();
            if (exemplaireId != null) {
                exemplaireId.getReservationCollection().remove(reservation);
                exemplaireId = em.merge(exemplaireId);
            }
            em.remove(reservation);
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

    public List<Reservation> findReservationEntities() {
        return findReservationEntities(true, -1, -1);
    }

    public List<Reservation> findReservationEntities(int maxResults, int firstResult) {
        return findReservationEntities(false, maxResults, firstResult);
    }

    private List<Reservation> findReservationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Reservation.class));
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

    public Reservation findReservation(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Reservation.class, id);
        } finally {
            em.close();
        }
    }

    public int getReservationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Reservation> rt = cq.from(Reservation.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
