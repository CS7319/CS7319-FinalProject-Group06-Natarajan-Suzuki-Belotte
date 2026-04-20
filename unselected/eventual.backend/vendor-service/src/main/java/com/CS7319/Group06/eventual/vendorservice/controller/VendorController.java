package com.CS7319.Group06.eventual.vendorservice.controller;

import com.CS7319.Group06.eventual.vendorservice.model.Vendor;
import com.CS7319.Group06.eventual.vendorservice.model.VendorReview;
import com.CS7319.Group06.eventual.vendorservice.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * REST controller for vendor management — add, update, review vendors and link them to events.
 */
@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // Get all vendors — pass ?preferred=true to return only preferred vendors
    @GetMapping
    public List<Vendor> getVendors(@RequestParam(defaultValue = "false") boolean preferred) {
        return vendorService.getVendors(preferred);
    }

    // Get a single vendor with its reviews
    @GetMapping("/{id}")
    public Vendor getVendorById(@PathVariable int id) {
        return vendorService.getVendorById(id);
    }

    // Add a new vendor — ORGANIZER only
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Vendor createVendor(@Valid @RequestBody Vendor vendor,
                               @RequestHeader("X-Authenticated-User") String currentUser,
                               @RequestHeader("X-User-Role") String role) {
        if (!"ORGANIZER".equals(role) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can add vendors");
        }
        return vendorService.createVendor(vendor, currentUser);
    }

    // Update an existing vendor — ORGANIZER only
    @PutMapping("/{id}")
    public Vendor updateVendor(@PathVariable int id,
                               @RequestBody Vendor vendor,
                               @RequestHeader("X-User-Role") String role) {
        if (!"ORGANIZER".equals(role) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can update vendors");
        }
        return vendorService.updateVendor(id, vendor);
    }

    // Post a review/comment on a vendor
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/reviews")
    public VendorReview addReview(@PathVariable int id,
                                  @Valid @RequestBody VendorReview review,
                                  @RequestHeader("X-Authenticated-User") String currentUser) {
        return vendorService.addReview(id, review, currentUser);
    }

    // Get all reviews for a vendor
    @GetMapping("/{id}/reviews")
    public List<VendorReview> getReviews(@PathVariable int id) {
        return vendorService.getReviews(id);
    }

    // Get all vendors linked to a specific event
    @GetMapping("/events/{eventId}")
    public List<Vendor> getVendorsByEvent(@PathVariable int eventId) {
        return vendorService.getVendorsByEvent(eventId);
    }

    // Link a vendor to an event — ORGANIZER only
    // Body: { "vendor_id": 3 }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/events/{eventId}")
    public void addVendorToEvent(@PathVariable int eventId,
                                 @RequestBody Map<String, Integer> body,
                                 @RequestHeader("X-User-Role") String role) {
        if (!"ORGANIZER".equals(role) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can link vendors to events");
        }
        Integer vendorId = body.get("vendor_id");
        if (vendorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vendor_id is required");
        }
        vendorService.addVendorToEvent(eventId, vendorId);
    }

    // Remove a vendor from an event — ORGANIZER only
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/events/{eventId}/{vendorId}")
    public void removeVendorFromEvent(@PathVariable int eventId,
                                      @PathVariable int vendorId,
                                      @RequestHeader("X-User-Role") String role) {
        if (!"ORGANIZER".equals(role) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can remove vendors from events");
        }
        vendorService.removeVendorFromEvent(eventId, vendorId);
    }
}
