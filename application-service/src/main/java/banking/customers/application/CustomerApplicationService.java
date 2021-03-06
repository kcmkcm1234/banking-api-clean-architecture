package banking.customers.application;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banking.common.application.EntityNotFoundResultException;
import banking.common.application.Notification;
import banking.common.application.dto.PaggedResponse;
import banking.common.application.enumeration.RequestBodyType;
import banking.customers.application.dto.CustomerDto;
import banking.customers.application.dto.UpdateCustomerDto;
import banking.customers.application.dto.mapper.CustomerToCustomerDtoMapper;
import banking.customers.domain.entity.Customer;
import banking.customers.domain.repository.CustomerRepository;
import banking.security.domain.entity.User;
import banking.security.domain.entity.UserRole;
import banking.security.domain.repository.UserRoleRepository;
import banking.security.domain.repository.UserRepository;

@Named
public class CustomerApplicationService {
	
	private Logger logger = LoggerFactory.getLogger(CustomerApplicationService.class);

	@Inject
	private CustomerRepository customerRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserRoleRepository roleRepository;

	@Inject
	CustomerToCustomerDtoMapper customerDtoMapper;

	@Transactional
	public Customer getCustomerById(long id) throws Exception {
		Notification notification = this.validation(id);
		if (notification.hasErrors()) {
			throw new IllegalArgumentException(notification.errorMessage());
		}
		Customer customer = this.customerRepository.findById(id);
		// Customer customer = this.customerRepository.findOne(id);
		return customer;
	}

	@Transactional
	public PaggedResponse<CustomerDto> findAllPaged(int pageNumber, int pageSize) throws Exception {
		Notification notification = this.validation(pageNumber, pageSize);
		if (notification.hasErrors()) {
			throw new IllegalArgumentException(notification.errorMessage());
		}
		List<Customer> customers = this.customerRepository.findAllPaginated(pageNumber, pageSize);

		if (customers == null || customers.size() == 0) {
			return null;
		}

		long totalRecords = this.customerRepository.countAll();

		PaggedResponse<CustomerDto> response = new PaggedResponse<CustomerDto>(customerDtoMapper.mapper(customers),
				pageNumber, pageSize, totalRecords);

		return response;
	}

	@Transactional
	public Customer getCustomerByDni(String dni) {
		Notification notification = this.validation(dni);
		if (notification.hasErrors()) {
			throw new IllegalArgumentException(notification.errorMessage());
		}
		Customer customer = this.customerRepository.findByDni(dni);
		return customer;
	}

	@Transactional
	public void save(CustomerDto dto) {

		
		Notification notification = this.validation(dto);
		try {
			User user = userRepository.findByUserName(dto.getUserName());
			
			notification.addError("User exist: " + user.getUsername());
			
		} catch (EntityNotFoundResultException e) {
			
		}
		
		try {
			Customer user = customerRepository.findByDni(dto.getDni());
			
			notification.addError("Customer with dni exist: " + user.getDocumentNumber());
			
		} catch (EntityNotFoundResultException e) {
			
		}

		if (notification.hasErrors()) {
			throw new IllegalArgumentException(notification.errorMessage());
		}

		User user = customerDtoMapper.userMapper(dto);
		
		logger.info(dto.toString());
		
		logger.info(user.toString());
		
		Customer customer = customerDtoMapper.reverseMapper(dto);
		
		this.customerRepository.save(customer);

		user.setCustomerId(customer.getId());

		userRepository.save(user);
		
		roleRepository.save((UserRole)user.getUserRole().toArray()[0]);

		dto.setId(customer.getId());

	}

	public UpdateCustomerDto update(UpdateCustomerDto dto) {
		Notification notification = this.validationUpdate(dto);

		if (notification.hasErrors()) {
			throw new IllegalArgumentException(notification.errorMessage());
		}
		

		Customer customer = this.customerRepository.findById(dto.getId());
		

		customer = customerDtoMapper.mergeDtoToCustomer(dto, customer);
		
		System.out.println("CustomerApplicationService.update()" + customer);

		// todo: save usuario

		this.customerRepository.save(customer);
		
		return dto;

	}

	private Notification validationUpdate(UpdateCustomerDto dto) {
		Notification notification = new Notification();
		if (dto == null || dto.getRequestBodyType() == RequestBodyType.INVALID) {
			notification.addError("Invalid JSON data in request body.");
		}
		return notification;
	}

	private Notification validation(String dni) {
		Notification notification = new Notification();
		if (StringUtils.isEmpty(dni)) {
			notification.addError("Customer dni must be present.");
		}
		return notification;
	}

	private Notification validation(int pageNumber, int pageSize) {
		Notification notification = new Notification();
		if (pageNumber <= 0) {
			notification.addError("Page must be greater than zero.");
		}
		if (pageSize <= 0) {
			notification.addError("Page size must be greater than zero.");
			return notification;
		}
		
		if (pageSize > 100) {
			notification.addError("Page size must not be greater than one hundred.");
			return notification;
		}
		return notification;
	}

	private Notification validation(long customerId) {
		Notification notification = new Notification();
		if (customerId <= 0) {
			notification.addError("Customer id must be greater than zero.");
		}
		return notification;
	}

	private Notification validation(CustomerDto dto) {
		Notification notification = new Notification();
		if (dto == null || dto.getRequestBodyType() == RequestBodyType.INVALID) {
			notification.addError("Invalid JSON data in request body.");
		}
		return notification;
	}
}
