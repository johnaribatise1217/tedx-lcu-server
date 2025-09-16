package tedxlcu.ticketing.payments.service.Discount;

import java.util.List;
import java.util.Optional;

import tedxlcu.ticketing.payments.Request.CreateDiscountWindow;
import tedxlcu.ticketing.payments.model.DiscountWindow;

public interface IDiscountService {
  DiscountWindow createWindow(CreateDiscountWindow window, String userId);
  void deleteWindow(String id);
  void refreshCode(String id, String userId);
  Optional<DiscountWindow> findByCode(String code);
  List<DiscountWindow> listOpenWindows();
  DiscountWindow validateCode(String code);
}