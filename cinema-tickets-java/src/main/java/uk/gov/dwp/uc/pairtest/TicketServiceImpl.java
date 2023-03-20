package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

     private TicketPaymentService ticketPaymentService;
     private SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService ) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (accountId <= 0) throw new InvalidPurchaseException();

        if(!containsAdult(ticketTypeRequests)) throw new InvalidPurchaseException();

        if(!containsEnoughAdults(ticketTypeRequests)) throw new InvalidPurchaseException();

        ticketPaymentService.makePayment(accountId, 20);
        seatReservationService.reserveSeat(accountId, 1);
    }

    private boolean containsAdult(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.asList(ticketTypeRequests).stream().anyMatch(ticket -> ticket.getTicketType().equals(TicketTypeRequest.Type.ADULT));
    }

    private boolean containsEnoughAdults(TicketTypeRequest[] ticketTypeRequests) {
        long numberOfAdults = Arrays.asList(ticketTypeRequests).stream().filter(ticket -> ticket.getTicketType().equals(TicketTypeRequest.Type.ADULT)).count();
        long numberOfInfants = Arrays.asList(ticketTypeRequests).stream().filter(ticket -> ticket.getTicketType().equals(TicketTypeRequest.Type.INFANT)).count();
        return numberOfAdults >= numberOfInfants;
    }

}
