import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    @Test(expected = InvalidPurchaseException.class)
    public void ContainsNonPostiveIntegerTickets() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

        TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1),new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)};

        ticketService.purchaseTickets(1L, tickets);
    } 

    @Test(expected = InvalidPurchaseException.class)
    public void emptyArray() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

        TicketTypeRequest[] tickets = {};

        ticketService.purchaseTickets(1L, tickets);
    } 

    @Test(expected = InvalidPurchaseException.class)
    public void nullParameter() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

        TicketTypeRequest[] tickets = null;

        ticketService.purchaseTickets(1L, tickets);
    } 

    @Test(expected = InvalidPurchaseException.class)
    public void nullTests() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

        TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
            null,
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)
        };


        ticketService.purchaseTickets(1L, tickets);
    } 

    @Test()
    public void returnCorrectTicketPriceAndSeatReservation() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

         TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
        };

        ticketService.purchaseTickets(1L, tickets);

        verify(mockSeatReservationService, times(1)).reserveSeat(1L, 8);
        verify(mockTicketPaymentService, times(1)).makePayment(1L, 110);

    } 

    @Test()
    public void returnCorrectTicketPriceAndSeatReservationIgnoringInfants() {

        TicketService ticketService = new TicketServiceImpl(mockTicketPaymentService, mockSeatReservationService);

         TicketTypeRequest[] tickets = {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)
        };

        ticketService.purchaseTickets(1L, tickets);

        verify(mockSeatReservationService, times(1)).reserveSeat(1L, 8);
        verify(mockTicketPaymentService, times(1)).makePayment(1L, 110);

    } 

    
}
