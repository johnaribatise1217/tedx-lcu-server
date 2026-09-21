package tedxlcu.ticketing.payments.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tedxlcu.ticketing.payments.DTO.TicketAdminDetails;
import tedxlcu.ticketing.payments.Exception.ForbiddenException;
import tedxlcu.ticketing.payments.Request.createTicketsReq;
import tedxlcu.ticketing.payments.Response.ApiResponse;
import tedxlcu.ticketing.payments.model.Tickets;
import tedxlcu.ticketing.payments.security.user.AdminUserDetails;
import tedxlcu.ticketing.payments.service.Tickets.ITicketsService;

@RestController
@RequiredArgsConstructor 
@RequestMapping("/api/tickets")
public class TicketController {
  private final ITicketsService ticketsService;

  private String getCurrentUserId(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof AdminUserDetails) {
      return ((AdminUserDetails) principal).getId();
    } else {
      throw new IllegalStateException("Unexpected principal type: ");
    }
  }

  private boolean isAdmin(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (!(principal instanceof AdminUserDetails)) {
      throw new ForbiddenException("You are not authorized to perform this action");
    }
    AdminUserDetails user = (AdminUserDetails) principal;

    // debug: list authorities (remove or reduce logging in production)
    var authNames = user.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .toList();

    // strict match — adjust to the exact granted authority your app uses ("ROLE_ADMIN" or "ADMIN")
    boolean ok = authNames.stream().anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));

    if (!ok) {
      throw new ForbiddenException("You are not authorized to perform this action. Your role: " + authNames);
    }
    return true;
  }

  @PostMapping("/admin/create")
  public ResponseEntity<ApiResponse> createTicket(@RequestBody createTicketsReq req, Authentication authentication){
    isAdmin(authentication);
    ticketsService.CreateTicket(req);
    return ResponseEntity.status(
      HttpStatus.CREATED
    ).body(new ApiResponse(true, "201", "Created successfully", null));
  }

  @PatchMapping("/admin/update/{ticketId}")
  public ResponseEntity<ApiResponse> UpdateTicket(@RequestBody createTicketsReq req, @PathVariable String ticketId, Authentication authentication){
    isAdmin(authentication);
    ticketsService.updateTicket(req, ticketId);
    return ResponseEntity.status(
      HttpStatus.OK
    ).body(new ApiResponse(true, "200", "updated successfully", null));
  }

  @GetMapping("/get-all")
  public ResponseEntity<ApiResponse> getAllTickets(){
    List<Tickets> ticketsList = ticketsService.GetAllTickets();
    return ResponseEntity.status(
      HttpStatus.OK
    ).body(new ApiResponse(true, "200", "Fetched successfully", ticketsList));
  }

  @GetMapping("/generate-qrcode")
  public ResponseEntity<ApiResponse> generateTicketQRcode(@RequestParam String email, @RequestParam String trxref){
    String qrCodeUrl = ticketsService.generateTicketQRcode(email, trxref);
    return ResponseEntity.status(
      HttpStatus.OK
    ).body(new ApiResponse(true, "200", "QR Code generated successfully", qrCodeUrl));
  } 

  @GetMapping("/admin/get-single/{id}")
  public ResponseEntity<ApiResponse> getSingleTicket(@PathVariable String id){
    return ResponseEntity.ok().body(
      new ApiResponse(true, "200", "Single fetched", ticketsService.getTicketById(id))
    );
  }

  @GetMapping("/admin/bookings")
  public ResponseEntity<ApiResponse> getAllBookingsForAdmin(){
    TicketAdminDetails details = ticketsService.getAllBookingsForAdmin();
    return ResponseEntity.status(
      HttpStatus.OK
    ).body(new ApiResponse(true, "200", "Fetched successfully", details));
  }

  @PutMapping("/admin/verify/{id}")
  public ResponseEntity<ApiResponse> verifyTicketBooking(Authentication authentication, @PathVariable String id){// Debug log
    String userId = getCurrentUserId(authentication);
    boolean isVerified = ticketsService.verifyTicketBooking(id, userId);
    return ResponseEntity.status(
      HttpStatus.OK
    ).body(new ApiResponse(true, "200", "Ticket verified successfully", isVerified));
  }
}