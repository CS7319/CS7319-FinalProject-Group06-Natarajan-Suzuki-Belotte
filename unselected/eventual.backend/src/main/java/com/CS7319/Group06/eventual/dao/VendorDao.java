package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.Vendor;
import com.CS7319.Group06.eventual.model.VendorReview;

import java.util.List;

/**
 * Data access operations for vendors and vendor reviews.
 */
public interface VendorDao {

  /**
   * To retrieve all the vendors
   *
   * @return
   */
  List<Vendor> getAllVendors();

  /**
   * To retrieve only prederred vendors
   *
   * @return
   */
  List<Vendor> getPreferredVendors();

  /**
   * Get a vendor by id
   *
   * @param id
   * @return
   */
  Vendor getVendorById(int id);

  /**
   * Create a new vendor
   *
   * @param vendor
   * @return
   */
  Vendor createVendor(Vendor vendor);

  /**
   * Edit an existing vendor
   *
   * @param vendor
   * @return
   */
  Vendor updateVendor(Vendor vendor);

  /**
   * Add review
   *
   * @param review
   * @return
   */
  VendorReview addReview(VendorReview review);

  /**
   * Get review for the vendor
   *
   * @param vendorId
   * @return
   */
  List<VendorReview> getReviewsByVendorId(int vendorId);

  /**
   * Get vendors by event id
   *
   * @param eventId
   * @return
   */
  List<Vendor> getVendorsByEventId(int eventId);

  /**
   * Add vendor to the event
   *
   * @param eventId
   * @param vendorId
   */
  void addVendorToEvent(int eventId, int vendorId);

  /**
   * Remove vendor from the event
   *
   * @param eventId
   * @param vendorId
   */
  void removeVendorFromEvent(int eventId, int vendorId);
}
