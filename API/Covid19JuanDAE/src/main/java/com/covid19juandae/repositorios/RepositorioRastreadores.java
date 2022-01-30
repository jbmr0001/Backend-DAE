/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.repositorios;

import com.covid19juandae.entidades.Rastreador;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jmait
 */

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioRastreadores {
    @PersistenceContext
    EntityManager em;
    
    /**
     * @brief Buscar Rastreador con dni dado
     * @return Optional<Rastreador>
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)    
    public Optional<Rastreador> buscar(String dni) {
        return Optional.ofNullable(em.find(Rastreador.class, dni));
    }
    
    /**
     * @param rastreador
     * @brief guarda el rastreador en el repositorio
     */
    public void guardar(Rastreador rastreador) {
        em.persist(rastreador);
    }
        
    /**
     * @param rastreador
     * @brief update rastereador
     */
    public void actualizar(Rastreador rastreador) {
        em.merge(rastreador);
    }
    
    /**
     * @param dni
     * @brief devuelve el numero de usuarios reportados por el rastreador dado
     * @return int numUsuarios
     */
    public int verPositivosReportados(String dni){
        Optional<Rastreador> rast=buscar(dni);
        return rast.get().getNumUsuariosReportados();
    }
    
}
