/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sf.fess.db.bsentity.customize.dbmeta;

import java.util.List;
import java.util.Map;

import jp.sf.fess.db.allcommon.DBCurrent;
import jp.sf.fess.db.allcommon.DBFluteConfig;
import jp.sf.fess.db.exentity.customize.SearchQueryRanking;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of SearchQueryRanking. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class SearchQueryRankingDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final SearchQueryRankingDbm _instance = new SearchQueryRankingDbm();

    private SearchQueryRankingDbm() {
    }

    public static SearchQueryRankingDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    @Override
    public DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, new EpgName(), "name");
        setupEpg(_epgMap, new EpgCnt(), "cnt");
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchQueryRanking) et).getName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchQueryRanking) et).setName((String) vl);
        }
    }

    public static class EpgCnt implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchQueryRanking) et).getCnt();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchQueryRanking) et).setCnt(ctl(vl));
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "SearchQueryRanking";

    protected final String _tablePropertyName = "searchQueryRanking";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "SearchQueryRanking", _tableDbName);
    {
        _tableSqlName.xacceptFilter(DBFluteConfig.getInstance()
                .getTableSqlNameFilter());
    }

    @Override
    public String getTableDbName() {
        return _tableDbName;
    }

    @Override
    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    @Override
    public TableSqlName getTableSqlName() {
        return _tableSqlName;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            String.class, "name", null, false, false, false, "VARCHAR", 1000,
            0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnCnt = cci("CNT", "CNT", null, null,
            Long.class, "cnt", null, false, false, false, "BIGINT", 19, 0,
            null, false, null, null, null, null, null);

    /**
     * NAME: {VARCHAR(1000), refers to SEARCH_FIELD_LOG.VALUE}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnName() {
        return _columnName;
    }

    /**
     * CNT: {BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCnt() {
        return _columnCnt;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnName());
        ls.add(columnCnt());
        return ls;
    }

    {
        initializeInformationResource();
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    @Override
    protected UniqueInfo cpui() {
        throw new UnsupportedOperationException(
                "The table does not have primary key: " + getTableDbName());
    }

    @Override
    public boolean hasPrimaryKey() {
        return false;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "jp.sf.fess.db.exentity.customize.SearchQueryRanking";
    }

    @Override
    public String getConditionBeanTypeName() {
        return null;
    }

    @Override
    public String getBehaviorTypeName() {
        return null;
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<SearchQueryRanking> getEntityType() {
        return SearchQueryRanking.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public SearchQueryRanking newEntity() {
        return new SearchQueryRanking();
    }

    public SearchQueryRanking newMyEntity() {
        return new SearchQueryRanking();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((SearchQueryRanking) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((SearchQueryRanking) et, mp);
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(final Entity et) {
        return doExtractPrimaryKeyMap(et);
    }

    @Override
    public Map<String, Object> extractAllColumnMap(final Entity et) {
        return doExtractAllColumnMap(et);
    }
}