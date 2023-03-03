package com.rjs.cms.repo;

import com.rjs.cms.model.TableInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableInfoRepo extends JpaRepository<TableInfo, TableInfo.TableInfoCompositeKey> {
    public List<TableInfo> findAllByOrderByTableInfoCompositeKeyTableNameAscTableInfoCompositeKeyFieldAsc();
}
