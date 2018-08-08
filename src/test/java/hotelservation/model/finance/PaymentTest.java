package hotelservation.model.finance;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import hotelreservation.model.Charge;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.finance.Payment;

public class PaymentTest {

	private Payment payment;

	@Before
	public void setup() {
		payment = new Payment();
	}

	@Test
	public void testGetSubTotal() {
		ReservationCharge chargeOne = new ReservationCharge();
		chargeOne.setCharge(new Charge(Currency.CZK, 3000, "charge", "charge"));
		chargeOne.setQuantity(1);
		payment.setReservationCharges(Arrays.asList(chargeOne));

		assertEquals(3000, payment.getSubTotal());

		chargeOne.setCharge(new Charge(Currency.CZK, 3000, "charge", "charge"));
		chargeOne.setQuantity(5);
		assertEquals(15000, payment.getSubTotal());

		chargeOne.setCharge(new Charge(Currency.CZK, 100, "charge", "charge"));
		chargeOne.setQuantity(7);
		assertEquals(700, payment.getSubTotal());
	}

	@Test
	public void testGetSubTotaManyChargesl() {
		payment.setReservationCharges(Arrays.asList(new ReservationCharge(new Charge(Currency.CZK, 50, "", ""), 2), new ReservationCharge(new Charge(Currency.CZK, 150, "", ""), 3),
				new ReservationCharge(new Charge(Currency.CZK, 170, "", ""), 1), new ReservationCharge(new Charge(Currency.CZK, 599, "", ""), 1),
				new ReservationCharge(new Charge(Currency.CZK, 999, "", ""), 2), new ReservationCharge(new Charge(Currency.CZK, 3000, "", ""), 2),
				new ReservationCharge(new Charge(Currency.CZK, 10000, "", ""), 3)));

		assertEquals(39317, payment.getSubTotal());
	}
}