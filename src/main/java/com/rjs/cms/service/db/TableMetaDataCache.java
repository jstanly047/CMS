package com.rjs.cms.service.db;

import com.rjs.cms.model.enity.TableInfo;
import com.rjs.cms.model.common.ColumnMetaData;
import com.rjs.cms.model.common.TableMetaData;
import com.rjs.cms.repo.TableInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TableMetaDataCache {
    private final TableInfoRepo tableInfoRepo;
    ThreadLocal<Map<String, TableMetaData>> tablesInfoCache;
    static AtomicInteger metaDataCount = new AtomicInteger();

    @Autowired
    public TableMetaDataCache(TableInfoRepo tableInfoRepo){
        this.tableInfoRepo = tableInfoRepo;
    }

    final TableMetaData getTableMeta(String table){
        return tablesInfoCache.get().get(table);
    }
    public void populateCache(){
        tablesInfoCache = new ThreadLocal<>();
        tablesInfoCache.set(new HashMap<>());
        List<TableInfo> tableInfos = tableInfoRepo.findAllByOrderByTableInfoCompositeKeyTableNameAscTableInfoCompositeKeyFieldAsc();

        for (TableInfo tableInfo: tableInfos){
            if (tablesInfoCache.get().containsKey(tableInfo.getTableInfoCompositeKey().getTableName()))
            {
                TableMetaData tableMetaData = tablesInfoCache.get().get(tableInfo.getTableInfoCompositeKey().getTableName());
                tableMetaData.put(new ColumnMetaData(tableInfo));
                continue;
            }

            TableMetaData tableMetaData = new TableMetaData(tableInfo.getTableInfoCompositeKey().getTableName());
            tablesInfoCache.get().put(tableInfo.getTableInfoCompositeKey().getTableName(), tableMetaData);
            tableMetaData.put(new ColumnMetaData(tableInfo));
        }

        metaDataCount.set(tablesInfoCache.get().size());
    }

    public void reloadCache(){
        populateCache();
    }
    public boolean contains(String table){
        return tablesInfoCache.get().containsKey(table);
    }
}
