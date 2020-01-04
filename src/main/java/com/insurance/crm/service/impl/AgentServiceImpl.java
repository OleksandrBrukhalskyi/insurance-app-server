package com.insurance.crm.service.impl;

import com.insurance.crm.constant.ErrorMessage;
import com.insurance.crm.constant.LogMessage;
import com.insurance.crm.dto.agent.AgentRoleDto;
import com.insurance.crm.dto.agent.AgentStatusDto;
import com.insurance.crm.dto.agent.AgentUpdateDto;
import com.insurance.crm.dto.agent.RoleDto;
import com.insurance.crm.entity.Agent;
import com.insurance.crm.entity.enums.AgentStatus;
import com.insurance.crm.entity.enums.Role;
import com.insurance.crm.exception.NotDeletedException;
import com.insurance.crm.exception.NotFoundException;
import com.insurance.crm.exception.NotUpdatedException;
import com.insurance.crm.repository.AgentRepository;
import com.insurance.crm.repository.FiliationRepository;
import com.insurance.crm.service.AgentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {
    @Autowired
    AgentRepository agentRepository;
    private FiliationRepository filiationRepository;
    private ModelMapper modelMapper;



    @Override
    public List<Agent> getAgents() {
        return agentRepository.findAll();
    }

    public Agent create(Agent agent) {
        log.info(LogMessage.IN_SAVE,agent);
        return agentRepository.save(agent);

    }

    @Override
    public Agent update(AgentUpdateDto dto,Long id) {
    log.info(LogMessage.IN_UPDATE);
    return agentRepository.findById(id)
            .map(agent -> {
                agent.setSurname(dto.getSurname());
                agent.setFirstname(dto.getFirstname());
                agent.setPatronymic(dto.getPatronymic());
                agent.setPassword(dto.getPassword());
                agent.setAge(dto.getAge());
                agent.setFiliation(filiationRepository.findById(dto.getFiliation().getId()).get());
                return agentRepository.save(agent);
            })
            .orElseThrow(()-> new NotUpdatedException(ErrorMessage.AGENT_NOT_UPDATED));


    }


    public void delete(Long id){
        log.info(LogMessage.IN_DELETE_BY_ID,id);
        if(!(agentRepository.findById(id).isPresent())){
            throw new NotDeletedException(ErrorMessage.AGENT_NOT_DELETED);
        }
        agentRepository.deleteById(id);
    }


    @Override
    public Agent getById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID,id);
        return agentRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ErrorMessage.AGENT_NOT_FOUND_BY_ID + id));
    }

    @Override
    public Agent getByEmail(String email) {
        log.info(LogMessage.IN_FIND_BY_EMAIL,email);
        return agentRepository.findAgentByEmail(email);
    }

    @Override
    public Agent findByAgentSurname(String surname) {
        return agentRepository.findAgentBySurname(surname);
    }
    @Override
    public AgentStatusDto updateStatus(Long id, AgentStatus agentStatus){
        log.info(LogMessage.IN_UPDATE,agentStatus);
        Agent agent = getById(id);
        agent.setAgentStatus(agentStatus);
        return modelMapper.map(agentRepository.save(agent),AgentStatusDto.class);
    }

    @Override
    public RoleDto getRoles() {
        log.info(LogMessage.IN_FIND_ALL);
        return new RoleDto(Role.class.getEnumConstants());
    }

    @Override
    public AgentRoleDto updateRole(Long id, Role role) {
        log.info(LogMessage.IN_UPDATE,role);
        Agent agent = getById(id);
        agent.setRole(role);
        return modelMapper.map(agentRepository.save(agent),AgentRoleDto.class);
    }

}
