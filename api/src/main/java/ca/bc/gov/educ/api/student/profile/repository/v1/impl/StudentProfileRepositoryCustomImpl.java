package ca.bc.gov.educ.api.student.profile.repository.v1.impl;

import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRepositoryCustom;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class StudentProfileRepositoryCustomImpl implements StudentProfileRepositoryCustom {

  @Getter(AccessLevel.PRIVATE)
  private final EntityManager entityManager;

  @Autowired
  StudentProfileRepositoryCustomImpl(final EntityManager em) {
    this.entityManager = em;
  }

  public List<StudentProfileEntity> findProfiles(UUID digitalID, String status, String pen) {
    final List<Predicate> predicates = new ArrayList<>();
    final CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
    final CriteriaQuery<StudentProfileEntity> criteriaQuery = criteriaBuilder.createQuery(StudentProfileEntity.class);
    Root<StudentProfileEntity> studentProfileEntityRoot = criteriaQuery.from(StudentProfileEntity.class);
    if (StringUtils.isNotBlank(status)) {
      predicates.add(criteriaBuilder.equal(studentProfileEntityRoot.get("studentRequestStatusCode"), status));
    }
    if (digitalID != null) {
      predicates.add(criteriaBuilder.equal(studentProfileEntityRoot.get("digitalID"), digitalID));
    }
    if (StringUtils.isNotBlank(pen)) {
      predicates.add(criteriaBuilder.equal(studentProfileEntityRoot.get("pen"), pen));
    }
    criteriaQuery.where(predicates.toArray(new Predicate[0]));

    return entityManager.createQuery(criteriaQuery).getResultList();
  }
}
