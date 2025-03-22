package com.salon.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.salon.service.entity.Location;

@Repository
public interface LocationDao extends JpaRepository<Location, Integer> {

}
