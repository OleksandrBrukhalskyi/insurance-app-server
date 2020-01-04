package com.insurance.crm.service.impl;

import com.insurance.crm.constant.ErrorMessage;
import com.insurance.crm.constant.LogMessage;
import com.insurance.crm.dto.customer.CustomerDto;
import com.insurance.crm.entity.Customer;
import com.insurance.crm.exception.BadIdException;
import com.insurance.crm.exception.NotDeletedException;
import com.insurance.crm.exception.NotFoundException;
import com.insurance.crm.repository.CustomerRepository;
import com.insurance.crm.service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    CustomerRepository customerRepository;
    private ModelMapper modelMapper;

    @Override
    public List<CustomerDto> getCustomers() {
        log.info(LogMessage.IN_FIND_ALL);
        return modelMapper.map(customerRepository.findAll(), new TypeToken<List<CustomerDto>>(){
        }.getType());
    }

    @Override
    public Customer create(CustomerDto dto) {
        log.info(LogMessage.IN_SAVE,dto);
        return customerRepository.save(modelMapper.map(dto, Customer.class));
    }

    @Override
    public Customer update(CustomerDto dto, Long id) {
        log.info(LogMessage.IN_UPDATE);
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setSurname(dto.getSurname());
                    customer.setFirstname(dto.getFirstname());
                    customer.setPatronymic(dto.getPatronymic());
                    customer.setHomeAddress(dto.getHomeAddress());
                    customer.setPhoneNum(dto.getHomeNumber());
                    customer.setAge(dto.getAge());
                    return customerRepository.save(customer);
                })
                .orElseThrow(()-> new NotFoundException(ErrorMessage.CUSTOMER_NOT_FOUND_BY_ID + id));
    }



    @Override
    public void delete(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);
        if(!(customerRepository.findById(id).isPresent())){
            throw new NotDeletedException(ErrorMessage.CUSTOMER_NOT_DELETED);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public Optional<Customer> getById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);
        return Optional.ofNullable(customerRepository.findById(id)
                .orElseThrow(() -> new BadIdException(ErrorMessage.CUSTOMER_NOT_FOUND_BY_ID + id)));
    }

    @Override
    public Customer findByCustomerSurname(String surname) {
        log.info(LogMessage.IN_FIND_BY_SURNAME,surname);
        return customerRepository.findCustomerBySurname(surname);
    }
}
