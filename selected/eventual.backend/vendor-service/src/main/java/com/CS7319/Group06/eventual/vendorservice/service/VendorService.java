package com.CS7319.Group06.eventual.vendorservice.service;

import com.CS7319.Group06.eventual.vendorservice.model.Vendor;
import com.CS7319.Group06.eventual.vendorservice.model.VendorReview;

import java.util.List;

/**
 * Service interface for vendor management operations.
 */
public interface VendorService {

    List<Vendor> getVendors(boolean preferredOnly);

    Vendor getVendorById(int id);

    Vendor createVendor(Vendor vendor, String addedByEmail);

    Vendor updateVendor(int id, Vendor vendor);

    VendorReview addReview(int vendorId, VendorReview review, String reviewerEmail);

    List<VendorReview> getReviews(int vendorId);

    List<Vendor> getVendorsByEvent(int eventId);

    void addVendorToEvent(int eventId, int vendorId);

    void removeVendorFromEvent(int eventId, int vendorId);
}
