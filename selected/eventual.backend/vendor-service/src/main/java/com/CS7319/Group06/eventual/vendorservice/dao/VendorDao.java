package com.CS7319.Group06.eventual.vendorservice.dao;

import com.CS7319.Group06.eventual.vendorservice.model.Vendor;
import com.CS7319.Group06.eventual.vendorservice.model.VendorReview;

import java.util.List;

/**
 * Data access operations for vendors and vendor reviews.
 */
public interface VendorDao {

    List<Vendor> getAllVendors();

    List<Vendor> getPreferredVendors();

    Vendor getVendorById(int id);

    Vendor createVendor(Vendor vendor);

    Vendor updateVendor(Vendor vendor);

    VendorReview addReview(VendorReview review);

    List<VendorReview> getReviewsByVendorId(int vendorId);

    List<Vendor> getVendorsByEventId(int eventId);

    void addVendorToEvent(int eventId, int vendorId);

    void removeVendorFromEvent(int eventId, int vendorId);
}
