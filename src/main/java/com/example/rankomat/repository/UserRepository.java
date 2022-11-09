package com.example.rankomat.repository;

import com.example.rankomat.model.user.*;
import org.springframework.data.jpa.repository.*;

public interface UserRepository extends JpaRepository<RecordEntity,Integer> {

}
