package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.Vendor;
import com.CS7319.Group06.eventual.model.VendorReview;

import java.util.List;

/**
 * Service interface for vendor management operations.
 */
public interface VendorService {

  /**
   * Get a list of preffered vendors
   *
   * @param preferredOnly
   * @return
   */
  List<Vendor> getVendors(boolean preferredOnly);

  /**
   * Get vendor by id
   *
   * @param id
   * @return
   */
  Vendor getVendorById(int id);

  /**
   * Create a vendor
   *
   * @param vendor
   * @param addedByEmail
   * @return
   */
  Vendor createVendor(Vendor vendor, String addedByEmail);

  /**
   * Update a vendor
   *
   * @param id
   * @param vendor
   * @return
   */
  Vendor updateVendor(int id, Vendor vendor);

  /**
   * Add reviews for the vendor
   *
   * @param vendorId
   * @param review
   * @param reviewerEmail
   * @return
   */
  VendorReview addReview(int vendorId, VendorReview review, String reviewerEmail);

  /**
   * Get reviews of the vendor
   *
   * @param vendorId
   * @return
   */
  List<VendorReview> getReviews(int vendorId);

  /**
   * Get vendors for an event
   *
   * @param eventId
   * @return
   */
  List<Vendor> getVendorsByEvent(int eventId);

  /**
   * Add vendor to an event
   *
   * @param eventId
   * @param vendorId
   */
  void addVendorToEvent(int eventId, int vendorId);

  /**
   * Remove a vendor from an event
   *
   * @param eventId
   * @param vendorId
   */
  void removeVendorFromEvent(int eventId, int vendorId);

  /**
   * Link vendors to an event
   *
   * @param eventId
   * @param vendorIds
   */
  void linkVendorsToEvent(int eventId, List<Integer> vendorIds);
}
