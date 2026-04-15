package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.SearchDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.EventSearchRequest;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.GroupSearchRequest;
import com.CS7319.Group06.eventual.model.search.SearchResult;
import com.CS7319.Group06.eventual.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation for SearchService
 *
 * @author harininatarajan
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
