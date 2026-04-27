package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.VendorDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Vendor;
import com.CS7319.Group06.eventual.model.VendorReview;
import com.CS7319.Group06.eventual.service.VendorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Implementation of VendorService.
 */
@Service
public class VendorServiceImpl implements VendorService {

    private final VendorDao vendorDao;

    public VendorServiceImpl(VendorDao vendorDao) {
        this.vendorDao = vendorDao;
    }

    @Override
    public List<Vendor> getVendors(boolean preferredOnly) {
        try {
            return preferredOnly ? vendorDao.getPreferredVendors() : vendorDao.getAllVendors();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Vendor getVendorById(int id) {
        try {
            Vendor vendor = vendorDao.getVendorById(id);
            if (vendor == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found with id: " + id);
            }
            vendor.setReviews(vendorDao.getReviewsByVendorId(id));
            return vendor;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Vendor createVendor(Vendor vendor, String addedByEmail) {
        vendor.setAddedBy(addedByEmail);
        try {
            return vendorDao.createVendor(vendor);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Vendor updateVendor(int id, Vendor vendor) {
        try {
            Vendor existing = vendorDao.getVendorById(id);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found with id: " + id);
            }
            vendor.setVendorId(id);
            return vendorDao.updateVendor(vendor);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public VendorReview addReview(int vendorId, VendorReview review, String reviewerEmail) {
        try {
            Vendor vendor = vendorDao.getVendorById(vendorId);
            if (vendor == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found with id: " + vendorId);
            }
            review.setVendorId(vendorId);
            review.setReviewerEmail(reviewerEmail);
            return vendorDao.addReview(review);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<VendorReview> getReviews(int vendorId) {
        try {
            Vendor vendor = vendorDao.getVendorById(vendorId);
            if (vendor == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found with id: " + vendorId);
            }
            return vendorDao.getReviewsByVendorId(vendorId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<Vendor> getVendorsByEvent(int eventId) {
        try {
            return vendorDao.getVendorsByEventId(eventId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void addVendorToEvent(int eventId, int vendorId) {
        try {
            if (vendorDao.getVendorById(vendorId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found with id: " + vendorId);
            }
            vendorDao.addVendorToEvent(eventId, vendorId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void removeVendorFromEvent(int eventId, int vendorId) {
        try {
            vendorDao.removeVendorFromEvent(eventId, vendorId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void linkVendorsToEvent(int eventId, List<Integer> vendorIds) {
        if (vendorIds == null || vendorIds.isEmpty()) return;
        try {
            for (int vendorId : vendorIds) {
                vendorDao.addVendorToEvent(eventId, vendorId);
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
