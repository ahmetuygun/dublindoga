package com.hiking.dublindoga.service.impl;

import com.hiking.dublindoga.domain.Joiner;
import com.hiking.dublindoga.repository.JoinerRepository;
import com.hiking.dublindoga.service.JoinerService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hiking.dublindoga.domain.Joiner}.
 */
@Service
@Transactional
public class JoinerServiceImpl implements JoinerService {

    private static final Logger LOG = LoggerFactory.getLogger(JoinerServiceImpl.class);

    private final JoinerRepository joinerRepository;

    public JoinerServiceImpl(JoinerRepository joinerRepository) {
        this.joinerRepository = joinerRepository;
    }

    @Override
    public Joiner save(Joiner joiner) {
        LOG.debug("Request to save Joiner : {}", joiner);
        return joinerRepository.save(joiner);
    }

    @Override
    public Joiner update(Joiner joiner) {
        LOG.debug("Request to update Joiner : {}", joiner);
        return joinerRepository.save(joiner);
    }

    @Override
    public Optional<Joiner> partialUpdate(Joiner joiner) {
        LOG.debug("Request to partially update Joiner : {}", joiner);

        return joinerRepository
            .findById(joiner.getId())
            .map(existingJoiner -> {
                if (joiner.getFullName() != null) {
                    existingJoiner.setFullName(joiner.getFullName());
                }
                if (joiner.getEmail() != null) {
                    existingJoiner.setEmail(joiner.getEmail());
                }
                if (joiner.getPhone() != null) {
                    existingJoiner.setPhone(joiner.getPhone());
                }
                if (joiner.getStatus() != null) {
                    existingJoiner.setStatus(joiner.getStatus());
                }
                if (joiner.getPhoto1() != null) {
                    existingJoiner.setPhoto1(joiner.getPhoto1());
                }
                if (joiner.getPhoto1ContentType() != null) {
                    existingJoiner.setPhoto1ContentType(joiner.getPhoto1ContentType());
                }
                if (joiner.getGender() != null) {
                    existingJoiner.setGender(joiner.getGender());
                }
                if (joiner.getPoint() != null) {
                    existingJoiner.setPoint(joiner.getPoint());
                }

                return existingJoiner;
            })
            .map(joinerRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Joiner> findAll(Pageable pageable) {
        LOG.debug("Request to get all Joiners");
        return joinerRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Joiner> findOne(Long id) {
        LOG.debug("Request to get Joiner : {}", id);
        return joinerRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Joiner : {}", id);
        joinerRepository.deleteById(id);
    }
}
