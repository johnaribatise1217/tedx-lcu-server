package tedxlcu.ticketing.payments.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tedxlcu.ticketing.payments.Request.createTicketsReq;
import tedxlcu.ticketing.payments.Response.ApiResponse;
import tedxlcu.ticketing.payments.model.Tickets;
import tedxlcu.ticketing.payments.service.Tickets.ITicketsService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/tickets")
public class TicketController {
  @Autowired
  private ITicketsService ticketsService;

  @PostMapping("/create")
  public ResponseEntity<ApiResponse> createTicket(@RequestBody createTicketsReq req){
    ticketsService.CreateTicket(req);
    return ResponseEntity.status(
      HttpStatus.CREATED
    ).body(new ApiResponse(true, "201", "Created successfully", null));
  }

  @GetMapping("/get-all")
  public ResponseEntity<ApiResponse> getAllTickets(){
    List<Tickets> ticketsList = ticketsService.GetAllTickets();
    return ResponseEntity.status(
      HttpStatus.OK
    ).body(new ApiResponse(true, "200", "Fetched successfully", ticketsList));
  }
}
