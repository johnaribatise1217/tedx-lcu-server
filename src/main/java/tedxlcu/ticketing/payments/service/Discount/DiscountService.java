package tedxlcu.ticketing.payments.service.Discount;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tedxlcu.ticketing.payments.Exception.AlreadyExistsException;
import tedxlcu.ticketing.payments.Exception.DiscountExpiredException;
import tedxlcu.ticketing.payments.Exception.InvalidDiscountCodeException;
import tedxlcu.ticketing.payments.Exception.ResourceNotFoundException;
import tedxlcu.ticketing.payments.Request.CreateDiscountWindow;
import tedxlcu.ticketing.payments.Response.ValidCodeResponse;
import tedxlcu.ticketing.payments.model.DiscountWindow;
import tedxlcu.ticketing.payments.model.User;
import tedxlcu.ticketing.payments.repository.DiscountRepository;
import tedxlcu.ticketing.payments.repository.UserRepository;

@Service
@RequiredArgsConstructor 
public class DiscountService implements IDiscountService {
  private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final SecureRandom rnd = new SecureRandom();
  
  private final DiscountRepository discountRepository;
  private final UserRepository userRepository;
  
  private String genCode() {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) sb.append(ALPHANUM.charAt(rnd.nextInt(ALPHANUM.length())));
    return sb.toString();
  }

  private void doesCodeExist(String code) {
    boolean itExists = discountRepository.findAll().stream()
      .anyMatch(discount -> discount.getCode().equals(code));
    if(itExists) {
      throw new AlreadyExistsException("code exists already");
    }
  }

  @Override
  public DiscountWindow createWindow(CreateDiscountWindow window, String userId) {
    User user = userRepository.findById(userId).orElseThrow(
      () -> new ResourceNotFoundException("User not found")
    );
    doesCodeExist(window.getDiscountCode());
    DiscountWindow newDiscountWindow = new DiscountWindow();
    if(window.getStartDate() != null && window.getEndDate() != null && 
      window.getEndDate().isBefore(window.getStartDate())
    ) {
      throw new IllegalArgumentException("End date must be after start date");
    }
    // newDiscountWindow.setCode(uniqueCode);
    newDiscountWindow.setUsageLimit(window.getUsageLimit());
    newDiscountWindow.setTimesUsed(0);
    newDiscountWindow.setStartDate(window.getStartDate());
    newDiscountWindow.setPercentage(window.getPercentage());
    newDiscountWindow.setEndDate(window.getEndDate());
    newDiscountWindow.setCode(window.getDiscountCode());
    newDiscountWindow.setDiscountName(window.getDiscountName());
    newDiscountWindow.setCreatedBy(user.getFirstName() + " " + user.getLastName());
    return discountRepository.save(newDiscountWindow);
  }

  @Override
  public void deleteWindow(String id) {
    discountRepository.deleteById(id);
  }

  @Override
  public void refreshCode(String id, String userId) {
    User user = userRepository.findById(userId).orElseThrow(
      () -> new ResourceNotFoundException("User not found")
    );
    DiscountWindow w = discountRepository.findById(id).orElseThrow(
      () -> new InvalidDiscountCodeException("Invalid discount code")
    );
    String newCode;
    do { 
      newCode = genCode();
    } while (
      discountRepository.findByCode(newCode).isPresent()
    );
    w.setCode(newCode);
    w.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
    discountRepository.save(w);
  }

  @Override
  public Optional<DiscountWindow> findByCode(String code) {
    return discountRepository.findByCode(code);
  }

  @Override
  public List<DiscountWindow> listOpenWindows() {
    LocalDateTime now = LocalDateTime.now();
    return discountRepository.findAll().stream().filter(
      window -> window.isWindowOpen(now)
    ).toList();
  }

  @Override
  public ValidCodeResponse validateCode(String code) {
    DiscountWindow w = discountRepository.findByCode(code).orElseThrow(
      () -> new InvalidDiscountCodeException("Invalid discount code")
    );
    boolean isExpired = w.getTimesUsed() == w.getUsageLimit();
    if(isExpired){
      throw new DiscountExpiredException("Discount code usage limit reached");
    }
    LocalDateTime now = LocalDateTime.now();
    if (!w.isWindowOpen(now)) throw new DiscountExpiredException("Discount is not open");
    if (w.getStartDate() != null && now.isBefore(w.getStartDate())) throw new DiscountExpiredException("Discount not yet active");
    if (w.getEndDate() != null && now.isAfter(w.getEndDate())) throw new DiscountExpiredException("Discount expired");
    
    return new ValidCodeResponse(true, w.getPercentage(), w.getEndDate());
  }

  @Override
  public DiscountWindow returnValidCode(String code) {
    DiscountWindow w = discountRepository.findByCode(code).orElseThrow(
      () -> new InvalidDiscountCodeException("Invalid discount code")
    );
    boolean isExpired = w.getTimesUsed() == w.getUsageLimit();
    if(isExpired){
      throw new DiscountExpiredException("Discount code usage limit reached");
    }
    LocalDateTime now = LocalDateTime.now();
    if (!w.isWindowOpen(now)) throw new DiscountExpiredException("Discount is not open");
    if (w.getStartDate() != null && now.isBefore(w.getStartDate())) throw new DiscountExpiredException("Discount not yet active");
    if (w.getEndDate() != null && now.isAfter(w.getEndDate())) throw new DiscountExpiredException("Discount expired");

    return w;
  }
}
