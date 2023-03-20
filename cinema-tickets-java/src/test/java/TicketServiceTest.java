import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.mock.*;


import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {
    
    @Mock
    TicketPaymentService mockTicketPaymentService;

    @Mock
    SeatReservationService mockSeatReservationService;


    @Test(expected = InvalidPurchaseException.class)
    public void accountIdLessThanZero() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

         TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)};

        ticketService.purchaseTickets(0L, tickets);

    } 

    @Test()
    public void accountIdGreaterThanZero() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

         TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)};

        ticketService.purchaseTickets(1L, tickets);

        verify(mockSeatReservationService, times(1)).reserveSeat(1L, 1);
        verify(mockTicketPaymentService, times(1)).makePayment(1L, 20);

    } 

    @Test(expected = InvalidPurchaseException.class)
    public void throwErrorIfOnlyChildren() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

         TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1), new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)};

        ticketService.purchaseTickets(1L, tickets);
    } 

    @Test(expected = InvalidPurchaseException.class)
    public void throwErrorIfOnlyInfants() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

         TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)};

        ticketService.purchaseTickets(1L, tickets);
    } 

    @Test(expected = InvalidPurchaseException.class)
    public void throwErrorIfOnlyInfantsAndChildren() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

         TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)};

        ticketService.purchaseTickets(1L, tickets);
    } 

    @Test(expected = InvalidPurchaseException.class)
    public void throwErrorIfNotEnoughAdults() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

         TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1),new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)};

        ticketService.purchaseTickets(1L, tickets);
    } 

}
