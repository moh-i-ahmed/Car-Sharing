package springData.domain;

import org.junit.Before;
import org.junit.Test;

import springData.domain.Car;
import springData.domain.Invoice;
import springData.domain.Payment;
import springData.domain.PaymentType;
import springData.domain.Request;
import springData.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PaymentTypeTest {

    private PaymentType paymentTypeUnderTest;

    @Before
    public void setUp() {
        paymentTypeUnderTest = new PaymentType();
    }

    @Test
    public void testGetPayments() {
        // Setup

        // Run the test
       // final List<Payment> result = paymentTypeUnderTest.getPayments();

        // Verify the results
    }

    @Test
    public void testAddPayment() {
        // Setup
        final Payment payment = new Payment();
        payment.setPaymentID(0);
        payment.setPaymentDate(LocalDate.of(2017, 1, 1));
        payment.setPaymentAmount(0.0);
        final Invoice invoice = new Invoice();
        invoice.setInvoiceID(0);
        invoice.setInvoiceDate(LocalDate.of(2017, 1, 1));
        invoice.setInvoiceTime(LocalTime.of(12, 0, 0));
        invoice.setInvoiceStatus("invoiceStatus");
        final Request request = new Request();
        request.setRequestID(0);
        request.setRequestDate(LocalDate.of(2017, 1, 1));
        request.setStartTime(LocalTime.of(12, 0, 0));
        request.setEndTime(LocalTime.of(12, 0, 0));
        request.setLatitude("latitude");
        request.setLongitude("longitude");
        request.setCar(new Car("registrationNumber", "carColor", "carName", false));
        request.setInvoice(new Invoice());
        request.setUser(new User());
        request.setStatus("status");
        invoice.setRequest(request);
        invoice.setPayment(new Payment());
        payment.setInvoice(invoice);
        payment.setPaymentType(new PaymentType());

        // Run the test
        paymentTypeUnderTest.addPayment(payment);

        // Verify the results
    }

    @Test
    public void testRemovePayment() {
        // Setup
        final Payment payment = new Payment();
        payment.setPaymentID(0);
        payment.setPaymentDate(LocalDate.of(2017, 1, 1));
        payment.setPaymentAmount(0.0);
        final Invoice invoice = new Invoice();
        invoice.setInvoiceID(0);
        invoice.setInvoiceDate(LocalDate.of(2017, 1, 1));
        invoice.setInvoiceTime(LocalTime.of(12, 0, 0));
        invoice.setInvoiceStatus("invoiceStatus");
        final Request request = new Request();
        request.setRequestID(0);
        request.setRequestDate(LocalDate.of(2017, 1, 1));
        request.setStartTime(LocalTime.of(12, 0, 0));
        request.setEndTime(LocalTime.of(12, 0, 0));
        request.setLatitude("latitude");
        request.setLongitude("longitude");
        request.setCar(new Car("registrationNumber", "carColor", "carName", false));
        request.setInvoice(new Invoice());
        request.setUser(new User());
        request.setStatus("status");
        invoice.setRequest(request);
        invoice.setPayment(new Payment());
        payment.setInvoice(invoice);
        payment.setPaymentType(new PaymentType());

        // Run the test
        paymentTypeUnderTest.removePayment(payment);

        // Verify the results
    }
}
