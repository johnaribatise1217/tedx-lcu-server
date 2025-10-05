package tedxlcu.ticketing.payments.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import tedxlcu.ticketing.payments.model.TicketBooking;

@Service
@RequiredArgsConstructor
public class EmailService {
  @Autowired
  private final JavaMailSender mailSender;

  public void sendNewAccountMail(String to, String firstName, String lastName, String email, String password, String loginUrl) throws Exception{
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(to);
    helper.setSubject("TEDx Lead City University New Account Created");

    //Html template
    String html = """
              <html>
              <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                  <div style="max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                      <h1 style="color: #FF0000; text-align: center;">TEDx Lead City University</h1>
                      <p style="color: #555;">Hello %s %s,</p>
                      <p style="color: #555;">Your admin account has been created successfully. Below are your login details:</p>
                      <ul style="color: #555;">
                          <li><strong>Email:</strong> %s</li>
                          <li><strong>Password:</strong> %s</li>
                          <li><strong>Login URL:</strong> <a href="%s">%s</a></li>
                      </ul>
                      <p style="color: #555;">Please log in and change your password immediately for security purposes.</p>
                      <p style="color: #555;">Best regards,<br/>TEDx Lead City University Team</p>
                      <p style="text-align: center; color: #888; font-size: 12px;">&copy; TEDx Lead City University</p>
                  </div>
              </body>
              </html>
              """.formatted(firstName, lastName, email, password, loginUrl, loginUrl);

        helper.setText(html, true);
        mailSender.send(message);
  }

  public void sendTicketMail(TicketBooking booking) throws Exception{
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(booking.getEmail());
    helper.setSubject("TEDx Lead City University Ticket Confirmation");

    String qrBase64 = null;
    String qrUrl = booking.getQrCodeUrl();
    String bookingId = booking.getId();
    if (qrUrl == null || qrUrl.isBlank()) {
      System.err.println("EmailService: booking QR URL is missing for booking id=" + String.valueOf(bookingId));
    } else {
      try {
        qrBase64 = generateQRBase64(qrUrl);
      } catch (WriterException | IOException e) {
        System.err.println("EmailService: failed to generate QR for url=" + qrUrl + " error=" + e.getMessage());
        qrBase64 = null;
      }
    }

    String imageSection;
    if (qrBase64 != null) {
      imageSection = "<img src=\"data:image/png;base64," + qrBase64 + "\" alt=\"QR Code\" style=\"width: 200px; height: 200px;\"/>";
    } else if (bookingId != null && !bookingId.isBlank()) {
      imageSection = "<p><a href=\"/admin/verify/" + bookingId + "\">Click here to view your ticket</a></p>";
    } else {
      imageSection = "<p>QR unavailable. Please open your admin dashboard to view your ticket.</p>";
    }

    //Html template
    String html = """
              <html>
              <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                  <div style="max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                      <h1 style="color: #FF0000; text-align: center;">TEDx Lead City University</h1>
                      <p style="color: #555; text-align: center;">Thank you for registering! Your ticket is confirmed.</p>
                      <div style="text-align: center; margin: 20px 0;">
                          <h2 style="color: #007bff;">%s %s</h2>
                          <p><strong>Ticket Type:</strong> %s</p>
                          %s
                          <p>Scan this QR at the event for verification.</p>
                      </div>
                      <p style="color: #555;">If you do not see any QR code attached , another one will be generated for you at the event.</p>
                      <p style="color: #555;">Event Details: November 7, 2025 Lead City University Conference Center. See you there!</p>
                      <p style="text-align: center; color: #888; font-size: 12px;">&copy; TEDx Lead City University</p>
                  </div>
              </body>
              </html>
              """.formatted(booking.getFirstName(), booking.getLastName(), booking.getTicketName(), imageSection);

    helper.setText(html, true);
    mailSender.send(message);
  }

  private String generateQRBase64(String url) throws WriterException, IOException {
    QRCodeWriter writer = new QRCodeWriter();
    BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 200, 200);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }
}