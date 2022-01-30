/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.repositorios;

import com.covid19juandae.entidades.Contacto;
import com.covid19juandae.entidades.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Pc
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioUsuarios {
    @PersistenceContext
    EntityManager em;
    
    static final LocalDateTime PASADO_DISTANTE = LocalDateTime.of(1970, 1, 1, 0, 0);
    static final LocalDateTime FUTURO_DISTANTE = LocalDateTime.of(2100, 1, 1, 0, 0);
    
    /**
     * @param uuid
     * @brief Buscar Usuario con UUID dado
     * @return Optional<Usuario>
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Usuario> buscar(UUID uuid) {
        return Optional.ofNullable(em.find(Usuario.class, uuid));
    }
    
    /**
     * @param usuario
     * @brief guarda el usuario en el repositorio
     */
    public void guardar(Usuario usuario) {
        em.persist(usuario);
    }

    /**
     * @param usuario
     * @brief update usuario con los datos dados
     */
    public void actualizar(Usuario usuario) {
        em.merge(usuario);
    }
    
    /**
     * @brief devuelve el numero de usuarios positivos
     * @return int usuarios
     */
    public int verNumPositivos(){
        return (int)(long)em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.positivo= TRUE").getSingleResult();   
    }
    
    /**
     * @brief devuelve el numero de usuarios positivos de la ultima quincena desde la fecha actual
     * @return int usuarios
     */
    public int verNumPositivosQuincena(){
        Query q = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.positivo= TRUE AND  u.fPositivo between :desde and :hasta");
        
        q.setParameter("desde", LocalDateTime.now().minusDays(15));
        q.setParameter("hasta", LocalDateTime.now());
        
        return (int)(long)q.getSingleResult();
    }
    
    /**
     * @brief devuelve el numero de usuarios que alguna vez han sido positivos
     * @return int usuarios
     */
    public int verNumInfectadosTotales(){
        
        return (int)(long)em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.fPositivo IS NOT NULL").getSingleResult();
    }
    
    /**
     * @param u
     * @param c
     * @brief inserta contacto asignado al usuario
     */
    public void insertarContacto(Usuario u,Contacto c){
        em.persist(c);
        em.merge(u);
    }
    
    /**
     * @brief borra los contactos obsoletos de hace mas de un mes
     */
    public void borrarContactos31Dias(){
        Query q = em.createQuery("DELETE FROM Contacto c WHERE c.fecha > :desde");
        
        q.setParameter("desde",LocalDateTime.now().minusDays(31));
    }

    /**
     * @brief devuelve el numero medio de contactos positivos por usuario
     * @return contactos/usuarios
     */
    public int mediaContactosPositivos(){
        //falta cambiar la estadistica
        //int numPositivos= (int)(long) em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.positivo= TRUE").getSingleResult();
        //int numContactos=(int)(long)em.createQuery("SELECT COUNT(c) FROM Contacto c ").getSingleResult();
        //return numContactos/numPositivos;
        Query q= em.createQuery("SELECT u FROM Usuario u WHERE u.positivo=TRUE");
        List<Usuario> list=q.getResultList();
        int contactosPositivos=0;
        for(Usuario usuario:list){
            Query q1= em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.uuidUsuario IN(SELECT c FROM Contacto c WHERE c.usuario=:uuid) AND u.fPositivo BETWEEN :desde AND :hasta");
            q1.setParameter("uuid", usuario);
            q1.setParameter("desde", usuario.getfPositivo());
            q1.setParameter("hasta", usuario.getfPositivo().plusDays(15));
            contactosPositivos+=(int)(long)q1.getSingleResult();
            
        }
        return contactosPositivos/list.size();
    }
  
    /**
     * @param uuidUsuario
     * @brief obtiene la lista de contactos cercanos al usuario con UUID dado
     * @return List<Contacto> l
     */
    public List<Contacto> getListaContactosCercanos(UUID uuidUsuario){
        List<Contacto> l;
        Optional<Usuario> u = buscar(uuidUsuario);
       
        Query q = em.createQuery("SELECT c FROM Contacto c WHERE c.usuario=:usu AND c.fecha BETWEEN :desde AND :hasta");
        q.setParameter("desde", u.get().getfPositivo().minusDays(14));
        q.setParameter("hasta", LocalDateTime.now());
        System.out.println(u.get().getfPositivo().minusDays(14).toString());
        System.out.println(LocalDateTime.now().toString());
        q.setParameter("usu", u.get());
        
        l = q.getResultList();
            
        return l;
    }
   
}