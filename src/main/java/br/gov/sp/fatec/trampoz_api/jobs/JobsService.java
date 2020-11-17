package br.gov.sp.fatec.trampoz_api.jobs;

import br.gov.sp.fatec.trampoz_api.contractors.ContractorEntity;
import br.gov.sp.fatec.trampoz_api.helpers.PaginationHelper;
import br.gov.sp.fatec.trampoz_api.shared.GenericService;
import org.hibernate.Session;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

public class JobsService extends GenericService implements JobsDao {

    @Override
    public JobEntity findById(UUID id) {
        return this.entityManager.find(JobEntity.class, id);
    }

    @Override
    public List<JobEntity> findAll(Integer page, Integer limit) {
        String jpql = "select j from JobEntity j order by publishingDate desc";
        TypedQuery<JobEntity> typedQuery = entityManager.createQuery(jpql, JobEntity.class);

        PaginationHelper paginationHelper = new PaginationHelper(page, limit);

        int firstResult = paginationHelper.getOffset();
        typedQuery.setFirstResult(firstResult);
        typedQuery.setMaxResults(limit);

        return typedQuery.getResultList();
    }

    @Override
    public Long getTotal() {
        Query queryTotal = entityManager.createQuery("select count(j.id) from JobEntity j");
        Long total = (Long) queryTotal.getSingleResult();
        return total;
    }

    @Override
    public void create(JobEntity job, ContractorEntity contractor) {
        job.setContractor(contractor);
        this.entityManager.persist(job);
    }

    @Override
    public void createAndCommit(JobEntity job, ContractorEntity contractor) {
        try {
            entityManager.getTransaction().begin();
            this.create(job, contractor);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            error.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void update(JobEntity job) {
        entityManager.merge(job);
    }

    @Override
    public void updateAndCommit(JobEntity job) {
        try {
            entityManager.getTransaction().begin();
            this.update(job);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            error.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void delete(UUID id) {
        JobEntity foundJob = findById(id);
        if (foundJob == null) throw new RuntimeException("Job don't exists");
        entityManager.remove(foundJob);
    }

    @Override
    public void deleteAndCommit(UUID id) {
        try {
            entityManager.getTransaction().begin();
            this.delete(id);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            error.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }
}
