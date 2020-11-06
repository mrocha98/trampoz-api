package br.gov.sp.fatec.trampoz_api.shared;

import javax.persistence.EntityManager;

public abstract class GenericService {
    protected final EntityManager entityManager;

    public GenericService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public GenericService() {
        this(PersistenceManager.getInstance().getEntityManager());
    }
}
