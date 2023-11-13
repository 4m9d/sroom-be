package com.m9d.sroom.common.repository.recommend

class RecommendRepositorySql {
    public static final String GET_LIST_BY_DOMAIN = """
        SELECT source_code, is_playlist, domain
        FROM RECOMMEND
        WHERE domain = ?
    """
}
