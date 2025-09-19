package tedxlcu.ticketing.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tedxlcu.ticketing.payments.Exception.ForbiddenException;
import tedxlcu.ticketing.payments.Request.CreateDiscountWindow;
import tedxlcu.ticketing.payments.Response.ApiResponse;
import tedxlcu.ticketing.payments.security.user.AdminUserDetails;
import tedxlcu.ticketing.payments.service.Discount.IDiscountService;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
  @Autowired
  private IDiscountService discountService;

  private String getCurrentUserId(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof AdminUserDetails) {
      return ((AdminUserDetails) principal).getId();
    } else {
      throw new IllegalStateException("Unexpected principal type: ");
    }
  }

  @GetMapping("/open-discount")
  public ResponseEntity<ApiResponse> listOpen() {
    return ResponseEntity.ok().body(
      new ApiResponse(true, "200", "fetched", discountService.listOpenWindows())
    );
  }

  @GetMapping("/validate/{code}")
  public ResponseEntity<ApiResponse> validateCode(@PathVariable String code){
    return ResponseEntity.ok().body(
      new ApiResponse(true, "200", "code is valid", discountService.validateCode(code))
    );
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
  
  @PostMapping("/create")
  public ResponseEntity<ApiResponse> create(
    @RequestBody CreateDiscountWindow window, 
    Authentication authentication
  ) {
    isAdmin(authentication);
    String userId = getCurrentUserId(authentication);
    discountService.createWindow(window, userId);
    return ResponseEntity.status(
      HttpStatus.CREATED
    ).body(new ApiResponse(true, "201", "Discount window created successfully", null));
  }

  @DeleteMapping("/{id}/delete")
  public ResponseEntity<ApiResponse> delete(@PathVariable String id, Authentication authentication) {
    isAdmin(authentication);
    discountService.deleteWindow(id);
    return ResponseEntity.ok().body(new ApiResponse(true, "200", "Deleted successfully", null));
  }

  @PutMapping("/{id}/refresh-code")
  public ResponseEntity<ApiResponse>
  refresh(@PathVariable String id, Authentication authentication) {
    isAdmin(authentication);
    String userId = getCurrentUserId(authentication);
    discountService.refreshCode(id, userId);
    return ResponseEntity.ok().body(
      new ApiResponse(true, "200", "Refreshed successfully", null)
    );
  }
}
