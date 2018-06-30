package banking.customers.infrastructure.hibernate.repository;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import banking.common.infrastructure.hibernate.repository.BaseHibernateRepository;
import banking.customers.domain.entity.Customer;
import banking.customers.domain.repository.CustomerRepository;

@Named
@Singleton
public class CustomerHibernateRepository extends BaseHibernateRepository<Customer> implements CustomerRepository {
	
	
	@Override
	public Customer findById(long id) throws Exception {
        Criteria criteria = getSession().createCriteria(Customer.class);

        //criteria.list();
        //criteria.add(Restrictions.eq("documentNumber", "10202366"));
        criteria.add(Restrictions.eq("id", id));

        //return (Customer)criteria.uniqueResult();
        return (Customer)criteria.uniqueResult();
//		Customer  customer = new Customer();//customerApplicationService.getCustomerById(customerId);
//		customer.setId(1);
//		customer.setFirstName("Felipe");
//		customer.setLastName("Llancachagua");
//		customer.setDocumentNumber("10558980");
//		return customer;
	}
	
	
	@Override
	public List<Customer> findAllPaginated(int pageNumber, int pageSize) {
		
		
		CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
		
		
		CriteriaQuery<Long> indexCriteria = criteriaBuilder.createQuery(Long.class);
		
		Root<Customer> fromIndex = indexCriteria.from(Customer.class);
		
		indexCriteria.multiselect(fromIndex.get("id"));
		
		TypedQuery<Long> indexQuery = getSession().createQuery(indexCriteria);
		
		indexQuery.setFirstResult((pageNumber - 1) * pageSize);
		indexQuery.setMaxResults(pageSize);
		
		List<Long> indexPaged = indexQuery.getResultList();
		
		// paged result
		CriteriaQuery<Customer> criteriaQuery = criteriaBuilder.createQuery(Customer.class);
		
		Root<Customer> from = criteriaQuery.from(Customer.class);
		
		CriteriaQuery<Customer> select = criteriaQuery.select(from);
		
		Predicate condition = from.get("id").in(indexPaged);
		
		

		TypedQuery<Customer> typedQuery = getSession().createQuery(select.where(condition));

		

		return typedQuery.getResultList();
	}
	
	@Override
	public long countAll() {

		CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();

		TypedQuery<Long> query = countAllQuery(criteriaBuilder);

		Long count = query.getSingleResult();

		return count;
	}

	private TypedQuery<Long> countAllQuery(CriteriaBuilder criteriaBuilder) {
		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
		
		countQuery.select(criteriaBuilder.count(countQuery.from(Customer.class)));
		
		TypedQuery<Long> query = getSession().createQuery(countQuery);
		return query;
	}	

}