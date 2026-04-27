package com.CS7319.Group06.eventual.searchservice.service.impl;

import com.CS7319.Group06.eventual.searchservice.dao.SearchDao;
import com.CS7319.Group06.eventual.searchservice.exception.DaoException;
import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.EventSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.SearchResult;
import com.CS7319.Group06.eventual.searchservice.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation for SearchService
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final SearchDao searchDao;

    public SearchServiceImpl(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    @Override
    public SearchResult<EventDocument> searchEvents(EventSearchRequest request) {
        validatePagination(request.getPage(), request.getSize());
        try {
            return searchDao.searchEvents(request);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public SearchResult<GroupDocument> searchGroups(GroupSearchRequest request) {
        validatePagination(request.getPage(), request.getSize());
        try {
            return searchDao.searchGroups(request);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be 0 or greater");
        }
        //limited to 100 to avoid causing memory issue
        if (size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be between 1 and 100");
        }
    }
}
